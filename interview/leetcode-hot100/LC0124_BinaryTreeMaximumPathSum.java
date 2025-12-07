public class LC0124_BinaryTreeMaximumPathSum {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) { val = x; }
    }

    private int maxSum;

    public int maxPathSum(TreeNode root) {
        maxSum = Integer.MIN_VALUE;
        dfs(root);
        return maxSum;
    }

    private int dfs(TreeNode node) {
        if (node == null) {
            return 0;
        }
        int leftGain = java.lang.Math.max(0, dfs(node.left));
        int rightGain = java.lang.Math.max(0, dfs(node.right));
        int priceNewPath = node.val + leftGain + rightGain;
        if (priceNewPath > maxSum) {
            maxSum = priceNewPath;
        }
        return node.val + java.lang.Math.max(leftGain, rightGain);
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(-10);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);
        LC0124_BinaryTreeMaximumPathSum solver = new LC0124_BinaryTreeMaximumPathSum();
        System.out.println(solver.maxPathSum(root));
    }
}
