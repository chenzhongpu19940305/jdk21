# LC0042 接雨水（Trapping Rain Water）

## 题目大意
给出一个非负整数数组表示柱子的高度，求能接多少雨水。

## 思路（双指针 + 局部最高）
- 关键结论：位置 `i` 能接的水为 `min(leftMax[i], rightMax[i]) - height[i]`。
- 用双指针从两端向中间：
  - 维护 `leftMax`、`rightMax`。
  - 若 `height[left] < height[right]`：
    - 此时左边的 `leftMax` 决定 `left` 处水量：
      - 若 `height[left] >= leftMax`，更新 `leftMax`。
      - 否则累加 `leftMax - height[left]`。
    - 左指针右移。
  - 否则对称处理右指针。

## 复杂度
- 时间复杂度：O(n)。
- 空间复杂度：O(1)。

## 考点
- 对接雨水问题的数学抽象。
- 双指针在空间优化中的运用。
