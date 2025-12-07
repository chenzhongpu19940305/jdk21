public class LC0279_PerfectSquares {

    public int numSquares(int n) {
        int[] dp = new int[n + 1];
        java.util.Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j * j <= i; j++) {
                dp[i] = java.lang.Math.min(dp[i], dp[i - j * j] + 1);
            }
        }
        return dp[n];
    }

    public static void main(String[] args) {
        LC0279_PerfectSquares solver = new LC0279_PerfectSquares();
        System.out.println(solver.numSquares(12));
        System.out.println(solver.numSquares(13));
    }
}
