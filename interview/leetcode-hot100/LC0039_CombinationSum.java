public class LC0039_CombinationSum {

    public java.util.List<java.util.List<Integer>> combinationSum(int[] candidates, int target) {
        java.util.Arrays.sort(candidates);
        java.util.List<java.util.List<Integer>> res = new java.util.ArrayList<>();
        backtrack(candidates, target, 0, new java.util.ArrayList<>(), res);
        return res;
    }

    private void backtrack(int[] candidates, int target, int start,
                           java.util.List<Integer> path,
                           java.util.List<java.util.List<Integer>> res) {
        if (target == 0) {
            res.add(new java.util.ArrayList<>(path));
            return;
        }
        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] > target) {
                break;
            }
            path.add(candidates[i]);
            backtrack(candidates, target - candidates[i], i, path, res);
            path.remove(path.size() - 1);
        }
    }

    public static void main(String[] args) {
        LC0039_CombinationSum solver = new LC0039_CombinationSum();
        System.out.println(solver.combinationSum(new int[]{2,3,6,7}, 7));
    }
}
