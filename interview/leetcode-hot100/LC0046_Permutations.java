public class LC0046_Permutations {

    public java.util.List<java.util.List<Integer>> permute(int[] nums) {
        java.util.List<java.util.List<Integer>> res = new java.util.ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrack(nums, used, new java.util.ArrayList<>(), res);
        return res;
    }

    private void backtrack(int[] nums, boolean[] used,
                           java.util.List<Integer> path,
                           java.util.List<java.util.List<Integer>> res) {
        if (path.size() == nums.length) {
            res.add(new java.util.ArrayList<>(path));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) {
                continue;
            }
            used[i] = true;
            path.add(nums[i]);
            backtrack(nums, used, path, res);
            path.remove(path.size() - 1);
            used[i] = false;
        }
    }

    public static void main(String[] args) {
        LC0046_Permutations solver = new LC0046_Permutations();
        System.out.println(solver.permute(new int[]{1,2,3}));
    }
}
