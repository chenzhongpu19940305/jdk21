# LC0309 最佳买卖股票时机含冷冻期（Best Time to Buy and Sell Stock with Cooldown）

## 题目大意
给定股票价格 `prices`，可以多次交易，但卖出股票后第二天不能立即买入（有一天冷冻期），求最大收益。

## 思路（状态机 DP）
- 定义三种状态：
  - `hold`：当前持有股票的最大收益；
  - `sold`：今天刚卖出股票的最大收益；
  - `rest`：当前不持股且不在冷冻期（可买）的最大收益。
- 转移：
  - `newHold = max(hold, rest - price)`（继续持有 or 今天买入）；
  - `newSold = hold + price`（今天卖出）;
  - `newRest = max(rest, sold)`（继续休息 or 从冷冻期恢复）。
- 初始：
  - `hold = -prices[0]`, `sold = 0`, `rest = 0`。
- 答案：`max(sold, rest)`。

## 复杂度
- 时间复杂度：O(n)。
- 空间复杂度：O(1)。

## 考点
- 股票问题的状态机建模。
- 冷冻期（隔天才能买）如何用状态表达。
