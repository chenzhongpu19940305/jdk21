public class LC0032_LongestValidParentheses {

    public int longestValidParentheses(String s) {
        int n = s.length();
        if (n < 2) {
            return 0;
        }
        int[] dp = new int[n];
        int max = 0;
        for (int i = 1; i < n; i++) {
            if (s.charAt(i) == ')') {
                if (s.charAt(i - 1) == '(') {
                    dp[i] = 2 + (i >= 2 ? dp[i - 2] : 0);
                } else {
                    int pre = i - dp[i - 1] - 1;
                    if (pre >= 0 && s.charAt(pre) == '(') {
                        dp[i] = dp[i - 1] + 2 + (pre >= 1 ? dp[pre - 1] : 0);
                    }
                }
                if (dp[i] > max) {
                    max = dp[i];
                }
            }
        }
        return max;
    }

    public static void main(String[] args) {
        LC0032_LongestValidParentheses solver = new LC0032_LongestValidParentheses();
        System.out.println(solver.longestValidParentheses("(()"));
        System.out.println(solver.longestValidParentheses(")()())"));
    }
}
