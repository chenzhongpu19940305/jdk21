public class LC0096_UniqueBinarySearchTrees {

    public int numTrees(int n) {
        int[] dp = new int[n + 1];
        dp[0] = 1;
        dp[1] = 1;
        for (int i = 2; i <= n; i++) {
            int sum = 0;
            for (int root = 1; root <= i; root++) {
                sum += dp[root - 1] * dp[i - root];
            }
            dp[i] = sum;
        }
        return dp[n];
    }

    public static void main(String[] args) {
        LC0096_UniqueBinarySearchTrees solver = new LC0096_UniqueBinarySearchTrees();
        System.out.println(solver.numTrees(3));
    }
}
