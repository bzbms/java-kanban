package managers;

/*
https://yandex.zoom.us/rec/play/z9N1dYzpo8vyexI3d69RS3N0rJxj7xo__b64MDdvlL0hy-F-rLGNyLUrdd4PwBY_7HT9F9N9WUPRxzOU.Qq9SVi53nyi6BE9U?canPlayFromShare=true&from=share_recording_detail&continueMode=true&componentName=rec-play&originRequestUrl=https%3A%2F%2Fyandex.zoom.us%2Frec%2Fshare%2FVIkXGMO1wQEGYHXZWlAaDpgbRM_gsVSI4m-vh20H0E0Q8eD1Col0M8qsXGCG02am.RZMk25c6HhgQUqbW

- тесты - чтобы был unit5 и правильный имопрт (org.junit.jupiter)
- в абстрактном классе-тесте тесты не будут ранится!!! (его не надо пробовать запускать)
- у вас был элемент в TreeSet вы его изменили - внимание - он теперь вне сортировки этого множества. Элемент надо сначала удалить из множества, потом изменить и снова добавить - так он отсортируется и добавится как надо

 */


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
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int uniqueId = 0;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    // Компаратор через stream перебирает задачи в коллекции (TreeSet) и вызывает у них метод начального времени,
    // сортируя первыми ставя Null'ы, а остальное ставя по убыванию.
    // Если время старта совпадёт, то первыми поставит по id задачи.
    private final Comparator<Task> taskComparator =
            Comparator.comparing(Task::getStartTime, Comparator.nullsFirst(Comparator.naturalOrder())).thenComparing(Task::getId);
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(taskComparator);
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public List<Task> getPrioritizedTasks() {
        // Отсеиваем null'ы сразу здесь.
        return prioritizedTasks.stream().filter(task -> task.getStartTime() != null).collect(Collectors.toList());
    }

    // ДОБАВЛЕНИЕ
    @Override
    public int addTask(Task task) {
        if (isTimeNotIntersects(task)) {
            final int id = ++uniqueId;
            task.setId(id);
            prioritizedTasks.add(task);
            tasks.put(id, task);
            return id;
        }
        return -1;
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
        if (isTimeNotIntersects(task)) {
            final int id = ++uniqueId; // 3. Только после этого можно дальше продолжать обычное добавление.
            task.setId(id);
            epic.addSubtasksId(id); // 4. В Список полученного Эпика добавляется ID Подзадачи.
            subtasks.put(task.getId(), task);
            prioritizedTasks.add(task);
            updateEpicStatus(epic);
            updateEpicTime(epic);
            return task.getId();
        }
        return -2;
    }

    // ОБНОВЛЕНИЕ
    @Override
    public int updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return -1;
        }
        Task oldTask = tasks.get(task.getId());
        prioritizedTasks.remove(oldTask); // Для обновления сортировки необходимо пере-добавить задачу в Set.
        if (isTimeNotIntersects(task)) {
            prioritizedTasks.add(task);
            tasks.put(task.getId(), task);
            return task.getId();
        }
        prioritizedTasks.add(oldTask); // Если обновлённая задача не пройдёт проверку - возвращаем старую на место.
        return -2;
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
        Subtask oldSubtask = subtasks.get(task.getId());
        prioritizedTasks.remove(oldSubtask);
        if (isTimeNotIntersects(task)) {
            prioritizedTasks.add(task);
            subtasks.put(task.getId(), task);
            Epic epic = epics.get(task.getEpicId());
            updateEpicStatus(epic);
            updateEpicTime(epic);
            return task.getId();
        }
        prioritizedTasks.add(oldSubtask);
        return -2;
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
    public void clear() {
        prioritizedTasks.clear();
        tasks.clear();
        subtasks.clear();
        epics.clear();
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
        List<Task> tasks = getPrioritizedTasks();

        if (tasks.size() > 1) {
            LocalDateTime newStart = task.getStartTime();
            LocalDateTime newEnd = task.getEndTime();
            // Можем не проверять весь список, если новая задача будет раньше первой или позже последней.
/*            if (newEnd.isAfter(tasks.getFirst().getStartTime()) || newStart.isBefore(tasks.getLast().getEndTime())) {
                return true;
            } else { */// Если же задача пытается вклиниться между текущими, то придётся проверять весь список.
            // Видимо интересная идея оптимизировать проверку, но я так и не смог её реализовать...

            for (int i = 0; i < tasks.size(); i++) { // Тогда бы я начинал проверку c i = 1 до предпоследнего...
                LocalDateTime ongoingStart = tasks.get(i).getStartTime();
                LocalDateTime ongoingEnd = tasks.get(i).getEndTime();
                if (!newEnd.isAfter(ongoingStart) || !newStart.isBefore(ongoingEnd)) {
                    continue; // Я здесь сломал голову, так и не смог понять как отбирают эти условия - оставил то, что выдаёт верный результат после метода перебора... =(
                }
                System.out.println("Время выполнения задач пересекается:\n"
                        + newStart.format(formatter) + " - " + newEnd.format(formatter) + " - " + task.getTitle() + ", id=" + task.getId() + "\n"
                        + ongoingStart.format(formatter) + " - " + ongoingEnd.format(formatter) + " - " + tasks.get(i).getTitle() + ", id=" + tasks.get(i).getId());
                throw new ManagerSaveException();
            }
        }
        // }
        return true;
    }

}