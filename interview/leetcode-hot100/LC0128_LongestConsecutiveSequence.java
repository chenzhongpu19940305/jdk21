public class LC0128_LongestConsecutiveSequence {

    public int longestConsecutive(int[] nums) {
        java.util.Set<Integer> set = new java.util.HashSet<>();
        for (int x : nums) {
            set.add(x);
        }
        int best = 0;
        for (int x : set) {
            if (!set.contains(x - 1)) {
                int cur = x;
                int len = 1;
                while (set.contains(cur + 1)) {
                    cur++;
                    len++;
                }
                if (len > best) {
                    best = len;
                }
            }
        }
        return best;
    }

    public static void main(String[] args) {
        LC0128_LongestConsecutiveSequence solver = new LC0128_LongestConsecutiveSequence();
        System.out.println(solver.longestConsecutive(new int[]{100,4,200,1,3,2}));
    }
}
