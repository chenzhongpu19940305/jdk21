public class LC0621_TaskScheduler {

    public int leastInterval(char[] tasks, int n) {
        int[] cnt = new int[26];
        for (char c : tasks) {
            cnt[c - 'A']++;
        }
        java.util.Arrays.sort(cnt);
        int max = cnt[25];
        int same = 1;
        for (int i = 24; i >= 0; i--) {
            if (cnt[i] == max) {
                same++;
            } else {
                break;
            }
        }
        int partCount = max - 1;
        int partLen = n + 1;
        int minLen = partCount * partLen + same;
        return java.lang.Math.max(minLen, tasks.length);
    }

    public static void main(String[] args) {
        LC0621_TaskScheduler solver = new LC0621_TaskScheduler();
        System.out.println(solver.leastInterval(new char[]{'A','A','A','B','B','B'}, 2));
    }
}
