public class LC0070_ClimbingStairs {

    public int climbStairs(int n) {
        if (n <= 2) {
            return n;
        }
        int a = 1;
        int b = 2;
        for (int i = 3; i <= n; i++) {
            int c = a + b;
            a = b;
            b = c;
        }
        return b;
    }

    public static void main(String[] args) {
        LC0070_ClimbingStairs solver = new LC0070_ClimbingStairs();
        System.out.println(solver.climbStairs(3));
    }
}
