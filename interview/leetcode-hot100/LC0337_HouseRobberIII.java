public class LC0337_HouseRobberIII {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) { val = x; }
    }

    public int rob(TreeNode root) {
        int[] res = dfs(root);
        return java.lang.Math.max(res[0], res[1]);
    }

    private int[] dfs(TreeNode node) {
        if (node == null) {
            return new int[]{0, 0};
        }
        int[] left = dfs(node.left);
        int[] right = dfs(node.right);
        int robThis = node.val + left[0] + right[0];
        int notRobThis = java.lang.Math.max(left[0], left[1]) + java.lang.Math.max(right[0], right[1]);
        return new int[]{notRobThis, robThis};
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.right = new TreeNode(3);
        root.right.right = new TreeNode(1);
        LC0337_HouseRobberIII solver = new LC0337_HouseRobberIII();
        System.out.println(solver.rob(root));
    }
}
