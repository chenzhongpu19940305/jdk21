public class LC0238_ProductOfArrayExceptSelf {

    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] res = new int[n];
        int prefix = 1;
        for (int i = 0; i < n; i++) {
            res[i] = prefix;
            prefix *= nums[i];
        }
        int suffix = 1;
        for (int i = n - 1; i >= 0; i--) {
            res[i] *= suffix;
            suffix *= nums[i];
        }
        return res;
    }

    public static void main(String[] args) {
        LC0238_ProductOfArrayExceptSelf solver = new LC0238_ProductOfArrayExceptSelf();
        int[] res = solver.productExceptSelf(new int[]{1,2,3,4});
        System.out.println(java.util.Arrays.toString(res));
    }
}
