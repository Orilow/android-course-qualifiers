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
    private Hashtable<Integer, HashMap<String, Boolean>> groupInterests;
    private HashSet<Integer> groupIdsToConcat;
    private int generatedId;

    private int getGeneratedId(){
        return generatedId++;
    }

    public MinUnionSolver() {
        groupInterests = new Hashtable<>();
        groupIdsToConcat = new HashSet<>();
        generatedId = 0;
    }

    public void addDeveloper(String interestsInOneString) {
        List<String> parsedInterests = Arrays.asList(interestsInOneString.split(" "));

        for (Map.Entry<Integer, HashMap<String, Boolean>> currentGroup: groupInterests.entrySet())
        {
            for (String parsedInterest: parsedInterests){
                if (currentGroup.getValue().containsKey(parsedInterest)) {
                    addToJoinLater(currentGroup.getKey());
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
        var hashMap = new HashMap<String, Boolean>();
        for (String interest: interests) {
            hashMap.put(interest, true);
        }
        groupInterests.put(getGeneratedId(), hashMap);
    }

    private void tryUnionGroupsHavingInterests(List<String> interests){
        if (groupIdsToConcat.size() > 1) {
            Iterator<Integer> groupIdsIterator = groupIdsToConcat.iterator();
            int mainGroupId = groupIdsIterator.next();
            var interestsMap = new HashMap<String, Boolean>();
            for (String interest: interests) {
                interestsMap.put(interest, true);
            }
            groupInterests.get(mainGroupId).putAll(interestsMap);
            while (groupIdsIterator.hasNext()) {
                int currentGroupId = groupIdsIterator.next();
                groupInterests.get(mainGroupId).putAll(groupInterests.get(currentGroupId));
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
        var hashMap = new HashMap<String, Boolean>();
        for (String interest: interests) {
            hashMap.put(interest, true);
        }
        groupInterests.get(groupId).putAll(hashMap);
    }

    public int getMinimumUnions()
    {
        return groupInterests.values().size();
    }
}
