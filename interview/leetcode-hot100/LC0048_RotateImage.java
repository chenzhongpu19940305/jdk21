public class LC0048_RotateImage {

    public void rotate(int[][] matrix) {
        int n = matrix.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int tmp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = tmp;
            }
        }
        for (int i = 0; i < n; i++) {
            int left = 0;
            int right = n - 1;
            while (left < right) {
                int tmp = matrix[i][left];
                matrix[i][left] = matrix[i][right];
                matrix[i][right] = tmp;
                left++;
                right--;
            }
        }
    }

    public static void main(String[] args) {
        LC0048_RotateImage solver = new LC0048_RotateImage();
        int[][] m = {{1,2,3},{4,5,6},{7,8,9}};
        solver.rotate(m);
        for (int i = 0; i < m.length; i++) {
            System.out.println(java.util.Arrays.toString(m[i]));
        }
    }
}
