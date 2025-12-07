public class LC0461_HammingDistance {

    public int hammingDistance(int x, int y) {
        int z = x ^ y;
        int cnt = 0;
        while (z != 0) {
            z &= (z - 1);
            cnt++;
        }
        return cnt;
    }

    public static void main(String[] args) {
        LC0461_HammingDistance solver = new LC0461_HammingDistance();
        System.out.println(solver.hammingDistance(1, 4));
    }
}
