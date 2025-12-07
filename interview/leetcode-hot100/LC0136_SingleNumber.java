public class LC0136_SingleNumber {

    public int singleNumber(int[] nums) {
        int x = 0;
        for (int n : nums) {
            x ^= n;
        }
        return x;
    }

    public static void main(String[] args) {
        LC0136_SingleNumber solver = new LC0136_SingleNumber();
        System.out.println(solver.singleNumber(new int[]{4,1,2,1,2}));
    }
}
