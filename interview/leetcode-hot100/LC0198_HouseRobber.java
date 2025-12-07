public class LC0198_HouseRobber {

    public int rob(int[] nums) {
        int n = nums.length;
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return nums[0];
        }
        int prev2 = nums[0];
        int prev1 = java.lang.Math.max(nums[0], nums[1]);
        for (int i = 2; i < n; i++) {
            int cur = java.lang.Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    public static void main(String[] args) {
        LC0198_HouseRobber solver = new LC0198_HouseRobber();
        System.out.println(solver.rob(new int[]{1,2,3,1}));
    }
}
