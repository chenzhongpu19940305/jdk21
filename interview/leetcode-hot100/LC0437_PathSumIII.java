public class LC0437_PathSumIII {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) { val = x; }
    }

    public int pathSum(TreeNode root, int targetSum) {
        java.util.Map<Long, Integer> prefix = new java.util.HashMap<>();
        prefix.put(0L, 1);
        return dfs(root, 0L, targetSum, prefix);
    }

    private int dfs(TreeNode node, long curSum, int target, java.util.Map<Long, Integer> prefix) {
        if (node == null) {
            return 0;
        }
        curSum += node.val;
        int res = prefix.getOrDefault(curSum - target, 0);
        prefix.put(curSum, prefix.getOrDefault(curSum, 0) + 1);
        res += dfs(node.left, curSum, target, prefix);
        res += dfs(node.right, curSum, target, prefix);
        prefix.put(curSum, prefix.get(curSum) - 1);
        return res;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(10);
        root.left = new TreeNode(5);
        root.right = new TreeNode(-3);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(2);
        root.right.right = new TreeNode(11);
        root.left.left.left = new TreeNode(3);
        root.left.left.right = new TreeNode(-2);
        root.left.right.right = new TreeNode(1);
        LC0437_PathSumIII solver = new LC0437_PathSumIII();
        System.out.println(solver.pathSum(root, 8));
    }
}
