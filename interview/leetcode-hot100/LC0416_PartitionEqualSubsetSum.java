public class LC0416_PartitionEqualSubsetSum {

    public boolean canPartition(int[] nums) {
        int sum = 0;
        for (int x : nums) {
            sum += x;
        }
        if ((sum & 1) == 1) {
            return false;
        }
        int target = sum / 2;
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;
        for (int num : nums) {
            for (int j = target; j >= num; j--) {
                dp[j] = dp[j] || dp[j - num];
            }
        }
        return dp[target];
    }

    public static void main(String[] args) {
        LC0416_PartitionEqualSubsetSum solver = new LC0416_PartitionEqualSubsetSum();
        System.out.println(solver.canPartition(new int[]{1,5,11,5}));
        System.out.println(solver.canPartition(new int[]{1,2,3,5}));
    }
}
