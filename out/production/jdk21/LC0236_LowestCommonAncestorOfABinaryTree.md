# LC0236 二叉树的最近公共祖先（Lowest Common Ancestor of a Binary Tree）

## 题目大意
给定一棵二叉树，以及其中的两个节点 `p`、`q`，求它们的最近公共祖先（LCA）。

## 思路（自底向上的递归）
- 对于当前节点 `root`：
  - 若 `root` 为空，或等于 `p` / `q`，直接返回 `root`；
  - 递归求左子树 `left = LCA(root.left, p, q)`；
  - 递归求右子树 `right = LCA(root.right, p, q)`；
- 根据返回结果分情况：
  - 若 `left` 和 `right` 都非空，说明 `p`、`q` 分别在左右子树，`root` 即为 LCA；
  - 若只有一边非空，则说明 LCA 在该子树中，返回该子树返回的节点；
  - 若两边都空，则返回 `null`。

## 复杂度
- 时间复杂度：O(n)。
- 空间复杂度：O(h)。

## 考点
- LCA 经典递归写法。
- 理解“自底向上返回带信息的节点”的思想。
