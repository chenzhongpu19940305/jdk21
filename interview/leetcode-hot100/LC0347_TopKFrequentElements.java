public class LC0347_TopKFrequentElements {

    public int[] topKFrequent(int[] nums, int k) {
        java.util.Map<Integer, Integer> freq = new java.util.HashMap<>();
        for (int x : nums) {
            freq.put(x, freq.getOrDefault(x, 0) + 1);
        }
        java.util.PriorityQueue<int[]> heap = new java.util.PriorityQueue<>(
                (a, b) -> a[1] - b[1]
        );
        for (java.util.Map.Entry<Integer, Integer> e : freq.entrySet()) {
            heap.offer(new int[]{e.getKey(), e.getValue()});
            if (heap.size() > k) {
                heap.poll();
            }
        }
        int[] res = new int[k];
        for (int i = k - 1; i >= 0; i--) {
            res[i] = heap.poll()[0];
        }
        return res;
    }

    public static void main(String[] args) {
        LC0347_TopKFrequentElements solver = new LC0347_TopKFrequentElements();
        int[] res = solver.topKFrequent(new int[]{1,1,1,2,2,3}, 2);
        System.out.println(java.util.Arrays.toString(res));
    }
}
