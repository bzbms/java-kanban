package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        final FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try {
            String csv = Files.readString(file.toPath());
            String[] csvLines = csv.split("\n");

            for (int i = 1; i < csvLines.length; i++) { // Пропускаем первую строку с заголовками
                if (csvLines[i].isBlank()) {
                    break;
                }

                Task task = fromString(csvLines[i]);

                taskManager.addTask(task); // Если строка с Подзадачей будет раньше её Эпика, то она не добавится -
                // - пока это не отлавливаю, т.к. это видимо не тривиально...

                if (task.getType().equals(TaskType.SUBTASK)) {
                    Subtask subtask = (Subtask) task;
                    Epic epic = (Epic) taskManager.getTask(subtask.getEpicId());
                    epic.getSubtasksIds().add(subtask.getId()); // Занесём Подзадачу в список Эпика.
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка выгрузки из файла: " + file.getName(), e);
        }
        return taskManager;
    }

    void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic");

            for (Task task : getAllTasks()) {
                writer.write(toString(task));
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic));
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask));
            }
        } catch (ManagerSaveException e) {
            e.getDetailedMessage();
            for (StackTraceElement stack : e.getStackTrace()) {
                System.out.printf("Класс: " + stack.getClassName() + ", " +
                        "метод: " + stack.getMethodName() + ", " +
                        "имя файла: " + stack.getFileName() + ", " +
                        "строка кода: " + stack.getLineNumber() + "%n%n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        // При try-with-resources поток должен сам закрываться.
    }

    public static Task fromString(String value) {
        String[] elem = value.split(","); // Разбиваем строку на элементы задачи

        if (TaskType.valueOf(elem[1]) == TaskType.TASK) {
            return new Task(elem[2], elem[4], TaskStatus.valueOf(elem[3]), Integer.parseInt(elem[0]));
        }
        if (TaskType.valueOf(elem[1]) == TaskType.EPIC) {
            return new Epic(elem[2], elem[4], TaskStatus.valueOf(elem[3]), Integer.parseInt(elem[0]));
        }
        if (TaskType.valueOf(elem[1]) == TaskType.SUBTASK) {
            return new Subtask(elem[2], elem[4], TaskStatus.valueOf(elem[3]), Integer.parseInt(elem[5]), Integer.parseInt(elem[0]));
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

    @Override
    public int addTask(Task task) {
        super.addTask(task);
        save();
        return task.getId();
    }

    @Override
    public int addTask(Epic task) {
        super.addTask(task);
        save();
        return task.getId();
    }

    @Override
    public int addTask(Subtask task) {
        super.addTask(task);
        save();
        return task.getId();
    }

    @Override
    public int updateTask(Task task) {
        super.updateTask(task);
        save();
        return task.getId();
    }

    @Override
    public int updateTask(Epic epic) {
        super.updateTask(epic);
        save();
        return epic.getId();
    }

    @Override
    public int updateTask(Subtask subtask) {
        super.updateTask(subtask);
        save();
        return subtask.getId();
    }

    @Override
    public Task removeTask(int id) {
        Task task = super.removeTask(id);
        save();
        return task;
    }

    @Override
    public Epic removeEpic(int id) {
        Epic epic = super.removeEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask removeSubtask(int id) {
        Subtask subtask = super.removeSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

}


