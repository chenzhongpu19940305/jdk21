public class LC0300_LongestIncreasingSubsequence {

    public int lengthOfLIS(int[] nums) {
        int n = nums.length;
        int[] tails = new int[n];
        int size = 0;
        for (int x : nums) {
            int l = 0;
            int r = size;
            while (l < r) {
                int m = (l + r) >>> 1;
                if (tails[m] < x) {
                    l = m + 1;
                } else {
                    r = m;
                }
            }
            tails[l] = x;
            if (l == size) {
                size++;
            }
        }
        return size;
    }

    public static void main(String[] args) {
        LC0300_LongestIncreasingSubsequence solver = new LC0300_LongestIncreasingSubsequence();
        System.out.println(solver.lengthOfLIS(new int[]{10,9,2,5,3,7,101,18}));
    }
}
