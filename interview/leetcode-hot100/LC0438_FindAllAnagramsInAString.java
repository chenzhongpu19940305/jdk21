public class LC0438_FindAllAnagramsInAString {

    public java.util.List<Integer> findAnagrams(String s, String p) {
        java.util.List<Integer> res = new java.util.ArrayList<>();
        if (s.length() < p.length()) {
            return res;
        }
        int[] cntP = new int[26];
        int[] cntWin = new int[26];
        for (int i = 0; i < p.length(); i++) {
            cntP[p.charAt(i) - 'a']++;
            cntWin[s.charAt(i) - 'a']++;
        }
        if (java.util.Arrays.equals(cntP, cntWin)) {
            res.add(0);
        }
        for (int i = p.length(); i < s.length(); i++) {
            cntWin[s.charAt(i) - 'a']++;
            cntWin[s.charAt(i - p.length()) - 'a']--;
            if (java.util.Arrays.equals(cntP, cntWin)) {
                res.add(i - p.length() + 1);
            }
        }
        return res;
    }

    public static void main(String[] args) {
        LC0438_FindAllAnagramsInAString solver = new LC0438_FindAllAnagramsInAString();
        System.out.println(solver.findAnagrams("cbaebabacd", "abc"));
    }
}
