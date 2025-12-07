public class LC0209_MinimumSizeSubarraySum {

    public int minSubArrayLen(int target, int[] nums) {
        int n = nums.length;
        int left = 0;
        int sum = 0;
        int ans = Integer.MAX_VALUE;
        for (int right = 0; right < n; right++) {
            sum += nums[right];
            while (sum >= target) {
                ans = java.lang.Math.min(ans, right - left + 1);
                sum -= nums[left++];
            }
        }
        return ans == Integer.MAX_VALUE ? 0 : ans;
    }

    public static void main(String[] args) {
        LC0209_MinimumSizeSubarraySum solver = new LC0209_MinimumSizeSubarraySum();
        System.out.println(solver.minSubArrayLen(7, new int[]{2,3,1,2,4,3}));
    }
}
