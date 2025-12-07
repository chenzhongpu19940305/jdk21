public class LC0152_MaximumProductSubarray {

    public int maxProduct(int[] nums) {
        int max = nums[0];
        int min = nums[0];
        int ans = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int x = nums[i];
            if (x < 0) {
                int tmp = max;
                max = min;
                min = tmp;
            }
            max = java.lang.Math.max(x, max * x);
            min = java.lang.Math.min(x, min * x);
            if (max > ans) {
                ans = max;
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        LC0152_MaximumProductSubarray solver = new LC0152_MaximumProductSubarray();
        System.out.println(solver.maxProduct(new int[]{2,3,-2,4}));
    }
}
