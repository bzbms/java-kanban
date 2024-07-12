import java.util.ArrayList;

public class EpicTask extends SingleTask {
    protected ArrayList<Integer> subTaskIds = new ArrayList<>();

    public EpicTask(String title, String description, TaskStatus status) {
        super(title, description, status);
    }

    public EpicTask(String title, String description, TaskStatus status, int id) {
        super(title, description, status, id);
    }

    public boolean isEpic() {
        return true;
    }

    public void addSubTasksId(int id) {
        subTaskIds.add(id);
    }

    public ArrayList<Integer> getSubTasksIds() {
        return subTaskIds;
    }
}
