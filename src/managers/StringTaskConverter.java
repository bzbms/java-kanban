package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

public final class StringTaskConverter {

    private StringTaskConverter() {
    }

    public static Task fromString(String value) {
        try {
            String[] elem = value.split(",");  // Разбиваем строку на элементы задачи
            if (TaskType.valueOf(elem[1]) == TaskType.TASK) {
                return new Task(elem[2], elem[4], TaskStatus.valueOf(elem[3]), Integer.parseInt(elem[0]));
            }
            if (TaskType.valueOf(elem[1]) == TaskType.EPIC) {
                return new Epic(elem[2], elem[4], TaskStatus.valueOf(elem[3]), Integer.parseInt(elem[0]));
            }
            if (TaskType.valueOf(elem[1]) == TaskType.SUBTASK) {
                return new Subtask(elem[2], elem[4], TaskStatus.valueOf(elem[3]), Integer.parseInt(elem[5]), Integer.parseInt(elem[0]));
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("В файле есть недопустимый тип задачи.");
        }
        return null;
    }

    public static String toString(Task task) {
        String epicId = "";

        if (task.getType().equals(TaskType.SUBTASK)) { // Если это Подзадача - узнаем её принадлежность к Эпику.
            Subtask subtask = (Subtask) task;
            epicId = Integer.toString(subtask.getEpicId());
        }

        return "\n" + task.getId() + ","
                + task.getType() + ","
                + task.getTitle() + ","
                + task.getStatus() + ","
                + task.getDescription() + ","
                + epicId; // Добавится пустое место или цифра(если Подзадача), запятую не ставим.
    }
}
