public class LC0003_LongestSubstringWithoutRepeatingCharacters {

    public int lengthOfLongestSubstring(String s) {
        int[] index = new int[128];
        java.util.Arrays.fill(index, -1);
        int left = 0;
        int res = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (index[c] >= left) {
                left = index[c] + 1;
            }
            index[c] = right;
            res = java.lang.Math.max(res, right - left + 1);
        }
        return res;
    }

    public static void main(String[] args) {
        LC0003_LongestSubstringWithoutRepeatingCharacters solver =
                new LC0003_LongestSubstringWithoutRepeatingCharacters();
        System.out.println(solver.lengthOfLongestSubstring("abcabcbb"));
        System.out.println(solver.lengthOfLongestSubstring("bbbbb"));
        System.out.println(solver.lengthOfLongestSubstring("pwwkew"));
    }
}
