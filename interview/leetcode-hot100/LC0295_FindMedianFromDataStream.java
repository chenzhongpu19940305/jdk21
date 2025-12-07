public class LC0295_FindMedianFromDataStream {

    private java.util.PriorityQueue<Integer> small;
    private java.util.PriorityQueue<Integer> large;

    public LC0295_FindMedianFromDataStream() {
        small = new java.util.PriorityQueue<>(java.util.Collections.reverseOrder());
        large = new java.util.PriorityQueue<>();
    }

    public void addNum(int num) {
        if (small.isEmpty() || num <= small.peek()) {
            small.offer(num);
        } else {
            large.offer(num);
        }
        if (small.size() > large.size() + 1) {
            large.offer(small.poll());
        } else if (large.size() > small.size()) {
            small.offer(large.poll());
        }
    }

    public double findMedian() {
        if (small.size() == large.size()) {
            return (small.peek() + large.peek()) / 2.0;
        } else {
            return small.peek();
        }
    }

    public static void main(String[] args) {
        LC0295_FindMedianFromDataStream mf = new LC0295_FindMedianFromDataStream();
        mf.addNum(1);
        mf.addNum(2);
        System.out.println(mf.findMedian());
        mf.addNum(3);
        System.out.println(mf.findMedian());
    }
}
