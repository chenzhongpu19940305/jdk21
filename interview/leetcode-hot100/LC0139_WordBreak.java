public class LC0139_WordBreak {

    public boolean wordBreak(String s, java.util.List<String> wordDict) {
        java.util.Set<String> set = new java.util.HashSet<>(wordDict);
        int n = s.length();
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;
        int maxLen = 0;
        for (String w : wordDict) {
            if (w.length() > maxLen) {
                maxLen = w.length();
            }
        }
        for (int i = 1; i <= n; i++) {
            for (int len = 1; len <= maxLen && len <= i; len++) {
                if (!dp[i - len]) {
                    continue;
                }
                String sub = s.substring(i - len, i);
                if (set.contains(sub)) {
                    dp[i] = true;
                    break;
                }
            }
        }
        return dp[n];
    }

    public static void main(String[] args) {
        LC0139_WordBreak solver = new LC0139_WordBreak();
        java.util.List<String> dict = java.util.Arrays.asList("leet", "code");
        System.out.println(solver.wordBreak("leetcode", dict));
    }
}
