public class LC0309_BestTimeToBuyAndSellStockWithCooldown {

    public int maxProfit(int[] prices) {
        if (prices.length == 0) {
            return 0;
        }
        int n = prices.length;
        int hold = -prices[0];
        int sold = 0;
        int rest = 0;
        for (int i = 1; i < n; i++) {
            int newHold = java.lang.Math.max(hold, rest - prices[i]);
            int newSold = hold + prices[i];
            int newRest = java.lang.Math.max(rest, sold);
            hold = newHold;
            sold = newSold;
            rest = newRest;
        }
        return java.lang.Math.max(sold, rest);
    }

    public static void main(String[] args) {
        LC0309_BestTimeToBuyAndSellStockWithCooldown solver = new LC0309_BestTimeToBuyAndSellStockWithCooldown();
        System.out.println(solver.maxProfit(new int[]{1,2,3,0,2}));
    }
}
