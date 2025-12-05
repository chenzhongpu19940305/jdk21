# Java基础知识面试题

## 3. 两个对象的hashCode()相同，则equals()也一定为true，对吗？

**回答要点：**
- 答案：**不对**
- hashCode()和equals()的关系：
  - 如果两个对象equals()返回true，则hashCode()必须相同
  - 如果两个对象hashCode()相同，equals()不一定为true
  - 这是Java对象的约定（contract）
- hashCode()的作用：
  - 用于哈希表（HashMap、HashSet等）的快速查找
  - 减少equals()的调用次数
  - 提升性能
- 哈希冲突：
  - 不同对象可能有相同的hashCode()
  - 这称为哈希冲突
  - HashMap使用链表或红黑树处理冲突
- 正确的实现方式：
  - 重写equals()时必须重写hashCode()
  - 保证：equals()相同 => hashCode()相同
  - 不要求：hashCode()相同 => equals()相同
- 代码示例：
  ```java
  class Person {
      private String name;
      private int age;
      
      @Override
      public boolean equals(Object obj) {
          if (this == obj) return true;
          if (!(obj instanceof Person)) return false;
          Person p = (Person) obj;
          return name.equals(p.name) && age == p.age;
      }
      
      @Override
      public int hashCode() {
          return Objects.hash(name, age);
      }
  }
  ```

---

## 4. final在java中有什么作用？

**回答要点：**
- final的三种用法：
  - **修饰类**：
    - 该类不能被继承
    - 例如：String、Integer等
    - 优点：安全、性能优化
  - **修饰方法**：
    - 该方法不能被重写
    - 子类不能覆盖该方法
    - 用于保护关键方法
  - **修饰变量**：
    - 该变量只能赋值一次
    - 赋值后不能修改
    - 必须在声明时或构造函数中初始化
- final修饰变量的细节：
  - **基本类型**：值不能改变
  - **引用类型**：引用不能改变，但对象内容可以改变
  - 示例：
    ```java
    final int x = 10;
    x = 20;  // 编译错误
    
    final List<String> list = new ArrayList<>();
    list.add("a");  // 可以，修改对象内容
    list = new ArrayList<>();  // 编译错误，不能改变引用
    ```
- final的性能优化：
  - JVM可以进行内联优化
  - 减少虚方法调用
  - 提升性能
- final的安全性：
  - 防止意外修改
  - 明确表达设计意图
  - 支持多线程安全（final字段的可见性）

---

## 5. JAVA中的Math.round(-1.5)等于多少？

**回答要点：**
- 答案：**-1**
- Math.round()的工作原理：
  - 返回最接近的整数
  - 如果恰好在两个整数之间，向上舍入
  - 向上舍入是指向正无穷方向
- 具体计算：
  - Math.round(-1.5)
  - -1.5在-2和-1之间
  - 向正无穷方向舍入 => -1
- 其他例子：
  - Math.round(1.5) = 2（向正无穷）
  - Math.round(2.5) = 3
  - Math.round(-2.5) = -2（向正无穷）
  - Math.round(0.5) = 1
  - Math.round(-0.5) = 0
- 实现原理：
  ```java
  public static long round(double a) {
      if (a != 0x1.fffffffffffffp-2) { // 不是0.5
          return (long)Math.floor(a + 0.5);
      } else {
          return 0L;
      }
  }
  ```
- 与其他舍入方式的区别：
  - Math.floor()：向下舍入（负无穷）
  - Math.ceil()：向上舍入（正无穷）
  - Math.round()：四舍五入（向正无穷）

---

## 6. String属于基础的数据类型吗？

**回答要点：**
- 答案：**不属于**
- Java的基本数据类型：
  - **数值类型**：
    - 整数：byte、short、int、long
    - 浮点：float、double
  - **字符类型**：char
  - **布尔类型**：boolean
  - 共8种基本类型
- String的特点：
  - 引用类型（Reference Type）
  - 属于java.lang包
  - 不可变（Immutable）
  - 在常量池中存储
- String vs 基本类型：
  - 基本类型：存储在栈上，值传递
  - String：存储在堆上，引用传递
  - 基本类型：固定大小
  - String：大小可变
- String的特殊性：
  - 虽然是引用类型，但有基本类型的特性
  - 字符串字面量存储在常量池
  - 支持+操作符（语法糖）
  - 不可变性保证线程安全

---

## 7. java中操作字符串都有哪些类？它们之间有什么区别？

**回答要点：**
- Java字符串相关的类：
  - **String**：
    - 不可变字符串
    - 线程安全
    - 性能：频繁修改时性能差
    - 适用：字符串不经常改变
  - **StringBuilder**：
    - 可变字符串
    - 非线程安全
    - 性能：高效，推荐使用
    - 适用：单线程环境下频繁修改
  - **StringBuffer**：
    - 可变字符串
    - 线程安全（synchronized）
    - 性能：比StringBuilder慢
    - 适用：多线程环境下频繁修改
  - **StringJoiner**（Java 8+）：
    - 用于拼接字符串
    - 支持分隔符和前后缀
    - 性能：高效
  - **CharSequence**：
    - 接口，定义字符序列的通用方法
    - String、StringBuilder、StringBuffer都实现了该接口
- 性能对比：
  ```
  String + String + String  // 最慢，产生多个中间对象
  StringBuilder.append()     // 最快
  StringBuffer.append()      // 中等，有同步开销
  ```
- 使用建议：
  - 字符串不改变：使用String
  - 单线程频繁修改：使用StringBuilder
  - 多线程频繁修改：使用StringBuffer
  - 拼接多个字符串：使用StringBuilder或StringJoiner
- 内部实现：
  - String：char[]数组（Java 9+使用byte[]）
  - StringBuilder/StringBuffer：char[]数组，动态扩容

---

## 8. String str="i"与String str=new String("i")一样吗？

**回答要点：**
- 答案：**不一样**
- 创建方式的区别：
  - **String str="i"**：
    - 字符串字面量
    - 存储在字符串常量池中
    - 如果常量池中已存在"i"，则直接引用
    - 只创建一个对象（或零个）
  - **String str=new String("i")**：
    - 显式创建新对象
    - 在堆上创建新对象
    - 同时在常量池中创建"i"（如果不存在）
    - 创建两个对象（或一个）
- 内存分布：
  ```
  String str1 = "i";           // 常量池中的"i"
  String str2 = "i";           // 指向常量池中的"i"
  String str3 = new String("i"); // 堆上的新对象
  
  str1 == str2  // true，指向同一对象
  str1 == str3  // false，不同对象
  str1.equals(str3)  // true，内容相同
  ```
- 性能影响：
  - 字面量方式更高效
  - 避免不必要的对象创建
  - 字符串常量池的优化
- 字符串常量池：
  - JVM维护的字符串缓存
  - 存储所有字符串字面量
  - 支持intern()方法手动加入
  - Java 7+：常量池移到堆上

---

## 9. 如何将字符串中反转？

**回答要点：**
- 方法一：使用StringBuilder的reverse()
  ```java
  String str = "hello";
  String reversed = new StringBuilder(str).reverse().toString();
  // 结果：olleh
  ```
- 方法二：使用char数组
  ```java
  String str = "hello";
  char[] chars = str.toCharArray();
  for (int i = 0; i < chars.length / 2; i++) {
      char temp = chars[i];
      chars[i] = chars[chars.length - 1 - i];
      chars[chars.length - 1 - i] = temp;
  }
  String reversed = new String(chars);
  ```
- 方法三：递归
  ```java
  public static String reverse(String str) {
      if (str.length() <= 1) {
          return str;
      }
      return reverse(str.substring(1)) + str.charAt(0);
  }
  ```
- 方法四：使用Stream（Java 8+）
  ```java
  String str = "hello";
  String reversed = Stream.of(str.split(""))
      .reduce("", (s1, s2) -> s2 + s1);
  ```
- 性能对比：
  - StringBuilder.reverse()：最快，推荐使用
  - char数组：性能好，易于理解
  - 递归：性能差，易导致栈溢出
  - Stream：性能一般，代码简洁

---

## 10. String类的常用方法都有哪些？

**回答要点：**
- 字符串查询方法：
  - `length()`：返回字符串长度
  - `charAt(int index)`：返回指定位置的字符
  - `indexOf(String str)`：返回子字符串首次出现的位置
  - `lastIndexOf(String str)`：返回子字符串最后出现的位置
  - `contains(String str)`：判断是否包含子字符串
  - `startsWith(String prefix)`：判断是否以指定前缀开始
  - `endsWith(String suffix)`：判断是否以指定后缀结束
- 字符串提取方法：
  - `substring(int beginIndex)`：提取从指定位置到末尾的子字符串
  - `substring(int beginIndex, int endIndex)`：提取指定范围的子字符串
  - `split(String regex)`：按正则表达式分割字符串
  - `toCharArray()`：转换为字符数组
- 字符串转换方法：
  - `toLowerCase()`：转换为小写
  - `toUpperCase()`：转换为大写
  - `trim()`：去除首尾空格
  - `replace(char oldChar, char newChar)`：替换字符
  - `replaceAll(String regex, String replacement)`：按正则表达式替换
  - `replaceFirst(String regex, String replacement)`：替换首次匹配
- 字符串比较方法：
  - `equals(Object obj)`：比较内容是否相同
  - `equalsIgnoreCase(String another)`：忽略大小写比较
  - `compareTo(String another)`：字典序比较
  - `compareToIgnoreCase(String another)`：忽略大小写的字典序比较
- 字符串匹配方法：
  - `matches(String regex)`：判断是否匹配正则表达式
- 其他方法：
  - `concat(String str)`：拼接字符串
  - `repeat(int count)`：重复字符串（Java 11+）
  - `strip()`：去除首尾空格（Java 11+）
  - `isBlank()`：判断是否为空或仅包含空格（Java 11+）
  - `isEmpty()`：判断是否为空字符串

---

## 11. 抽象类必须要有抽象方法吗？

**回答要点：**
- 答案：**不一定**
- 抽象类的定义：
  - 使用abstract关键字修饰的类
  - 不能被实例化
  - 可以有构造函数
  - 可以有普通方法和抽象方法
- 抽象类的特点：
  - **可以没有抽象方法**：
    - 只是为了防止被实例化
    - 子类仍需继承该类
  - **可以有多个抽象方法**：
    - 子类必须实现所有抽象方法
  - **可以有普通方法**：
    - 子类可以继承使用
  - **可以有成员变量**：
    - 可以是static、final等
  - **可以有构造函数**：
    - 用于初始化成员变量
- 代码示例：
  ```java
  // 没有抽象方法的抽象类
  abstract class Base {
      public void print() {
          System.out.println("Hello");
      }
  }
  
  // 有抽象方法的抽象类
  abstract class Animal {
      abstract void eat();
      
      public void sleep() {
          System.out.println("Sleeping");
      }
  }
  ```
- 使用场景：
  - 为了防止类被实例化
  - 定义通用的实现
  - 强制子类实现某些方法

---

## 12. 普通类和抽象类有哪些区别？

**回答要点：**
- 定义方式：
  - 普通类：class关键字
  - 抽象类：abstract class关键字
- 实例化：
  - 普通类：可以直接new实例化
  - 抽象类：不能直接实例化，必须通过子类
- 方法：
  - 普通类：只能有普通方法
  - 抽象类：可以有普通方法和抽象方法
- 继承：
  - 普通类：可以被继承
  - 抽象类：必须被继承（否则无意义）
- 子类实现：
  - 普通类的子类：可以不实现任何方法
  - 抽象类的子类：必须实现所有抽象方法（除非子类也是抽象类）
- 构造函数：
  - 普通类：可以有构造函数
  - 抽象类：可以有构造函数（用于初始化）
- 成员变量：
  - 普通类：可以是任何访问修饰符
  - 抽象类：可以是任何访问修饰符
- 使用场景：
  - 普通类：具体的实现
  - 抽象类：定义通用接口和部分实现
- 对比表：

| 特性 | 普通类 | 抽象类 |
|------|--------|--------|
| 实例化 | 可以 | 不可以 |
| 抽象方法 | 不能有 | 可以有 |
| 普通方法 | 可以有 | 可以有 |
| 继承 | 可以 | 必须 |
| 构造函数 | 可以有 | 可以有 |

---

## 13. 抽象类能使用final修饰吗？

**回答要点：**
- 答案：**不能**
- 原因：
  - final修饰类表示该类不能被继承
  - abstract修饰类表示该类必须被继承
  - 两者是矛盾的
  - 编译器会报错
- 代码示例：
  ```java
  abstract final class Base {  // 编译错误
      abstract void method();
  }
  ```
- 类似的矛盾：
  - abstract不能修饰方法的实现体
  - abstract方法不能有方法体
  - abstract方法必须在抽象类中
- final的其他限制：
  - final类不能被继承
  - final方法不能被重写
  - final变量不能被修改

---

## 14. 接口和抽象类有什么区别？

**回答要点：**
- 定义方式：
  - 接口：interface关键字
  - 抽象类：abstract class关键字
- 实现方式：
  - 接口：implements关键字
  - 抽象类：extends关键字
- 多继承：
  - 接口：一个类可以实现多个接口
  - 抽象类：一个类只能继承一个抽象类
- 方法：
  - 接口：默认是抽象方法（Java 8+可以有default方法）
  - 抽象类：可以有普通方法和抽象方法
- 成员变量：
  - 接口：只能是public static final
  - 抽象类：可以是任何访问修饰符
- 构造函数：
  - 接口：不能有构造函数
  - 抽象类：可以有构造函数
- 访问修饰符：
  - 接口：方法默认是public
  - 抽象类：方法可以是任何修饰符
- 使用场景：
  - 接口：定义规范和契约
  - 抽象类：定义通用实现和部分实现
- 对比表：

| 特性 | 接口 | 抽象类 |
|------|------|--------|
| 多继承 | 支持 | 不支持 |
| 构造函数 | 不能有 | 可以有 |
| 普通方法 | 不能有（Java 8+可以） | 可以有 |
| 成员变量 | public static final | 任意 |
| 实现方式 | implements | extends |
| 方法访问修饰符 | public | 任意 |

---

## 15. java中IO流分为几种？

**回答要点：**
- 按数据流向分：
  - **输入流（Input Stream）**：从源读取数据
  - **输出流（Output Stream）**：向目标写入数据
- 按处理数据单位分：
  - **字节流**：处理字节数据
    - InputStream：输入字节流
    - OutputStream：输出字节流
  - **字符流**：处理字符数据
    - Reader：输入字符流
    - Writer：输出字符流
- 按功能分：
  - **节点流**：直接操作数据源
    - FileInputStream、FileOutputStream
    - FileReader、FileWriter
  - **处理流**：对节点流进行包装
    - BufferedInputStream、BufferedOutputStream
    - BufferedReader、BufferedWriter
- 常见的IO流类：
  - **字节流**：
    - FileInputStream/FileOutputStream
    - BufferedInputStream/BufferedOutputStream
    - DataInputStream/DataOutputStream
    - ObjectInputStream/ObjectOutputStream
  - **字符流**：
    - FileReader/FileWriter
    - BufferedReader/BufferedWriter
    - InputStreamReader/OutputStreamWriter
  - **特殊流**：
    - PrintStream：打印流
    - PrintWriter：打印流
    - PipedInputStream/PipedOutputStream：管道流
- 选择流的原则：
  - 处理文本数据：使用字符流
  - 处理二进制数据：使用字节流
  - 需要缓冲：使用BufferedXxx
  - 需要转换编码：使用InputStreamReader/OutputStreamWriter

---

## 16. BIO、NIO、AIO有什么区别？

**回答要点：**
- **BIO（Blocking IO）**：
  - 阻塞式IO
  - 一个线程处理一个连接
  - 线程阻塞等待数据
  - 适用：连接数少的场景
  - 优点：编程简单
  - 缺点：性能低，线程开销大
- **NIO（Non-blocking IO）**：
  - 非阻塞式IO
  - 一个线程处理多个连接
  - 使用Selector多路复用
  - 适用：连接数多的场景
  - 优点：性能高，资源利用率高
  - 缺点：编程复杂
- **AIO（Asynchronous IO）**：
  - 异步IO
  - 基于事件和回调
  - 操作系统负责IO操作
  - 适用：高并发场景
  - 优点：性能最高，编程相对简单
  - 缺点：系统支持不一致
- 对比表：

| 特性 | BIO | NIO | AIO |
|------|-----|-----|-----|
| 阻塞方式 | 阻塞 | 非阻塞 | 异步 |
| 线程数 | 多 | 少 | 少 |
| 并发能力 | 低 | 高 | 最高 |
| 编程复杂度 | 简单 | 复杂 | 中等 |
| 适用场景 | 少连接 | 多连接 | 高并发 |

- 使用建议：
  - 连接数少且固定：使用BIO
  - 连接数多且不稳定：使用NIO
  - 高并发场景：使用AIO或NIO

---

## 17. Files的常用方法都有哪些？

**回答要点：**
- 文件检查方法：
  - `exists(Path path)`：检查文件是否存在
  - `isDirectory(Path path)`：检查是否为目录
  - `isRegularFile(Path path)`：检查是否为普通文件
  - `isSymbolicLink(Path path)`：检查是否为符号链接
  - `isReadable(Path path)`：检查是否可读
  - `isWritable(Path path)`：检查是否可写
  - `isExecutable(Path path)`：检查是否可执行
- 文件操作方法：
  - `createFile(Path path)`：创建文件
  - `createDirectory(Path path)`：创建目录
  - `createDirectories(Path path)`：创建目录（包括父目录）
  - `delete(Path path)`：删除文件或目录
  - `deleteIfExists(Path path)`：如果存在则删除
  - `copy(Path source, Path target)`：复制文件
  - `move(Path source, Path target)`：移动文件
- 文件读写方法：
  - `readAllBytes(Path path)`：读取所有字节
  - `readAllLines(Path path)`：读取所有行
  - `readString(Path path)`：读取字符串（Java 11+）
  - `write(Path path, byte[] bytes)`：写入字节
  - `writeString(Path path, String string)`：写入字符串（Java 11+）
- 文件属性方法：
  - `size(Path path)`：获取文件大小
  - `getLastModifiedTime(Path path)`：获取最后修改时间
  - `getOwner(Path path)`：获取文件所有者
  - `getPosixFilePermissions(Path path)`：获取文件权限
- 目录遍历方法：
  - `list(Path dir)`：列出目录中的文件
  - `walk(Path start)`：递归遍历目录
  - `find(Path start, int maxDepth, BiPredicate filter)`：查找文件
- 其他方法：
  - `isSameFile(Path path1, Path path2)`：判断是否为同一文件
  - `getFileStore(Path path)`：获取文件存储信息
  - `probeContentType(Path path)`：探测文件类型

---

## 18. 重写和重载的区别

**回答要点：**
- **重载（Overload）**：
  - 同一个类中
  - 方法名相同
  - 参数列表不同（参数个数、类型、顺序）
  - 返回值类型可以相同也可以不同
  - 访问修饰符可以不同
  - 编译时确定（静态绑定）
  - 例子：
    ```java
    public void print(int x) {}
    public void print(String x) {}
    public void print(int x, String y) {}
    ```
- **重写（Override）**：
  - 子类和父类之间
  - 方法名相同
  - 参数列表相同
  - 返回值类型相同或是父类返回值的子类
  - 访问修饰符不能更严格
  - 不能抛出更多的异常
  - 运行时确定（动态绑定）
  - 例子：
    ```java
    class Animal {
        public void eat() {}
    }
    class Dog extends Animal {
        @Override
        public void eat() {}
    }
    ```
- 对比表：

| 特性 | 重载 | 重写 |
|------|------|------|
| 位置 | 同一类 | 父子类 |
| 方法名 | 相同 | 相同 |
| 参数列表 | 不同 | 相同 |
| 返回值 | 可不同 | 相同或子类 |
| 访问修饰符 | 可不同 | 不能更严格 |
| 绑定时间 | 编译时 | 运行时 |

---

## 19. 什么是多态

**回答要点：**
- 多态的定义：
  - 同一个对象在不同情况下表现出不同的行为
  - 同一个接口被不同的对象实现
  - 一个引用变量可以指向多种类型的对象
- 多态的三个必要条件：
  - **继承**：子类继承父类
  - **重写**：子类重写父类方法
  - **向上转型**：父类引用指向子类对象
- 多态的实现方式：
  - **编译时多态（静态多态）**：
    - 方法重载
    - 在编译时确定调用哪个方法
  - **运行时多态（动态多态）**：
    - 方法重写
    - 在运行时根据对象类型确定调用哪个方法
- 代码示例：
  ```java
  class Animal {
      public void eat() {
          System.out.println("Animal eating");
      }
  }
  
  class Dog extends Animal {
      @Override
      public void eat() {
          System.out.println("Dog eating");
      }
  }
  
  class Cat extends Animal {
      @Override
      public void eat() {
          System.out.println("Cat eating");
      }
  }
  
  public static void main(String[] args) {
      Animal animal1 = new Dog();
      Animal animal2 = new Cat();
      
      animal1.eat();  // 输出：Dog eating
      animal2.eat();  // 输出：Cat eating
  }
  ```
- 多态的优点：
  - 提高代码的灵活性和可扩展性
  - 降低代码的耦合度
  - 支持开闭原则（对扩展开放，对修改关闭）
  - 便于代码的维护和升级
- 多态的应用场景：
  - 框架设计
  - 插件系统
  - 策略模式
  - 工厂模式
  - 模板方法模式

---

## 补充资源

- 推荐阅读：
  - 《Java核心技术》
  - 《Effective Java》
  - 《Java并发编程实战》
  - Oracle Java官方文档

- 学习建议：
  - 理解Java基础概念的本质
  - 掌握String、IO流等常用类
  - 深入理解面向对象的三大特性
  - 多做练习和代码实验
