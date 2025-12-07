public class LC0017_LetterCombinationsOfAPhoneNumber {

    private static final String[] MAP = new String[]{
            "",    "",    "abc",  "def",
            "ghi", "jkl", "mno",  "pqrs",
            "tuv", "wxyz"
    };

    public java.util.List<String> letterCombinations(String digits) {
        java.util.List<String> res = new java.util.ArrayList<>();
        if (digits == null || digits.length() == 0) {
            return res;
        }
        backtrack(digits, 0, new StringBuilder(), res);
        return res;
    }

    private void backtrack(String digits, int idx, StringBuilder path, java.util.List<String> res) {
        if (idx == digits.length()) {
            res.add(path.toString());
            return;
        }
        char c = digits.charAt(idx);
        String letters = MAP[c - '0'];
        for (int i = 0; i < letters.length(); i++) {
            path.append(letters.charAt(i));
            backtrack(digits, idx + 1, path, res);
            path.deleteCharAt(path.length() - 1);
        }
    }

    public static void main(String[] args) {
        LC0017_LetterCombinationsOfAPhoneNumber solver = new LC0017_LetterCombinationsOfAPhoneNumber();
        System.out.println(solver.letterCombinations("23"));
    }
}
