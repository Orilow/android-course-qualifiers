package firsttask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class InterestsUnion {

    public static void main(String[] args) throws IOException {
        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in)); // a bit faster than Scanner
        int devs_amount = Integer.parseInt(bi.readLine());
        MinUnionSolver minUnionSolver = new MinUnionSolver();

        for (int i = 0; i < devs_amount; i++) {
            String dev_interests = bi.readLine();
            minUnionSolver.addDeveloper(dev_interests);
        }
        bi.close();
        System.out.println(minUnionSolver.getUnionsAmount());
    }
}

class MinUnionSolver {
    private Map<String, Integer> nodes;
    private List<HashSet<Integer>> edges;
    private int generatedId;

    private int getGeneratedId(){
        return generatedId++;
    }

    public MinUnionSolver() {
        nodes = new HashMap<>();
        edges = new ArrayList<>();
        generatedId = 0;
    }

    public void addDeveloper(String interestsInOneString) {
        String[] parsedInterests = interestsInOneString.split(" ");
        String previous = null;
        for (int i = 0; i < parsedInterests.length; i++) {
            String parsedInterest = parsedInterests[i];
            if (!nodes.containsKey(parsedInterest)) {
                int newId = createNode(parsedInterest);
                if (previous != null) {
                    addNewEdge(nodes.get(previous), newId);
                }
            } else {
                if (previous != null) {
                    addNewEdge(nodes.get(parsedInterest), nodes.get(previous));
                }
            }
            previous = parsedInterest;
        }
    }

    private int createNode(String name) {
        int newId = getGeneratedId();
        nodes.put(name, newId);
        edges.add(new HashSet<>());
        return newId;
    }

    private void addNewEdge(int i, int j) {
        edges.get(i).add(j);
        edges.get(j).add(i);
    }

    public int getUnionsAmount() {
        boolean[] used = new boolean[edges.size()];
        int componentsCounter = 0;
        for (int i = 0; i < edges.size(); i++) {
            if (!used[i]) {
                componentsCounter++;
                dfs(i, used);
            }
        }

        return componentsCounter;
    }

    private void dfs(int v, boolean[] used) {
        used[v] = true;
        for (int to : edges.get(v)) {
            if (!used[to]) {
                dfs(to, used);
            }
        }
    }
}
