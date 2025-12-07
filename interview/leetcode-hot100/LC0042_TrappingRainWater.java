public class LC0042_TrappingRainWater {

    public int trap(int[] height) {
        int n = height.length;
        if (n == 0) {
            return 0;
        }
        int left = 0;
        int right = n - 1;
        int leftMax = 0;
        int rightMax = 0;
        int res = 0;
        while (left < right) {
            if (height[left] < height[right]) {
                if (height[left] >= leftMax) {
                    leftMax = height[left];
                } else {
                    res += leftMax - height[left];
                }
                left++;
            } else {
                if (height[right] >= rightMax) {
                    rightMax = height[right];
                } else {
                    res += rightMax - height[right];
                }
                right--;
            }
        }
        return res;
    }

    public static void main(String[] args) {
        LC0042_TrappingRainWater solver = new LC0042_TrappingRainWater();
        System.out.println(solver.trap(new int[]{0,1,0,2,1,0,1,3,2,1,2,1}));
    }
}
