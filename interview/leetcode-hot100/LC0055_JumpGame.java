public class LC0055_JumpGame {

    public boolean canJump(int[] nums) {
        int farthest = 0;
        for (int i = 0; i <= farthest && i < nums.length; i++) {
            farthest = java.lang.Math.max(farthest, i + nums[i]);
            if (farthest >= nums.length - 1) {
                return true;
            }
        }
        return nums.length == 1;
    }

    public static void main(String[] args) {
        LC0055_JumpGame solver = new LC0055_JumpGame();
        System.out.println(solver.canJump(new int[]{2,3,1,1,4}));
        System.out.println(solver.canJump(new int[]{3,2,1,0,4}));
    }
}
