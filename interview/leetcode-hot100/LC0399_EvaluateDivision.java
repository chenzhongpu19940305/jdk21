public class LC0399_EvaluateDivision {

    public double[] calcEquation(java.util.List<java.util.List<String>> equations, double[] values,
                                 java.util.List<java.util.List<String>> queries) {
        java.util.Map<String, java.util.Map<String, Double>> graph = new java.util.HashMap<>();
        for (int i = 0; i < equations.size(); i++) {
            String a = equations.get(i).get(0);
            String b = equations.get(i).get(1);
            double v = values[i];
            graph.computeIfAbsent(a, k -> new java.util.HashMap<>()).put(b, v);
            graph.computeIfAbsent(b, k -> new java.util.HashMap<>()).put(a, 1.0 / v);
        }
        double[] res = new double[queries.size()];
        for (int i = 0; i < queries.size(); i++) {
            String x = queries.get(i).get(0);
            String y = queries.get(i).get(1);
            if (!graph.containsKey(x) || !graph.containsKey(y)) {
                res[i] = -1.0;
            } else if (x.equals(y)) {
                res[i] = 1.0;
            } else {
                java.util.Set<String> visited = new java.util.HashSet<>();
                res[i] = dfs(graph, x, y, 1.0, visited);
            }
        }
        return res;
    }

    private double dfs(java.util.Map<String, java.util.Map<String, Double>> graph,
                       String cur, String target, double acc, java.util.Set<String> visited) {
        if (cur.equals(target)) {
            return acc;
        }
        visited.add(cur);
        java.util.Map<String, Double> next = graph.get(cur);
        for (java.util.Map.Entry<String, Double> e : next.entrySet()) {
            String nei = e.getKey();
            if (visited.contains(nei)) {
                continue;
            }
            double res = dfs(graph, nei, target, acc * e.getValue(), visited);
            if (res != -1.0) {
                return res;
            }
        }
        return -1.0;
    }

    public static void main(String[] args) {
        LC0399_EvaluateDivision solver = new LC0399_EvaluateDivision();
        java.util.List<java.util.List<String>> equations = new java.util.ArrayList<>();
        equations.add(java.util.Arrays.asList("a", "b"));
        equations.add(java.util.Arrays.asList("b", "c"));
        double[] values = {2.0, 3.0};
        java.util.List<java.util.List<String>> queries = new java.util.ArrayList<>();
        queries.add(java.util.Arrays.asList("a", "c"));
        double[] ans = solver.calcEquation(equations, values, queries);
        System.out.println(ans[0]);
    }
}
