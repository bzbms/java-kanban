package tasks;

import managers.TaskType;

public class Subtask extends Task {
    protected int epicId;
    protected TaskType type = TaskType.SUBTASK;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, TaskStatus status, int epicId, int id) {
        super(title, description, status, id);
        this.epicId = epicId;
    }

    public TaskType getType() {
        return type;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        String showTask = "Задача{" +
                "title=" + title +
                ", description=" + description +
                ", status=" + status +
                ", epicId=" + epicId +
                ", id=" + id;
        return showTask + '}';
    }
}
