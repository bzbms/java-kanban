package tasks;

import java.util.ArrayList;
import java.util.List;

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
}
