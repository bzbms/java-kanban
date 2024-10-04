package managers;

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
            return -1; // А Подзадача не будет добавлена.
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
        if (!tasks.containsKey(task.getId())) {
            return -1;
        }
        if (task.getStartTime() == null) {
            prioritizedTasks.remove(task); // Если у задачи убрали время, значит она более не приоритетная...
        } else {
            prioritizedTasks.remove(tasks.get(task.getId())); // Чтобы сравнивать обновлённую со всеми, нужно убрать её старый экземпляр.
            if (isTimeNotIntersects(task)) {
                prioritizedTasks.add(task); // Если обновлённая задача не пройдёт проверку - то и зачем ей задали время...
            }
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
        if (task.getStartTime() == null) {
            prioritizedTasks.remove(task);
        } else {
            prioritizedTasks.remove(subtasks.get(task.getId()));
            if (isTimeNotIntersects(task)) {
                prioritizedTasks.add(task);
            }
        }
        subtasks.put(task.getId(), task);
        Epic epic = epics.get(task.getEpicId());
        updateEpicStatus(epic);
        updateEpicTime(epic);
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

        epic.getSubtasksIds().forEach(subId -> subtasksToShow.add(subtasks.get(subId))); // ТЗ-8 Замена цикла foreach на stream.
        return subtasksToShow;
    }

    // УДАЛЕНИЕ
    @Override
    public Task removeTask(int id) {
        if (tasks.get(id) == null) {
            return null;
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
            return null;
        }
        epic.getSubtasksIds().forEach(subId -> { // ТЗ-8 Замена цикла foreach на stream.
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
            return null;
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
        tasks.keySet().forEach(historyManager::removeTask);  // ТЗ-8 Замена цикла foreach на stream.
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        subtasks.keySet().forEach(historyManager::removeTask);  // ТЗ-8 Замена циклов foreach на stream.
        epics.keySet().forEach(historyManager::removeTask);
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.values().forEach(prioritizedTasks::remove);
        subtasks.keySet().forEach(historyManager::removeTask); // ТЗ-8 Замена цикла foreach на stream.
        subtasks.clear();
        epics.values().forEach(epic -> { // ТЗ-8 Замена цикла foreach на stream.
            epic.clearSubtaskIds();
            updateEpicStatus(epic);
            updateEpicTime(epic);
        });
        // epics.values().forEach(Epic::clearSubtaskIds); Выглядят кратко-красиво, но как объединить кратко в одну строку не нашёл. =)
        // epics.values().forEach(this::updateEpicStatus);
        // epics.values().forEach(this::updateEpicTime);
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
a.start <= b.end AND a.end >= b.start - упрощение
*/
        for (Task addedTask : prioritizedTasks) {
            LocalDateTime newStart = task.getStartTime();
            LocalDateTime newEnd = task.getEndTime();
            LocalDateTime ongoingStart = addedTask.getStartTime();
            LocalDateTime ongoingEnd = addedTask.getEndTime();
            if ((newEnd.isAfter(ongoingStart) || newEnd.equals(ongoingStart)) && (newStart.isBefore(ongoingEnd) || newStart.equals(ongoingEnd))) {
                System.out.println("Время выполнения задач пересекается:\n"
                        + newStart.format(formatter) + " - " + newEnd.format(formatter) + " - " + task.getTitle() + ", id=" + task.getId() + "\n"
                        + ongoingStart.format(formatter) + " - " + ongoingEnd.format(formatter) + " - " + addedTask.getTitle() + ", id=" + addedTask.getId());
                throw new ManagerSaveException();
            }
        }
        return true;
    }
}