public class LC0076_MinimumWindowSubstring {

    public String minWindow(String s, String t) {
        if (s.length() < t.length()) {
            return "";
        }
        int[] need = new int[128];
        int required = 0;
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            if (need[c] == 0) {
                required++;
            }
            need[c]++;
        }
        int[] window = new int[128];
        int formed = 0;
        int left = 0;
        int bestLen = Integer.MAX_VALUE;
        int bestL = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            window[c]++;
            if (need[c] > 0 && window[c] == need[c]) {
                formed++;
            }
            while (formed == required && left <= right) {
                if (right - left + 1 < bestLen) {
                    bestLen = right - left + 1;
                    bestL = left;
                }
                char lc = s.charAt(left);
                window[lc]--;
                if (need[lc] > 0 && window[lc] < need[lc]) {
                    formed--;
                }
                left++;
            }
        }
        return bestLen == Integer.MAX_VALUE ? "" : s.substring(bestL, bestL + bestLen);
    }

    public static void main(String[] args) {
        LC0076_MinimumWindowSubstring solver = new LC0076_MinimumWindowSubstring();
        System.out.println(solver.minWindow("ADOBECODEBANC", "ABC"));
    }
}
