public class LC0001_TwoSum {

    public int[] twoSum(int[] nums, int target) {
        java.util.Map<Integer, Integer> map = new java.util.HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            map.put(nums[i], i);
        }
        return new int[0];
    }

    public static void main(String[] args) {
        LC0001_TwoSum solver = new LC0001_TwoSum();
        int[] res = solver.twoSum(new int[]{2, 7, 11, 15}, 9);
        System.out.println(java.util.Arrays.toString(res));
    }
}
