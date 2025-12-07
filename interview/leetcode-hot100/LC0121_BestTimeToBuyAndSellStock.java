public class LC0121_BestTimeToBuyAndSellStock {

    public int maxProfit(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;
        for (int p : prices) {
            if (p < minPrice) {
                minPrice = p;
            } else if (p - minPrice > maxProfit) {
                maxProfit = p - minPrice;
            }
        }
        return maxProfit;
    }

    public static void main(String[] args) {
        LC0121_BestTimeToBuyAndSellStock solver = new LC0121_BestTimeToBuyAndSellStock();
        System.out.println(solver.maxProfit(new int[]{7,1,5,3,6,4}));
    }
}
