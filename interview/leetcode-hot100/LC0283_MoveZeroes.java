public class LC0283_MoveZeroes {

    public void moveZeroes(int[] nums) {
        int slow = 0;
        for (int fast = 0; fast < nums.length; fast++) {
            if (nums[fast] != 0) {
                int tmp = nums[slow];
                nums[slow] = nums[fast];
                nums[fast] = tmp;
                slow++;
            }
        }
    }

    public static void main(String[] args) {
        LC0283_MoveZeroes solver = new LC0283_MoveZeroes();
        int[] nums = {0,1,0,3,12};
        solver.moveZeroes(nums);
        System.out.println(java.util.Arrays.toString(nums));
    }
}
