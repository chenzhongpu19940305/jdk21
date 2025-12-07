public class LC0215_KthLargestElementInAnArray {

    public int findKthLargest(int[] nums, int k) {
        java.util.PriorityQueue<Integer> heap = new java.util.PriorityQueue<>();
        for (int x : nums) {
            heap.offer(x);
            if (heap.size() > k) {
                heap.poll();
            }
        }
        return heap.peek();
    }

    public static void main(String[] args) {
        LC0215_KthLargestElementInAnArray solver = new LC0215_KthLargestElementInAnArray();
        System.out.println(solver.findKthLargest(new int[]{3,2,1,5,6,4}, 2));
    }
}
