package Tasks;

public class Subtask extends Task {
    protected int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }
    public Subtask(String title, String description, TaskStatus status, int epicId, int id) {
        super(title, description, status, id);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
