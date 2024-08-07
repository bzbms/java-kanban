package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int uniqueId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    // ДОБАВЛЕНИЕ
    @Override
    public int addTask(Task task) {
        final int id = ++uniqueId;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public int addTask(Epic task) {
        final int id = ++uniqueId;
        task.setId(id);
        epics.put(id, task);
        return id;
    }

    // Подзадачи не должны существовать отдельно от Эпика, потому:
    @Override
    public int addTask(Subtask task) {
        Epic epic = epics.get(task.getEpicId()); // 1. Проверяется, есть ли Эпик с ID, равным epicId у Подзадачи.
        if (epic == null) { // 2. Если таких Эпиков не создано, то метод будет завершаться на этом во избежание ошибки.
            return -1; // А Подзадача не будет добавлена.
        }
        final int id = ++uniqueId; // 3. Только после этого можно дальше продолжать обычное добавление.
        task.setId(id);
        epic.addSubtasksId(id); // 4. В Список полученного Эпика добавляется ID Подзадачи.
        subtasks.put(task.getId(), task);
        updateEpicStatus(epic);
        return task.getId();
    }

    // ОБНОВЛЕНИЕ
    @Override
    public int updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return -1;
        }
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int updateTask(Epic task) {
        if (!epics.containsKey(task.getId())) {
            return -1;
        }
        epics.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int updateTask(Subtask task) {
        if (!subtasks.containsKey(task.getId())) {
            return -1;
        }
        subtasks.put(task.getId(), task);
        updateEpicStatus(epics.get(task.getEpicId()));
        return task.getId();
    }

    // ПОЛУЧЕНИЕ
    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);

        historyManager.addTask(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);

        historyManager.addTask(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);

        historyManager.addTask(subtask);
        return subtask;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Получение списка Подзадач Эпика.
    @Override
    public ArrayList<Subtask> getEpicSubtasks(int id) {
        Epic epic = epics.get(id);
        ArrayList<Subtask> subtasksToShow = new ArrayList<>();

        for (Integer subId : epic.getSubtasksIds()) {
            subtasksToShow.add(subtasks.get(subId));
        }
        return subtasksToShow;
    }

    // УДАЛЕНИЕ
    @Override
    public Task removeTask(int id) {
        if (tasks.get(id) == null) {
            return null;
        }
        return tasks.remove(id);
    }

    @Override
    public Epic removeEpic(int id) {
        // Вместе с Эпиком также удаляются все его Подзадачи.
        Epic epic = epics.get(id);

        if (epic == null) {
            return null;
        }
        for (Integer subId : epic.getSubtasksIds()) {
            subtasks.remove(subId);
        }
        return epics.remove(id);
    }

    @Override
    public Subtask removeSubtask(int id) {
        // Вся Подзадача временно сохраняется при удалении.
        // ID Подзадачи удаляется из Списка Эпика, иначе возникает ошибка при обновлении Статуса.
        if (subtasks.get(id) == null) {
            return null;
        }
        Subtask subtask = subtasks.remove(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtasksId(id);
        updateEpicStatus(epic);
        return subtask;
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            updateEpicStatus(epic);
        }
    }

    @Override
    public List<Task> getHistory(){
        return historyManager.getHistory();
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasksToCheck = getEpicSubtasks(epic.getId());

        if (subtasksToCheck.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean isNEW = false;
        boolean isDONE = false;
        for (Subtask subTask : subtasksToCheck) {
            if (subTask.getStatus() == TaskStatus.IN_PROGRESS) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                return; // Если встретится Подзадача с IN_PROGRESS, то дальнейшие проверки делать не нужно.
            }
            if (subTask.getStatus() == TaskStatus.NEW) { // И это значит, что далее может прийти только 2 Статуса.
                isNEW = true;
            } else {
                isDONE = true;
            }
        }
        if (!isNEW) {
            epic.setStatus(TaskStatus.DONE);
        } else if (!isDONE) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

}