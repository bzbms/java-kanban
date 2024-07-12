import java.util.ArrayList;

public class SubTask extends SingleTask {
    protected int epicId;

    public SubTask(String title, String description, TaskStatus status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }
    public SubTask(String title, String description, TaskStatus status, int epicId, int id) {
        super(title, description, status, id);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
