public class LC0010_RegularExpressionMatching {

    public boolean isMatch(String s, String p) {
        int m = s.length();
        int n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;
        for (int j = 2; j <= n; j++) {
            if (p.charAt(j - 1) == '*' && dp[0][j - 2]) {
                dp[0][j] = true;
            }
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char pc = p.charAt(j - 1);
                if (pc == '*') {
                    dp[i][j] |= dp[i][j - 2];
                    char pre = p.charAt(j - 2);
                    if (pre == '.' || pre == s.charAt(i - 1)) {
                        dp[i][j] |= dp[i - 1][j];
                    }
                } else if (pc == '.' || pc == s.charAt(i - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
            }
        }
        return dp[m][n];
    }

    public static void main(String[] args) {
        LC0010_RegularExpressionMatching solver = new LC0010_RegularExpressionMatching();
        System.out.println(solver.isMatch("aa", "a"));
        System.out.println(solver.isMatch("aa", "a*"));
        System.out.println(solver.isMatch("ab", ".*"));
    }
}
