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
        List<String> parsedInterests = Arrays.asList(interestsInOneString.split(" "));

        for (int i = 0; i < groupInterests.size(); i++) {
            HashSet<String> currentGroup = groupInterests.get(i);
            for (String parsedInterest : parsedInterests) {
                if (currentGroup.contains(parsedInterest)) {
                    addToJoinLater(i);
                    break;
                }
            }
        }

        tryUnionGroupsHavingInterests(parsedInterests);
    }

    private void addToJoinLater(int groupId) {
        groupIdsToConcat.add(groupId);
    }

    private void addNewGroup(Collection<String> interests) {
        groupInterests.add(new HashSet<>(interests));
    }

    private void tryUnionGroupsHavingInterests(List<String> interests){
        if (groupIdsToConcat.size() > 1) {
            Iterator<Integer> groupIdsIterator = groupIdsToConcat.iterator();
            int mainGroupId = groupIdsIterator.next();
            groupInterests.get(mainGroupId).addAll(interests);
            while (groupIdsIterator.hasNext()) {
                int currentGroupId = groupIdsIterator.next();
                groupInterests.get(mainGroupId).addAll(groupInterests.get(currentGroupId));
                groupInterests.remove(currentGroupId);
            }
        } else if (groupIdsToConcat.size() == 1) {
            addDeveloperToGroup(groupIdsToConcat.iterator().next(), interests);
        } else {
            addNewGroup(interests);
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
