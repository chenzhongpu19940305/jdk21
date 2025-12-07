public class LC0448_FindAllNumbersDisappearedInAnArray {

    public java.util.List<Integer> findDisappearedNumbers(int[] nums) {
        java.util.List<Integer> res = new java.util.ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            int idx = java.lang.Math.abs(nums[i]) - 1;
            if (nums[idx] > 0) {
                nums[idx] = -nums[idx];
            }
        }
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > 0) {
                res.add(i + 1);
            }
        }
        return res;
    }

    public static void main(String[] args) {
        LC0448_FindAllNumbersDisappearedInAnArray solver = new LC0448_FindAllNumbersDisappearedInAnArray();
        System.out.println(solver.findDisappearedNumbers(new int[]{4,3,2,7,8,2,3,1}));
    }
}
