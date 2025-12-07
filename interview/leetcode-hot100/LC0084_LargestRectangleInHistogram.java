public class LC0084_LargestRectangleInHistogram {

    public int largestRectangleArea(int[] heights) {
        int n = heights.length;
        java.util.Deque<Integer> stack = new java.util.ArrayDeque<>();
        int max = 0;
        for (int i = 0; i <= n; i++) {
            int h = (i == n) ? 0 : heights[i];
            while (!stack.isEmpty() && h < heights[stack.peek()]) {
                int height = heights[stack.pop()];
                int right = i;
                int left = stack.isEmpty() ? -1 : stack.peek();
                int width = right - left - 1;
                max = java.lang.Math.max(max, height * width);
            }
            stack.push(i);
        }
        return max;
    }

    public static void main(String[] args) {
        LC0084_LargestRectangleInHistogram solver = new LC0084_LargestRectangleInHistogram();
        System.out.println(solver.largestRectangleArea(new int[]{2,1,5,6,2,3}));
    }
}
