package Tasks;

import java.util.ArrayList;

public class Epic extends Task {
    protected ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(String title, String description, TaskStatus status, int id) {
        super(title, description, status, id);
    }

    public boolean isEpic() {
        return true;
    }

    public void addSubtasksId(int id) {
        subtaskIds.add(id);
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtaskIds;
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }
}
