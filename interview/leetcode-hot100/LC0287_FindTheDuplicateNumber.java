public class LC0287_FindTheDuplicateNumber {

    public int findDuplicate(int[] nums) {
        int slow = nums[0];
        int fast = nums[nums[0]];
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[nums[fast]];
        }
        slow = 0;
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[fast];
        }
        return slow;
    }

    public static void main(String[] args) {
        LC0287_FindTheDuplicateNumber solver = new LC0287_FindTheDuplicateNumber();
        System.out.println(solver.findDuplicate(new int[]{1,3,4,2,2}));
    }
}
