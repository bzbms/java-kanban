package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

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
            int lastId = 0;

            for (int i = 1; i < csvLines.length; i++) { // Пропускаем первую строку с заголовками
                if (csvLines[i].isBlank()) { // и пустые, если попадутся...
                    break;
                }

                Task task = StringTaskConverter.fromString(csvLines[i]);

                if (task.getType().equals(TaskType.TASK)) {
                    taskManager.tasks.put(task.getId(), task);
                }
                if (task.getType().equals(TaskType.EPIC)) {
                    taskManager.epics.put(task.getId(), (Epic) task);
                }
                if (task.getType().equals(TaskType.SUBTASK)) {
                    Subtask subtask = (Subtask) task;
                    Epic epic = taskManager.epics.get(subtask.getEpicId());

                    taskManager.subtasks.put(task.getId(), subtask);
                    epic.getSubtasksIds().add(subtask.getId()); // Занесём Подзадачу в список Эпика.

                    /* В целом этот костыль поправляет неверно введённые данные в текстовый файлик,
                    но там же может быть много таких нарушений и охотиться за каждым из них пока нецелесообразно...
                    List<Subtask> subtasks = taskManager.getEpicSubtasks(subtask.getEpicId());
                    epic.updateStatus(subtasks);
                    epic.updateTime(subtasks);
                    */
                }
                if (lastId < task.getId()) {
                    lastId = task.getId();
                }
            } // При прямом добавлении в хешмапы тогда нужно и уникальный ID сделать актуальным.
            taskManager.uniqueId = lastId;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка выгрузки из файла: " + file.getName(), e);
        }
        return taskManager;
    }

    void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,startTime,duration(min),epicId");

            for (Task task : getAllTasks()) {
                writer.write(StringTaskConverter.toString(task));
            }
            for (Epic epic : getAllEpics()) {
                writer.write(StringTaskConverter.toString(epic));
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(StringTaskConverter.toString(subtask));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл: " + file.getName(), e);
        }
        // При try-with-resources поток должен сам закрываться.
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
        int id = super.addTask(task);
        save();
        return id;
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


