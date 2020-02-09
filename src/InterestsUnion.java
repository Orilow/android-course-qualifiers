import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class InterestsUnion {

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
    private List<HashSet<String>> groupInterests;
    private HashSet<Integer> groupIdsToConcat;

    public MinUnionSolver() {
        groupInterests = new ArrayList<>();
        groupIdsToConcat = new HashSet<>();
    }

    public void addDeveloper(String interestsInOneString) {
        List<String> currentInterests = Arrays.asList(interestsInOneString.split(" "));

        if (groupInterests.isEmpty()) {
            addNewGroup(currentInterests);
            return;
        }

        for (int i = 0; i < groupInterests.size(); i++) {
            HashSet<String> currentGroup = groupInterests.get(i);
            for (String currentInterest : currentInterests) {
                boolean collision = currentGroup.stream().anyMatch(x -> x.equals(currentInterest));
                if (collision) {
                    addToJoinLater(i);
                    break;
                }
            }
        }

        tryUnionGroupsHavingInterests(currentInterests);
    }

    private void addToJoinLater(int groupId) {
        groupIdsToConcat.add(groupId);
    }

    private void addNewGroup(Collection<String> interests) {
        String id = UUID.randomUUID().toString();
        groupInterests.add(new HashSet<>(interests));
    }

    private void tryUnionGroupsHavingInterests(List<String> interests){
        if (groupIdsToConcat.size() == 1) {
            addDeveloperToGroup(groupIdsToConcat.iterator().next(), interests);
        } else if (groupIdsToConcat.size() == 0) {
            addNewGroup(interests);
        } else {
            Iterator<Integer> groupIdsIterator = groupIdsToConcat.iterator();
            int mainGroupId = groupIdsIterator.next();
            while (groupIdsIterator.hasNext()){
                int currentGroupId = groupIdsIterator.next();
                groupInterests.get(mainGroupId).addAll(groupInterests.get(currentGroupId));
                groupInterests.remove(currentGroupId);
            }
        }

        groupIdsToConcat.clear();
    }

    private void addDeveloperToGroup(int groupId, Collection<String> interests) {
        groupInterests.get(groupId).addAll(interests);
    }

    public int getMinimumUnions()
    {
        return groupInterests.size();
    }
}
