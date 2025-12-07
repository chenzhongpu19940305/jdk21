# LC0208 实现 Trie (前缀树)（Implement Trie (Prefix Tree)）

## 题目大意
实现一个前缀树 Trie，支持：
- `insert(word)` 插入单词；
- `search(word)` 查询完整单词是否存在；
- `startsWith(prefix)` 查询是否存在以 `prefix` 为前缀的单词。

## 思路（多叉字典树）
- 每个节点存：
  - `children[26]`：子节点指针数组，对应 26 个小写字母；
  - `isEnd`：该节点是否对应某个单词结尾。
- `insert`：从根开始，逐字符向下走，没有则新建节点，最后标记 `isEnd = true`。
- `search`：沿着字符走到最后一个字符对应节点，且要求 `isEnd = true`。
- `startsWith`：与 `search` 类似，但只需判断路径是否存在即可。

## 复杂度
- 设单词长度为 L：
- 每次插入/查询时间复杂度：O(L)。
- 空间复杂度：与插入单词总长度之和成正比。

## 考点
- Trie / 前缀树结构定义与基本操作。
- 数组实现多叉树子节点。
