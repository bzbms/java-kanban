package tasks;

import managers.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    protected ArrayList<Integer> subtaskIds = new ArrayList<>();
    protected LocalDateTime endTime = LocalDateTime.of(0, 1, 1, 0, 0);

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(String title, String description, String startTime, long duration) {
        super(title, description, TaskStatus.NEW, 0, startTime, duration);
    }

    public Epic(String title, String description, TaskStatus status, int id) {
        super(title, description, status, id);
    }

    public Epic(String title, String description, TaskStatus status, int id, String startTime, long duration) {
        super(title, description, status, id, startTime, duration);
    }

    public TaskType getType() {
        return TaskType.EPIC;
    }

    public boolean isEpic() {
        return true;
    }

    public void addSubtasksId(int id) {
        subtaskIds.add(id);
    }

    public void removeSubtasksId(Integer id) {
        subtaskIds.remove(id);
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtaskIds;
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    public void setStatus(TaskStatus status) { // ТЗ-6. Ограничить возможность использовать методы .set
        System.out.println("Статус Эпика должен высчитываться автоматически.");
    }

    public void updateStatus(List<Subtask> subtasksToCheck) {
        this.status = defineStatus(subtasksToCheck);
    }

    public LocalDateTime getStartTime(List<Subtask> subtasks) {
        if (!subtasks.isEmpty()) {
            startTime = subtasks.getFirst().getStartTime();
            if (subtasks.size() > 1) {
                LocalDateTime subtaskStartTime;
                for (int i = 1; i < subtasks.size(); i++) {
                    subtaskStartTime = subtasks.get(i).getStartTime();
                    if (startTime == null || startTime.isAfter(subtaskStartTime)) {
                        startTime = subtaskStartTime;
                    }
                }
            }
        }
        return startTime;
    }

    public Duration getDuration(List<Subtask> subtasks) {
        if (!subtasks.isEmpty()) {
            duration = subtasks.getFirst().getDuration();
            if (subtasks.size() > 1) {
                Duration subtaskDuration;
                for (int i = 1; i < subtasks.size(); i++) {
                    subtaskDuration = subtasks.get(i).getDuration();
                    duration = duration.plus(subtaskDuration);
                }
            }
        }
        return duration;
    }

    public LocalDateTime getEndTime(List<Subtask> subtasks) {
        if (!subtasks.isEmpty()) {
            endTime = subtasks.getFirst().getEndTime();
            if (subtasks.size() > 1) {
                LocalDateTime subtaskEndTime;
                for (int i = 1; i < subtasks.size(); i++) {
                    subtaskEndTime = subtasks.get(i).getEndTime();
                    if (endTime == null || endTime.isBefore(subtaskEndTime)) {
                        endTime = subtaskEndTime;
                    }
                }
            }
        }
        return endTime;
    }

    public String updateTime(List<Subtask> subtasks) {
        return getStartTime(subtasks).format(formatter) + " - "
                + getDuration(subtasks).toMinutes() + "мин, "
                + getEndTime(subtasks).format(formatter);
    }

    private TaskStatus defineStatus(List<Subtask> subtasksToCheck) {

        if (subtasksToCheck.isEmpty()) {
            return TaskStatus.NEW;
        }

        boolean isNEW = false;
        boolean isDONE = false;
        for (Subtask subTask : subtasksToCheck) {
            if (subTask.getStatus() == TaskStatus.IN_PROGRESS) {
                return TaskStatus.IN_PROGRESS; // Если встретится Подзадача с IN_PROGRESS, то дальнейшие проверки делать не нужно.
            }
            if (subTask.getStatus() == TaskStatus.NEW) { // И это значит, что далее может прийти только 2 Статуса.
                isNEW = true;
            } else {
                isDONE = true;
            }
        }
        if (!isNEW) {
            return TaskStatus.DONE;
        } else if (!isDONE) {
            return TaskStatus.NEW;
        } else {
            return TaskStatus.IN_PROGRESS;
        }
    }

    @Override
    public String toString() {
        String showTask = "Задача{" +
                "title=" + title +
                ", description=" + description +
                ", status=" + status +
                ", id=" + id +
                ", startTime=" + startTime.format(formatter) +
                ", duration=" + duration.toMinutes() +
                ", endTime=" + endTime.format(formatter);
        return showTask + '}';
    }

}
