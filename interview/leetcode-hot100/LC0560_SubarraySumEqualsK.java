public class LC0560_SubarraySumEqualsK {

    public int subarraySum(int[] nums, int k) {
        java.util.Map<Integer, Integer> map = new java.util.HashMap<>();
        map.put(0, 1);
        int sum = 0;
        int ans = 0;
        for (int x : nums) {
            sum += x;
            ans += map.getOrDefault(sum - k, 0);
            map.put(sum, map.getOrDefault(sum, 0) + 1);
        }
        return ans;
    }

    public static void main(String[] args) {
        LC0560_SubarraySumEqualsK solver = new LC0560_SubarraySumEqualsK();
        System.out.println(solver.subarraySum(new int[]{1,1,1}, 2));
    }
}
