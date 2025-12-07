public class LC0105_ConstructBinaryTreeFromPreorderAndInorderTraversal {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) { val = x; }
    }

    private java.util.Map<Integer, Integer> indexMap;

    public TreeNode buildTree(int[] preorder, int[] inorder) {
        indexMap = new java.util.HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            indexMap.put(inorder[i], i);
        }
        return build(preorder, 0, preorder.length - 1, 0, inorder.length - 1);
    }

    private TreeNode build(int[] preorder, int preL, int preR,
                           int inL, int inR) {
        if (preL > preR) {
            return null;
        }
        int rootVal = preorder[preL];
        TreeNode root = new TreeNode(rootVal);
        int inIndex = indexMap.get(rootVal);
        int leftSize = inIndex - inL;
        root.left = build(preorder, preL + 1, preL + leftSize, inL, inIndex - 1);
        root.right = build(preorder, preL + leftSize + 1, preR, inIndex + 1, inR);
        return root;
    }

    public static void main(String[] args) {
        LC0105_ConstructBinaryTreeFromPreorderAndInorderTraversal solver =
                new LC0105_ConstructBinaryTreeFromPreorderAndInorderTraversal();
        int[] preorder = {3,9,20,15,7};
        int[] inorder = {9,3,15,20,7};
        TreeNode root = solver.buildTree(preorder, inorder);
        System.out.println(root.val);
    }
}
