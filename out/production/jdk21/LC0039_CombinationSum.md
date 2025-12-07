# LC0039 组合总和（Combination Sum）

## 题目大意
给定候选数组 `candidates`（无重复）和目标值 `target`，找出所有和为 `target` 的组合。每个数可以被选取多次，顺序不同视为同一组合。

## 思路（回溯 + 剪枝）
- 对 `candidates` 排序，方便剪枝。
- 回溯函数参数：
  - 当前起始下标 `start`（保证组合不重复、顺序无关）。
  - 当前剩余和 `target`。
  - 当前路径 `path`。
- 递归过程：
  - 若 `target == 0`，记录当前路径。
  - 遍历下标 `i` 从 `start` 开始：
    - 若 `candidates[i] > target`，后面更大，直接剪枝。
    - 选择 `candidates[i]`，递归时 `start` 仍为 `i`，表示可以重复选当前数。

## 复杂度
- 时间复杂度：与解的数量有关，通常为指数级。
- 空间复杂度：O(target / min(candidates)) 的递归深度。

## 考点
- 回溯搜索空间设计。
- 通过排序实现剪枝。
