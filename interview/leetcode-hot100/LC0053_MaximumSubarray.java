public class LC0053_MaximumSubarray {

    public int maxSubArray(int[] nums) {
        int maxSoFar = nums[0];
        int cur = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (cur < 0) {
                cur = nums[i];
            } else {
                cur += nums[i];
            }
            if (cur > maxSoFar) {
                maxSoFar = cur;
            }
        }
        return maxSoFar;
    }

    public static void main(String[] args) {
        LC0053_MaximumSubarray solver = new LC0053_MaximumSubarray();
        System.out.println(solver.maxSubArray(new int[]{-2,1,-3,4,-1,2,1,-5,4}));
    }
}
