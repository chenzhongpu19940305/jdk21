public class LC0056_MergeIntervals {

    public int[][] merge(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new int[0][0];
        }
        java.util.Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
        java.util.List<int[]> res = new java.util.ArrayList<>();
        int[] cur = intervals[0];
        for (int i = 1; i < intervals.length; i++) {
            int[] next = intervals[i];
            if (next[0] <= cur[1]) {
                cur[1] = java.lang.Math.max(cur[1], next[1]);
            } else {
                res.add(cur);
                cur = next;
            }
        }
        res.add(cur);
        return res.toArray(new int[res.size()][]);
    }

    public static void main(String[] args) {
        LC0056_MergeIntervals solver = new LC0056_MergeIntervals();
        int[][] ans = solver.merge(new int[][]{{1,3},{2,6},{8,10},{15,18}});
        for (int[] it : ans) {
            System.out.println(java.util.Arrays.toString(it));
        }
    }
}
