public class LC0049_GroupAnagrams {

    public java.util.List<java.util.List<String>> groupAnagrams(String[] strs) {
        java.util.Map<String, java.util.List<String>> map = new java.util.HashMap<>();
        for (String s : strs) {
            char[] arr = s.toCharArray();
            java.util.Arrays.sort(arr);
            String key = new String(arr);
            java.util.List<String> list = map.get(key);
            if (list == null) {
                list = new java.util.ArrayList<>();
                map.put(key, list);
            }
            list.add(s);
        }
        return new java.util.ArrayList<>(map.values());
    }

    public static void main(String[] args) {
        LC0049_GroupAnagrams solver = new LC0049_GroupAnagrams();
        System.out.println(solver.groupAnagrams(new String[]{"eat","tea","tan","ate","nat","bat"}));
    }
}
