public class LC0078_Subsets {

    public java.util.List<java.util.List<Integer>> subsets(int[] nums) {
        java.util.List<java.util.List<Integer>> res = new java.util.ArrayList<>();
        backtrack(nums, 0, new java.util.ArrayList<>(), res);
        return res;
    }

    private void backtrack(int[] nums, int start,
                           java.util.List<Integer> path,
                           java.util.List<java.util.List<Integer>> res) {
        res.add(new java.util.ArrayList<>(path));
        for (int i = start; i < nums.length; i++) {
            path.add(nums[i]);
            backtrack(nums, i + 1, path, res);
            path.remove(path.size() - 1);
        }
    }

    public static void main(String[] args) {
        LC0078_Subsets solver = new LC0078_Subsets();
        System.out.println(solver.subsets(new int[]{1,2,3}));
    }
}
