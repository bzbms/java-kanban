package Managers;
/*
Привет, Сергей. Всё удалось поправить, только не понял как лишний импорт Списка
попал в класс Subtask и его там не было, когда стал проверять... Мистика. х)
*/

import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;
import Tasks.TaskStatus;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int uniqueId = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private  HashMap<Integer, Subtask> subtasks = new HashMap<>();

    // ДОБАВЛЕНИЕ
    public int addTask(Task task) {
        final int id = ++uniqueId;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    public int addTask(Epic task) {
        final int id = ++uniqueId;
        task.setId(id);
        epics.put(id, task);
        return id;
    }

    // У меня сделано так, что Подзадачи не могут существовать отдельно от Эпика, потому:
    public int addTask(Subtask task) {
        Epic epic = getEpic(task.getEpicId()); // 1. Проверяется, есть ли Эпик с ID, равным epicId у Подзадачи.
        if (epic == null) { // 2. Если таких Эпиков не создано, то метод будет завершаться на этом во избежание ошибки.
            return -1; // А Подзадача не будет добавлена.
        }
        final int id = ++uniqueId; // 3. Только после этого можно дальше продолжать обычное добавление.
        task.setId(id);
        epic.addSubtasksId(id); // 4. В Список полученного Эпика добавляется ID Подзадачи.
        subtasks.put(task.getId(), task);
        updateEpicStatus(epic.getId());
        return task.getId();
    }

    // ОБНОВЛЕНИЕ
    public int updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return -1;
        }
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public int updateTask(Epic task) {
        if (!epics.containsKey(task.getId())) {
            return -1;
        }
        epics.put(task.getId(), task);
        return task.getId();
    }

    public int updateTask(Subtask task) {
        if (!subtasks.containsKey(task.getId())) {
            return -1;
        }
        subtasks.put(task.getId(), task);
        updateEpicStatus(task.getEpicId());
        return task.getId();
    }

    // ПОЛУЧЕНИЕ
    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Получение списка Подзадач Эпика.
    public ArrayList<Subtask> getEpicSubtasks(int id) {
        Epic epic = getEpic(id);
        ArrayList<Subtask> subtasksToShow = new ArrayList<>();

        for (Integer subId : epic.getSubtasksIds()) {
            subtasksToShow.add(getSubtask(subId));
        }
        return subtasksToShow;
    }

    // УДАЛЕНИЕ
    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeEpic(int id) {
        // Вместе с Эпиком также удаляются все его Подзадачи.
        Epic epic = getEpic(id);

        for (Integer subId : epic.getSubtasksIds()) {
            subtasks.remove(subId);
        }
        epics.remove(id);
    }

    public void removeSubtask(int id) {
        // Так как после удаления Подзадачи уже не узнаем её EpicId, то надо его запомнить для обновления Статуса.
        int epicId = subtasks.get(id).getEpicId();
        subtasks.remove(id);
        updateEpicStatus(epicId);
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
        }
        subtasks.clear();
    }

    // Обновление статуса Эпика реализовано счётчиками.
    private void updateEpicStatus(int id) {
        Epic epic = getEpic(id);
        ArrayList<Subtask> subtasksToCheck = getEpicSubtasks(id);

        if (subtasksToCheck.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        int countNEW = 0;
        int countDONE = 0;
        for (Subtask subTask : subtasksToCheck) {
            if (subTask.getStatus() == TaskStatus.IN_PROGRESS) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                return; // Если встретится Подзадача с IN_PROGRESS, то дальнейшие проверки делать не нужно.
            }
            if (subTask.getStatus() == TaskStatus.NEW) { // И это значит, что далее может прийти только 2 Статуса.
                countNEW++;
            } else {
                countDONE++;
            }
        }
        if (countNEW == 0) {
            epic.setStatus(TaskStatus.DONE);
        } else if (countDONE == 0) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}