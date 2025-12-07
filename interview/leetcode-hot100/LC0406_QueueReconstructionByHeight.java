public class LC0406_QueueReconstructionByHeight {

    public int[][] reconstructQueue(int[][] people) {
        java.util.Arrays.sort(people, (a, b) -> {
            if (a[0] != b[0]) {
                return b[0] - a[0];
            } else {
                return a[1] - b[1];
            }
        });
        java.util.List<int[]> list = new java.util.ArrayList<>();
        for (int[] p : people) {
            list.add(p[1], p);
        }
        return list.toArray(new int[people.length][]);
    }

    public static void main(String[] args) {
        LC0406_QueueReconstructionByHeight solver = new LC0406_QueueReconstructionByHeight();
        int[][] people = {{7,0},{4,4},{7,1},{5,0},{6,1},{5,2}};
        int[][] res = solver.reconstructQueue(people);
        System.out.println(java.util.Arrays.deepToString(res));
    }
}
