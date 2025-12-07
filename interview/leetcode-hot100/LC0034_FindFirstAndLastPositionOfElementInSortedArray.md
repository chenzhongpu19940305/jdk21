# LC0034 在排序数组中查找元素的第一个和最后一个位置

## 题目大意
在一个有序数组中查找给定值 `target` 出现的起始位置和结束位置，没有则返回 `[-1, -1]`，要求时间复杂度 O(log n)。

## 思路（两次二分：lower_bound / upper_bound）
- 使用二分找到：
  - 第一个大于等于 `target` 的位置 `first`（lower_bound）。
  - 第一个大于 `target` 的位置 `lastExclusive`（upper_bound）。
- 若 `first` 越界或 `nums[first] != target`，则说明不存在。
- 否则答案为 `[first, lastExclusive - 1]`。

## 复杂度
- 时间复杂度：O(log n)。
- 空间复杂度：O(1)。

## 考点
- 二分查找模板的变体：
  - `>= target` 的第一个位置。
  - `> target` 的第一个位置。
