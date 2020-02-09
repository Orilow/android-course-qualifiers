import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));
        int devs_amount = Integer.parseInt(bi.readLine());
        MinUnionSolver minUnionSolver = new MinUnionSolver();

        for (int i = 0; i < devs_amount; i++) {
            String dev_interests = bi.readLine();
            minUnionSolver.addDeveloper(dev_interests);
        }
        bi.close();
        System.out.print(minUnionSolver.getMinimumUnions());
    }
}

class MinUnionSolver {
    private Map<String, HashSet<String>> groupInterests;
    private HashSet<String> groupIdsToConcat;

    public MinUnionSolver() {
        groupInterests = new HashMap<>();
        groupIdsToConcat = new HashSet<>();
    }

    public void addDeveloper(String interestsInOneString) {
        List<String> currentInterests = Arrays.asList(interestsInOneString.split(" "));

        if (groupInterests.values().isEmpty()) {
            addNewGroup(currentInterests);
            return;
        }

        for (String currentGroupId : groupInterests.keySet()) {
            HashSet<String> currentGroup = groupInterests.get(currentGroupId);
            for (int i = 0; i < currentInterests.size(); i++) {
                String currentInterest = currentInterests.get(i);
                boolean collision = currentGroup.stream().anyMatch(x -> x.equals(currentInterest));
                if (collision) {
                    addToJoinLater(currentGroupId);
                    break;
                }
            }
        }

        tryUnionGroupsHavingInterests(currentInterests);
    }

    private void addToJoinLater(String groupId) {
        groupIdsToConcat.add(groupId);
    }

    private void addNewGroup(Collection<String> interests) {
        String id = UUID.randomUUID().toString();
        groupInterests.put(id, new HashSet<>(interests));
    }

    private void tryUnionGroupsHavingInterests(List<String> interests){
        if (groupIdsToConcat.size() == 1) {
            addDeveloperToGroup(groupIdsToConcat.iterator().next(), interests);
        } else if (groupIdsToConcat.size() == 0) {
            addNewGroup(interests);
        } else {
            Iterator<String> groupIdsIterator = groupIdsToConcat.iterator();
            String mainGroupId = groupIdsIterator.next();
            while (groupIdsIterator.hasNext()){
                String currentGroupId = groupIdsIterator.next();
                groupInterests.get(mainGroupId).addAll(groupInterests.get(currentGroupId));
                groupInterests.remove(currentGroupId);
            }
        }

        groupIdsToConcat.clear();
    }

    private void addDeveloperToGroup(String groupId, Collection<String> interests) {
        groupInterests.get(groupId).addAll(interests);
    }

    public int getMinimumUnions()
    {
        return groupInterests.size();
    }
}
