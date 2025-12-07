# LC0206 反转链表（Reverse Linked List）

## 题目大意
反转一个单链表，返回反转后的头结点。

## 思路（迭代指针翻转）
- 维护三个指针：`prev`、`cur`、`next`：
  - 初始 `prev = null`, `cur = head`；
  - 循环：
    - 暂存 `next = cur.next`；
    - 反转指向：`cur.next = prev`；
    - `prev = cur`, `cur = next`；
- 循环结束时 `prev` 即为新链表头结点。

## 复杂度
- 时间复杂度：O(n)。
- 空间复杂度：O(1)。

## 考点
- 链表指针操作的基本功。
