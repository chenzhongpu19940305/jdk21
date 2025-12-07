public class LC0739_DailyTemperatures {

    public int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] res = new int[n];
        java.util.Deque<Integer> stack = new java.util.ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            int t = temperatures[i];
            while (!stack.isEmpty() && temperatures[stack.peek()] < t) {
                int idx = stack.pop();
                res[idx] = i - idx;
            }
            stack.push(i);
        }
        return res;
    }

    public static void main(String[] args) {
        LC0739_DailyTemperatures solver = new LC0739_DailyTemperatures();
        int[] ans = solver.dailyTemperatures(new int[]{73,74,75,71,69,72,76,73});
        System.out.println(java.util.Arrays.toString(ans));
    }
}

