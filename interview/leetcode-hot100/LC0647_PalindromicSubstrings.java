public class LC0647_PalindromicSubstrings {

    public int countSubstrings(String s) {
        int n = s.length();
        int ans = 0;
        for (int center = 0; center < 2 * n - 1; center++) {
            int l = center / 2;
            int r = l + center % 2;
            while (l >= 0 && r < n && s.charAt(l) == s.charAt(r)) {
                ans++;
                l--;
                r++;
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        LC0647_PalindromicSubstrings solver = new LC0647_PalindromicSubstrings();
        System.out.println(solver.countSubstrings("aaa"));
    }
}
