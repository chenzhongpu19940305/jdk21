public class LC0169_MajorityElement {

    public int majorityElement(int[] nums) {
        int cand = 0;
        int count = 0;
        for (int x : nums) {
            if (count == 0) {
                cand = x;
                count = 1;
            } else if (x == cand) {
                count++;
            } else {
                count--;
            }
        }
        return cand;
    }

    public static void main(String[] args) {
        LC0169_MajorityElement solver = new LC0169_MajorityElement();
        System.out.println(solver.majorityElement(new int[]{2,2,1,1,1,2,2}));
    }
}
