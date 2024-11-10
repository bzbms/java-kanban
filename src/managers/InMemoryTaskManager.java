package managers;

import servers.NotFoundException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Comparator;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    protected int uniqueId = 0;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    // Компаратор через stream перебирает задачи в коллекции (TreeSet) и вызывает у них метод начального времени,
    // сортируя по убыванию. Если время старта совпадёт, то первыми поставит по id задачи.
    private final Comparator<Task> taskComparator =
            Comparator.comparing(Task::getStartTime, Comparator.naturalOrder()).thenComparing(Task::getId);
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(taskComparator);
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    // ДОБАВЛЕНИЕ
    @Override
    public int addTask(Task task) {
        final int id = ++uniqueId;
        task.setId(id);
        if (task.getStartTime() != null && isTimeNotIntersects(task)) {
            prioritizedTasks.add(task);
        }
        tasks.put(id, task);
        return id;
    }

    @Override
    public int addTask(Epic task) { // Так как время Эпика берётся от его Подзадач, то его сравнивать с ними не нужно.
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
            throw new NotFoundException(TaskType.EPIC, task.getEpicId()); // А Подзадача не будет добавлена.
        }
        final int id = ++uniqueId; // 3. Только после этого можно дальше продолжать обычное добавление.
        task.setId(id);
        epic.addSubtasksId(id); // 4. В Список полученного Эпика добавляется ID Подзадачи.
        if (task.getStartTime() != null && isTimeNotIntersects(task)) {
            prioritizedTasks.add(task);
        }
        subtasks.put(task.getId(), task);
        updateEpicStatus(epic);
        updateEpicTime(epic);
        return task.getId();
    }

    // ОБНОВЛЕНИЕ
    @Override
    public int updateTask(Task task) {
        int id = task.getId();
        if (!tasks.containsKey(id)) {
            throw new NotFoundException(TaskType.TASK, id);
        }
        if (task.getStartTime() == null) {
            prioritizedTasks.remove(task); // Если у задачи убрали время, значит она более не приоритетная...
        } else {
            prioritizedTasks.remove(tasks.get(id)); // Чтобы сравнивать обновлённую со всеми, нужно убрать её старый экземпляр.
            if (isTimeNotIntersects(task)) {
                prioritizedTasks.add(task); // Если обновлённая задача не пройдёт проверку - то и зачем ей задали время...
            }
        }
        tasks.put(id, task);
        return id;
    }

    @Override
    public int updateTask(Epic task) {
        int id = task.getId();
        if (!epics.containsKey(id)) {
            throw new NotFoundException(TaskType.EPIC, id);
        }
        epics.put(id, task);
        return id;
    }

    @Override
    public int updateTask(Subtask task) {
        int id = task.getId();
        if (!subtasks.containsKey(id)) {
            throw new NotFoundException(TaskType.SUBTASK, id);
        }
        if (task.getStartTime() == null) {
            prioritizedTasks.remove(task);
        } else {
            prioritizedTasks.remove(subtasks.get(id));
            if (isTimeNotIntersects(task)) {
                prioritizedTasks.add(task);
            }
        }
        subtasks.put(id, task);
        Epic epic = epics.get(task.getEpicId());
        updateEpicStatus(epic);
        updateEpicTime(epic);
        return id;
    }

    // ПОЛУЧЕНИЕ
    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException(TaskType.TASK, id);
        }
        historyManager.addTask(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException(TaskType.EPIC, id);
        }
        historyManager.addTask(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException(TaskType.SUBTASK, id);
        }
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

        epic.getSubtasksIds().forEach(subId -> subtasksToShow.add(subtasks.get(subId)));
        return subtasksToShow;
    }

    // УДАЛЕНИЕ
    @Override
    public Task removeTask(int id) {
        if (tasks.get(id) == null) {
            throw new NotFoundException(TaskType.TASK, id);
        }
        prioritizedTasks.remove(tasks.get(id));
        historyManager.removeTask(id);
        return tasks.remove(id);
    }

    @Override
    public Epic removeEpic(int id) {
        // Вместе с Эпиком также удаляются все его Подзадачи.
        Epic epic = epics.get(id);

        if (epic == null) {
            throw new NotFoundException(TaskType.EPIC, id);
        }
        epic.getSubtasksIds().forEach(subId -> {
            prioritizedTasks.remove(subtasks.get(subId));
            historyManager.removeTask(subId);
            subtasks.remove(subId);
        });
        historyManager.removeTask(id);
        return epics.remove(id);
    }

    @Override
    public Subtask removeSubtask(int id) {
        // Вся Подзадача временно сохраняется при удалении.
        // ID Подзадачи удаляется из Списка Эпика, иначе возникает ошибка при обновлении Статуса.
        if (subtasks.get(id) == null) {
            throw new NotFoundException(TaskType.SUBTASK, id);
        }
        Subtask subtask = subtasks.remove(id);
        prioritizedTasks.remove(subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtasksId(id);
        updateEpicStatus(epic);
        updateEpicTime(epic);
        historyManager.removeTask(id);
        return subtask;
    }

    @Override
    public void removeAllTasks() {
        tasks.values().forEach(prioritizedTasks::remove);
        tasks.keySet().forEach(historyManager::removeTask);
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        subtasks.keySet().forEach(historyManager::removeTask);
        epics.keySet().forEach(historyManager::removeTask);
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.values().forEach(prioritizedTasks::remove);
        subtasks.keySet().forEach(historyManager::removeTask);
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.clearSubtaskIds();
            updateEpicStatus(epic);
            updateEpicTime(epic);
        });
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasksToCheck = getEpicSubtasks(epic.getId());
        epic.updateStatus(subtasksToCheck);
    }

    private void updateEpicTime(Epic epic) {
        ArrayList<Subtask> subtasksToCheck = getEpicSubtasks(epic.getId());
        epic.updateTime(subtasksToCheck);
    }

    private boolean isTimeNotIntersects(Task task) {
        List<Task> prioritizedTasks = getPrioritizedTasks();

/* Мой разум всё равно глючит с логическими преобразованиями... x)
as    ae   bs     be
|------|   |------|
as     bs   ae    be
|------|====|-----|

a.start > b.end OR a.end < b.start - непересечение
NOT( a.start > b.end OR a.end < b.start ) - пересечение
a.start <= b.end AND a.end >= b.start - преобразование
*/
        for (Task addedTask : prioritizedTasks) {
            LocalDateTime newStart = task.getStartTime();
            LocalDateTime newEnd = task.getEndTime();
            LocalDateTime ongoingStart = addedTask.getStartTime();
            LocalDateTime ongoingEnd = addedTask.getEndTime();
            if (!(newEnd.isBefore(ongoingStart) || newStart.isAfter(ongoingEnd))) {
                String error = "Время выполнения задач пересекается:\n"
                        + newStart.format(formatter) + " - " + newEnd.format(formatter) + " - " + task.getTitle() + ", id=" + task.getId() + "\n"
                        + ongoingStart.format(formatter) + " - " + ongoingEnd.format(formatter) + " - " + addedTask.getTitle() + ", id=" + addedTask.getId();
                throw new ManagerSaveException(error);
            }
        }
        return true;
    }
}