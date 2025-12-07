public class LC0394_DecodeString {

    public String decodeString(String s) {
        java.util.Deque<Integer> countStack = new java.util.ArrayDeque<>();
        java.util.Deque<StringBuilder> strStack = new java.util.ArrayDeque<>();
        StringBuilder cur = new StringBuilder();
        int k = 0;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch >= '0' && ch <= '9') {
                k = k * 10 + (ch - '0');
            } else if (ch == '[') {
                countStack.push(k);
                strStack.push(cur);
                k = 0;
                cur = new StringBuilder();
            } else if (ch == ']') {
                int times = countStack.pop();
                StringBuilder prev = strStack.pop();
                for (int t = 0; t < times; t++) {
                    prev.append(cur);
                }
                cur = prev;
            } else {
                cur.append(ch);
            }
        }
        return cur.toString();
    }

    public static void main(String[] args) {
        LC0394_DecodeString solver = new LC0394_DecodeString();
        System.out.println(solver.decodeString("3[a2[c]]"));
    }
}
