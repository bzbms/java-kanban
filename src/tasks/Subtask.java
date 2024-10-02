package tasks;

import managers.TaskType;

public class Subtask extends Task {
    protected int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, String startTime, long duration, int epicId) {
        super(title, description, TaskStatus.NEW, 0, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, TaskStatus status, int epicId, int id) {
        super(title, description, status, id);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, TaskStatus status, int epicId, int id, String startTime, long duration) {
        super(title, description, status, id, startTime, duration);
        this.epicId = epicId;
    }

    public TaskType getType() {
        return TaskType.SUBTASK;
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
                ", id=" + id +
                ", startTime=" + startTime.format(formatter) +
                ", duration=" + duration.toMinutes() +
                ", endTime=" + getEndTime().format(formatter);
        return showTask + '}';
    }
}
