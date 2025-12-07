# LC0114 二叉树展开为链表（Flatten Binary Tree to Linked List）

## 题目大意
将一棵二叉树原地展开为一个“链表”，要求：
- 使用右指针指向下一个节点；
- 左指针全部置为 null；
- 展开顺序为 **先序遍历** 顺序。

## 思路（原地修改 + 找前驱）
- 迭代地处理当前节点 `cur`：
  - 若 `cur.left == null`，则直接 `cur = cur.right`。
  - 若有左子树：
    1. 在 `cur.left` 中找到最右侧节点 `pre`（相当于先序遍历中左子树的最后一个节点）。
    2. 将 `pre.right` 指向 `cur.right`。
    3. 将 `cur.right` 指向 `cur.left`，并置 `cur.left = null`。
    4. `cur = cur.right` 继续。

## 复杂度
- 时间复杂度：O(n)。
- 空间复杂度：O(1)。

## 考点
- 原地修改二叉树结构。
- 与 Morris 遍历思想相似的“前驱节点”技巧。
