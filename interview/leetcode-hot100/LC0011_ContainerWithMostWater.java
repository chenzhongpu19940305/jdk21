public class LC0011_ContainerWithMostWater {

    public int maxArea(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int ans = 0;
        while (left < right) {
            int h = java.lang.Math.min(height[left], height[right]);
            ans = java.lang.Math.max(ans, h * (right - left));
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        LC0011_ContainerWithMostWater solver = new LC0011_ContainerWithMostWater();
        System.out.println(solver.maxArea(new int[]{1,8,6,2,5,4,8,3,7}));
    }
}
