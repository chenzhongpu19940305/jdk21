public class LC0022_GenerateParentheses {

    public java.util.List<String> generateParenthesis(int n) {
        java.util.List<String> res = new java.util.ArrayList<>();
        backtrack(n, 0, 0, new StringBuilder(), res);
        return res;
    }

    private void backtrack(int n, int open, int close, StringBuilder path,
                           java.util.List<String> res) {
        if (path.length() == 2 * n) {
            res.add(path.toString());
            return;
        }
        if (open < n) {
            path.append('(');
            backtrack(n, open + 1, close, path, res);
            path.deleteCharAt(path.length() - 1);
        }
        if (close < open) {
            path.append(')');
            backtrack(n, open, close + 1, path, res);
            path.deleteCharAt(path.length() - 1);
        }
    }

    public static void main(String[] args) {
        LC0022_GenerateParentheses solver = new LC0022_GenerateParentheses();
        System.out.println(solver.generateParenthesis(3));
    }
}
