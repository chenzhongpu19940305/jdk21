public class LC0494_TargetSum {

    public int findTargetSumWays(int[] nums, int target) {
        int sum = 0;
        for (int x : nums) {
            sum += x;
        }
        if ((sum + target) % 2 != 0 || Math.abs(target) > sum) {
            return 0;
        }
        int pos = (sum + target) / 2;
        int[] dp = new int[pos + 1];
        dp[0] = 1;
        for (int num : nums) {
            for (int j = pos; j >= num; j--) {
                dp[j] += dp[j - num];
            }
        }
        return dp[pos];
    }

    public static void main(String[] args) {
        LC0494_TargetSum solver = new LC0494_TargetSum();
        System.out.println(solver.findTargetSumWays(new int[]{1,1,1,1,1}, 3));
    }
}
