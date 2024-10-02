package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.format.DateTimeFormatter;

public final class StringTaskConverter {
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    private StringTaskConverter() {
    }

    public static Task fromString(String value) {
        String[] elem = value.split(",");  // Разбиваем строку на элементы задачи

        return switch (TaskType.valueOf(elem[1])) {
            case TaskType.TASK ->
                    new Task(elem[2], elem[4], TaskStatus.valueOf(elem[3]), Integer.parseInt(elem[0]), elem[5], Long.parseLong(elem[6]));
            case TaskType.EPIC ->
                    new Epic(elem[2], elem[4], TaskStatus.valueOf(elem[3]), Integer.parseInt(elem[0]), elem[5], Long.parseLong(elem[6]));
            case TaskType.SUBTASK ->
                    new Subtask(elem[2], elem[4], TaskStatus.valueOf(elem[3]), Integer.parseInt(elem[7]), Integer.parseInt(elem[0]), elem[5], Long.parseLong(elem[6]));
        };
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
                + task.getStartTime().format(formatter) + ","
                + task.getDuration().toMinutes() + ","
                + epicId; // Добавится пустое место или цифра(если Подзадача), запятую не ставим.
    }
}
