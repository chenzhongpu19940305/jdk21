public class LC0338_CountingBits {

    public int[] countBits(int n) {
        int[] res = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            res[i] = res[i >> 1] + (i & 1);
        }
        return res;
    }

    public static void main(String[] args) {
        LC0338_CountingBits solver = new LC0338_CountingBits();
        int[] res = solver.countBits(5);
        System.out.println(java.util.Arrays.toString(res));
    }
}
