public class LC0079_WordSearch {

    public boolean exist(char[][] board, String word) {
        int m = board.length;
        int n = board[0].length;
        boolean[][] visited = new boolean[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (dfs(board, word, 0, i, j, visited)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dfs(char[][] board, String word, int idx,
                        int x, int y, boolean[][] visited) {
        if (idx == word.length()) {
            return true;
        }
        if (x < 0 || x >= board.length || y < 0 || y >= board[0].length) {
            return false;
        }
        if (visited[x][y]) {
            return false;
        }
        if (board[x][y] != word.charAt(idx)) {
            return false;
        }
        visited[x][y] = true;
        int[] dx = {1,-1,0,0};
        int[] dy = {0,0,1,-1};
        for (int k = 0; k < 4; k++) {
            if (dfs(board, word, idx + 1, x + dx[k], y + dy[k], visited)) {
                visited[x][y] = false;
                return true;
            }
        }
        visited[x][y] = false;
        return false;
    }

    public static void main(String[] args) {
        LC0079_WordSearch solver = new LC0079_WordSearch();
        char[][] board = {
                {'A','B','C','E'},
                {'A','F','C','S'},
                {'A','D','E','E'}
        };
        System.out.println(solver.exist(board, "ABCCED"));
    }
}
