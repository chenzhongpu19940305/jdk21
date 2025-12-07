# RocketMQ 架构与原理

> 目标：从整体结构到 Broker 内部存储，系统地梳理 RocketMQ 的核心组件与工作流程，便于面试和系统设计时阐述。

---

## 一、整体架构与核心角色

### 1.1 总体结构图

```text
          ┌──────────────── RocketMQ 集群 ────────────────┐
          │                                               │
          │   ┌──────────────┐        ┌──────────────┐   │
          │   │  NameServer1 │  ...   │  NameServerN │   │
          │   └──────┬───────┘        └──────┬───────┘   │
          │          │ 路由同步               │           │
          │   ┌──────v───────────────────────v───────┐   │
          │   │               Broker 集群            │   │
          │   │                                      │   │
          │   │  BrokerA-M  BrokerA-S  BrokerB-M ... │   │
          │   └───────────────┬─────────────┬───────┘   │
          │                    │             │           │
          └────────────────────┼─────────────┼───────────┘
                               │             │
                         生产 / 消费      生产 / 消费
                               │             │
                      ┌────────v─┐       ┌───v────────┐
                      │Producer  │       │ Consumer   │
                      │应用集群 │       │应用集群    │
                      └──────────┘       └────────────┘
```

**主要角色：**
- **Producer（生产者）**：发送消息到 Broker。
- **Consumer（消费者）**：从 Broker 拉取/监听消息并处理。
- **NameServer**：
  - 非常轻量级的路由注册中心（无状态，可集群）。
  - 维护 Topic ⇒ Broker 的路由信息。
- **Broker**：
  - 消息存储与转发的核心节点。
  - 提供消息写入、拉取、消费进度管理、主从同步等能力。

---

## 二、NameServer 结构与职责

```text
+---------------------+         +---------------------+
|     NameServer 1    |  ...   |     NameServer N    |
+---------+-----------+         +----------+----------+
          ^                                  ^
          | 注册路由信息                      |
     +----+-------------------------------+--+
     |                                    |   
+----+-----------+                  +-----+----------+
|   Broker A    |                  |   Broker B    |
+----------------+                 +----------------+
```

- NameServer 是**无状态**的：
  - Broker 启动时向所有 NameServer 注册：自己的 IP、端口、可服务的 Topic、读写权限等。
  - Producer/Consumer 通过 NameServer 获取最新路由，但不会在其上保存会话状态。
- 路由数据：
  - TopicConfig、BrokerData、Queue 分布等。
- 故障容错：
  - NameServer 间互不通信，任何一个宕机不影响整体，可通过剩余节点继续提供路由能力。

---

## 三、Broker 内部结构

### 3.1 Broker 对外视角

```text
        ┌────────────────────────────┐
        │           Broker           │
        │                            │
        │  +----------------------+  │
        │  | Remoting Server      |  │  接收 Producer / Consumer 请求
        │  +----------------------+  │
        │  | Client Manager       |  │  管理生产者、消费者连接
        │  +----------------------+  │
        │  | HA Service           |  │  主从同步
        │  +----------------------+  │
        │  | Store Service        |  │  消息存储子系统
        │  +----------------------+  │
        └────────────────────────────┘
```

### 3.2 消息存储子系统

RocketMQ 的核心优势在于**顺序写磁盘 + 多索引结构**：

```text
Broker 存储目录示意：

$HOME/store/
  ├─ commitlog/        # 消息主体文件，顺序追加
  │    ├─ 00000000000000000000
  │    ├─ 00000000000064000000
  │    └─ ...
  ├─ consumequeue/     # 每个 topic/queue 对应一组逻辑队列
  │    └─ {topic}/{queueId}/
  │          ├─ 00000000000000000000
  │          └─ ...
  └─ index/            # IndexFile，按 key 建立倒排索引
       ├─ 20251205.index
       └─ ...
```

- **CommitLog**（消息真正存放的地方）：
  - 所有 Topic、所有 Queue 的消息都顺序写入同一系列 CommitLog 文件。
  - 采用 mmap + 顺序写，IO 吞吐高。
- **ConsumeQueue**（逻辑队列）：
  - 相当于每个 Topic 下每个 MessageQueue 的“索引文件”。
  - 记录：`CommitLogOffset`、`消息大小`、`tag hash`。
  - Consumer 实际读取 ConsumeQueue，再根据其中的 offset 去 CommitLog 取消息正文。
- **IndexFile**（可选）：
  - 提供按 key 查询消息能力（如 messageKey、traceId）。

**简化示意：**

```text
[Producer 写入]              [Broker 内部]

消息 ---> CommitLog (顺序追加)
              |
              |  根据 Topic + QueueId 构建逻辑索引
              v
        ConsumeQueue (按队列顺序索引)
```

---

## 四、Topic、MessageQueue 与 Consumer Group

### 4.1 Topic 与 MessageQueue

```text
Topic: "log-topic"

             ┌───────────────────────────┐
             │         Topic(log)        │
             ├───────────────────────────┤
             │   Queue 0   (Broker A)    │
             │   Queue 1   (Broker A)    │
             │   Queue 2   (Broker B)    │
             │   Queue 3   (Broker B)    │
             └───────────────────────────┘
```

- Topic：逻辑上的一类消息。
- MessageQueue：Topic 内部的分片（类似 Kafka partition），决定并行度与顺序语义。

### 4.2 Consumer Group 与负载均衡

```text
Consumer Group: group_log

           Queue0   Queue1   Queue2   Queue3
             |        |        |        |
    ┌────────v──┐ ┌───v────┐ ┌─v────┐ ┌─v─────┐
    │Consumer A │ │Cons B  │ │Cons C│ │Cons D │
    └───────────┘ └────────┘ └──────┘ └───────┘
```

- **集群模式（Clustering）**：
  - 同一 Group 下的多个 Consumer 会对 Topic 的队列做负载均衡。
  - 一个队列同一时刻只会被 Group 中的一个 Consumer 消费。
- **广播模式（Broadcasting）**：
  - 一个队列会被该 Group 中的所有 Consumer 消费，各自一份。

---

## 五、消息发送流程

### 5.1 时序图

```text
Producer              NameServer              Broker
   |                       |                    |
   | 1. 启动时拉取路由      |                    |
   |---------------------->|                    |
   |  返回 Topic 路由信息   |                    |
   |<----------------------|                    |
   |                       |                    |
   | 2. 按路由选择 Broker+Queue                |
   |------------------------------------------->|
   |             SEND_MESSAGE 请求              |
   |                                            |
   |                     3. 追加 CommitLog      |
   |                        & 更新 ConsumeQueue |
   |                                            |
   |<-------------------------------------------|
   |                 4. 返回发送结果            |
   |                                            |
```

**关键点：**
- Producer 先从 NameServer 获取 Topic 对应的所有 Broker + Queue 路由信息并本地缓存。
- 发送时根据负载均衡策略（轮询 / hash / 最小延迟等）选择具体队列。
- Broker 在本地：
  - 将消息顺序写入 CommitLog；
  - 异步/同步方式构建 ConsumeQueue 和 IndexFile；
  - 如配置了主从同步，还要异步将日志同步到 Slave。

---

## 六、消息消费流程

### 6.1 Pull / Push 模式（本质都是 Pull）

RocketMQ 的“PushConsumer”内部其实也是定时 Pull：

```text
Consumer 线程               Broker
    |                          |
    | 1. 拉取路由 + 订阅关系   |
    |------------------------->|
    |                          |
    | 2. PullRequest(QueueX)   |
    |------------------------->|
    |                          |
    |      返回消息列表        |
    |<-------------------------|
    |                          |
    | 3. 业务处理 + 提交 offset|
    |------------------------->|
```

- 每个 Consumer 持有自己在每个队列上的消费进度 offset。
- Offset 存储位置：
  - 广播模式：通常存本地。
  - 集群模式：一般存 Broker（或外部存储）。
- 消费失败：
  - 可配置重试次数与重试 Topic；
  - 超过阈值后进入 DLQ（死信队列）。

---

## 七、主从结构与高可用（HA）

```text
           +------------------------------+
           |          NameServer          |
           +------------------------------+
                 ^                 ^
                 | 路由信息        | 路由信息
         +-------+--------+  +----+---------+
         |   Broker A-M   |  |  Broker A-S  |
         | (Master, ID=0) |  | (Slave, ID=1)|
         +----------------+  +--------------+
                 ^                 ^
                 | 生产 / 消费      |
                 |                  |
             Producer / Consumer 应用
```

- 一个 BrokerName 下可有多个 BrokerId：0 为 Master，其余为 Slave。
- Master 接收客户端读写请求；Slave 主要作为备份，也可配置只读。
- HA 同步：
  - **同步刷盘 + 同步复制**：最安全，延迟略高。
  - **异步刷盘 + 异步复制**：吞吐更高，但可能丢少量数据。
- Master 宕机：
  - 默认不会自动“选主”，需人工调整或借助外部高可用组件（DLedger 模式可做自动主备切换）。

---

## 八、消息存储与顺序保证

### 8.1 顺序写入提高性能

- 所有消息先顺序写入 CommitLog（磁盘顺序写，配合 OS PageCache 和刷盘策略）。
- ConsumeQueue 只是逻辑索引，体积小、顺序访问快。

### 8.2 顺序消息

```text
同一业务 Key (如订单ID) ---> 固定路由到同一 Queue
Queue 内 FIFO + 单线程消费  =  全局顺序 (对该 Key)
```

- 全局顺序：Topic 只有一个队列，全量顺序，吞吐低。
- 局部顺序：按业务 key 将消息路由到固定队列，保证**同一 key 内的顺序**。

---

## 九、特殊特性概览（简要）

### 9.1 延迟消息

- 使用内部延迟队列方式实现（特殊 Topic），到期后再投递到真实 Topic。
- 支持固定的延迟级别（1s、5s、10s...）。

### 9.2 事务消息

- 二阶段提交思想：
  - Half Message 写入 Broker，但对 Consumer 不可见；
  - Producer 执行本地事务；
  - 根据事务结果 Commit / Rollback / Check；
  - Broker 定期回查不确定状态的事务。

### 9.3 消息过滤

- Tag 过滤：在 ConsumeQueue 中按 tag hash 做简单过滤。
- SQL92 过滤：在 Broker 端按属性过滤（性能略差）。

---

## 十、Topic 与 Broker / Producer / Consumer 的关系

### 10.1 Topic 与 Broker 的关系

```text
逻辑视角：

 Topic: log-topic
   ├─ Queue 0  ──>  Broker A
   ├─ Queue 1  ──>  Broker A
   ├─ Queue 2  ──>  Broker B
   └─ Queue 3  ──>  Broker C

物理视角：

 Broker A:  CommitLogA  (存 Queue0/1 的消息)
 Broker B:  CommitLogB  (存 Queue2 的消息)
 Broker C:  CommitLogC  (存 Queue3 的消息)
```

- **Topic 是逻辑概念**，Broker 是物理节点。
- 一个 Topic 可以分成多个 MessageQueue，这些队列可以**分布在多个 Broker 上**：
  - 提升吞吐（多 Broker 并行写/读）；
  - 提升可用性（某个 Broker 挂掉，只影响部分队列）。
- NameServer 维护的是：`Topic -> {BrokerName, QueueId 列表}` 的映射。

### 10.2 多个 Producer 与多个 Broker 的关系

```text
        +---------------------------+
        |       NameServer         |
        +-------------+------------+
                      |
     路由: log-topic -> [BrokerA,Q0/Q1], [BrokerB,Q2], [BrokerC,Q3]
                      |
      +---------------+---------------------------+
      |                                           |
┌─────v─────┐                               ┌─────v─────┐
│ Producer1 │                               │ Producer2 │  ...
└─────+─────┘                               └─────+─────┘
      |   根据路由和负载均衡策略选择具体 Queue   |
      +----------------+-----------+---------+
                       |           |         
                 ┌─────v───┐  ┌───v───┐  ┌──v───┐
                 │Broker A │  │BrokerB│  │BrokerC│
                 └─────────┘  └──────┘  └──────┘
```

- Producer 是**无状态**、**多实例部署**的：
  - 每个 Producer 启动时向 NameServer 拉取路由，并在内存中缓存 Topic→Queue→Broker 信息；
  - 发送每条消息时，根据**负载均衡算法**（如轮询、按 key hash、最小延迟）选择一个具体的 Queue；
  - 这个 Queue 可能在任何一个 Broker 上。
- 因此：
  - **多个 Producer 与多个 Broker 之间是多对多关系**；
  - 单个 Producer 实例可以同时与多个 Broker 建立连接；
  - 不同 Producer 也可以同时往同一个 Broker / 同一个 Queue 写数据（由 Broker 顺序写 CommitLog）。

### 10.3 多个 Consumer 与多个 Broker 的关系

以集群模式（Clustering）为例：

```text
Topic: log-topic

 Queue0  (BrokerA)  ─────────┐
 Queue1  (BrokerA)  ─────┐   │  负载均衡分配
 Queue2  (BrokerB)  ─┐   │   │
 Queue3  (BrokerC)   ─┴───┴───┘
                         |
           Consumer Group: log_group
                         |
        ┌────────────────+────────────────┐
        │                                │
   ┌────v─────┐                    ┌─────v────┐
   │Consumer1 │                    │Consumer2 │   ...
   └──────────┘                    └─────────┘
```

- NameServer 告诉 Consumer：某 Topic 的 Queue 分布在哪些 Broker 上。
- 同一 Group 下的多个 Consumer 会对所有 Queue 做**重新分配（Rebalance）**：
  - 一个 Queue 在同一时刻只会被 Group 中**一个** Consumer 实例消费；
  - 但一个 Consumer 实例可以同时处理来自**多个 Broker、多条 Queue** 的消息。
- 因此：
  - **Consumer 与 Broker 之间同样是多对多关系**；
  - 在 Queue 这一层，关系是：
    - `一个 Queue` —— `一个 Consumer 实例`（同一 Group 内）；
    - 多个 Queue 可以分摊到多个 Consumer 上，实现负载均衡与横向扩展。

---

## 十一、小结：如何在面试中高效说明 RocketMQ 架构

1. **先画大图**：Producer / Consumer / NameServer / Broker 的交互结构。
2. **再讲存储**：CommitLog + ConsumeQueue + IndexFile 的分层结构，强调“顺序写 + 索引读取”。
3. **补充高可用**：Master-Slave、HA Service 的复制方式。
4. **解释 Topic / Broker / Producer / Consumer 的多对多关系**：
   - Topic 通过多个 Queue 分布在多个 Broker 上；
   - Producer / Consumer 都是无状态集群，通过 NameServer 路由到不同 Broker；
   - 重点强调：**Queue 这一层是分片与负载均衡的核心单元**。
5. **最后点到特性**：顺序消息、延迟消息、事务消息、过滤与重试机制。

掌握以上结构和关键术语，就可以在系统设计题或者 RocketMQ 相关面试中做出清晰完整的说明。

---

## 十二、消息堆积与消费者扩容：只加消费者够吗？

### 12.1 什么情况下“加消费者”是有效的？

先看一个简单公式：

```text
持续稳定情况下：

生产速度  <=  消费速度

否则：消息总量会越来越多，形成堆积。
```

**当满足以下条件时，增加 Consumer 实例通常是有效的：**

- Topic 的 Queue 数量 **大于** 当前 Consumer 数量：
  - 例如：Topic 有 32 个 Queue，而 Group 只有 4 个 Consumer，
  - Rebalance 后可能每个 Consumer 分到 8 个 Queue，压力较大；
  - 再多加几个 Consumer，可以把 Queue 分摊给更多实例，整体吞吐提升。
- 单条消息处理逻辑耗时较长（例如写 DB/ES）：
  - 单个 Consumer CPU / IO 已经打满；
  - 增加实例相当于**多机并行跑同样处理逻辑**，可以线性提高总消费能力（前提是后端瓶颈未出现）。

### 12.2 Queue 数量带来的硬约束

在一个 Consumer Group 内：

```text
一个 Queue  同一时刻 只会被 1 个 Consumer 实例消费
```

因此：

- **可利用的最大并行度 ≈ Queue 数量**。
- 如果 Queue 只有 4 条，你起 100 个 Consumer 也没用：
  - 最多只有 4 个实例真的在干活，其余 96 个空转。
- 想通过增加 Consumer 继续扩容时，往往需要：
  - **增加 Topic 的 Queue 数量**（重新建 Topic 或迁移）；
  - 并确保这些 Queue 分布在多个 Broker 上，避免单 Broker 成为瓶颈。

### 12.3 其它常见瓶颈：不只是 MQ 本身

即使 Queue 足够、Consumer 也足够，仍然可能因为**其它组件**导致堆积：

1. **Broker / 磁盘 IO 瓶颈**
   - CommitLog 写入、PageCache 刷盘速度跟不上：
     - 磁盘性能差、刷盘策略不合理（同步刷盘 + 大量写入）。
   - Consumer 从 Broker 拉消息时延高，整体消费 QPS 上不去。

2. **网络带宽瓶颈**
   - Broker 节点出网带宽满载：
     - 多个 Consumer 从同一 Broker 拉大量消息；
     - 或 Broker 与应用之间跨机房、跨地域，RT 高、带宽紧张。

3. **下游服务（DB / ES / 外部 API）瓶颈**
   - 常见场景：
     - Consumer 需要写 MySQL / ES / 调用外部 HTTP 接口；
     - 下游 QPS / 连接数有限，开始超时、排队、报错；
   - 结果：
     - 单条消息处理耗时变长，Consumer 实例再多也被下游“卡住”；
     - 甚至触发大量重试，进一步放大流量，形成**放大效应**。

4. **应用自身的 CPU / 内存 / GC 瓶颈**
   - 复杂的业务逻辑、频繁的 JSON 序列化 / 反序列化；
   - 内存分配频繁导致 Full GC，Consumer 经常 Stop-The-World；
   - 实例 CPU 打满，线程再多也抢不到时间片。

### 12.4 面试回答思路小结

当被问到“消费者不足导致消息堆积，是不是只要加消费者就行？”时，可以这样回答：

1. **先给结论**：
   - 不一定，只加消费者有时有效，有时完全没用，要看系统的真正瓶颈在哪里。
2. **从三个层次分析瓶颈：**
   - **MQ 逻辑层**：Queue 数量是否足够？一个 Queue 同时只能被一个实例消费；
   - **MQ 物理层**：Broker 的磁盘 / 网络是否已经打满；
   - **下游依赖层**：数据库、ES、外部服务是否是实际瓶颈。
3. **给出扩容策略**：
   - 优先优化消费逻辑和下游：批量处理、异步写、缓存；
   - Queue 数足够时，再水平扩容 Consumer 实例；
   - 有必要时增加 Topic Queue 数或 Broker 数量，整体扩容 MQ 集群能力。

用这种结构化的回答方式，可以体现你不仅懂 RocketMQ 的机制，还具备用整体视角分析性能瓶颈的能力。
