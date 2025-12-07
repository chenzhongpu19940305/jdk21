public class LC0538_ConvertBSTToGreaterTree {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) { val = x; }
    }

    private int sum = 0;

    public TreeNode convertBST(TreeNode root) {
        reverseInorder(root);
        return root;
    }

    private void reverseInorder(TreeNode node) {
        if (node == null) {
            return;
        }
        reverseInorder(node.right);
        sum += node.val;
        node.val = sum;
        reverseInorder(node.left);
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(2);
        root.left = new TreeNode(1);
        root.right = new TreeNode(3);
        LC0538_ConvertBSTToGreaterTree solver = new LC0538_ConvertBSTToGreaterTree();
        solver.convertBST(root);
        System.out.println(root.val + "," + root.left.val + "," + root.right.val);
    }
}
