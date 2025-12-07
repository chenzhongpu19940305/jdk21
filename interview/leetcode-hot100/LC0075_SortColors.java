public class LC0075_SortColors {

    public void sortColors(int[] nums) {
        int p0 = 0;
        int p2 = nums.length - 1;
        int i = 0;
        while (i <= p2) {
            if (nums[i] == 0) {
                int tmp = nums[i];
                nums[i] = nums[p0];
                nums[p0] = tmp;
                p0++;
                i++;
            } else if (nums[i] == 2) {
                int tmp = nums[i];
                nums[i] = nums[p2];
                nums[p2] = tmp;
                p2--;
            } else {
                i++;
            }
        }
    }

    public static void main(String[] args) {
        LC0075_SortColors solver = new LC0075_SortColors();
        int[] nums = {2,0,2,1,1,0};
        solver.sortColors(nums);
        System.out.println(java.util.Arrays.toString(nums));
    }
}
