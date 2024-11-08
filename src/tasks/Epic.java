package tasks;

import managers.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    protected ArrayList<Integer> subtaskIds = new ArrayList<>();
    protected LocalDateTime endTime;

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
        // Почему-то при добавлении Эпика через веб-клиент его список Id Подзадач остаётся null
        // - возникал NullPointer при добавлении Подзадачи. Так и не нашёл, почему это происходит и сделал пока так:
        if (subtaskIds == null) {
            subtaskIds = new ArrayList<>();
        }
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

    public void updateTime(List<Subtask> subtasks) {
        if (!subtasks.isEmpty()) {
            duration = Duration.ofMinutes(0);
            subtasks.forEach(subtask -> {
                if (startTime == null || startTime.isAfter(subtask.getStartTime())) {
                    startTime = subtask.getStartTime();
                }
                if (endTime == null || endTime.isBefore(subtask.getEndTime())) {
                    endTime = subtask.getEndTime();
                }
                duration = Duration.between(startTime, endTime);
            });
        }
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
        String start = "Не задано";
        String end = "Не задано";
        if (startTime != null) {
            start = startTime.format(formatter);
        }
        if (endTime != null) {
            end = endTime.format(formatter);
        }
        String showTask = "Задача{" +
                "title=" + title +
                ", description=" + description +
                ", status=" + status +
                ", id=" + id +
                ", startTime=" + start +
                ", duration=" + duration.toMinutes() +
                ", endTime=" + end;
        return showTask + '}';
    }

}
