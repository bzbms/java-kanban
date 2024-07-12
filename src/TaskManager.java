/* Добрый день Сергей, после долгого перерыва вот снова продолжаю.
 Было тяжко, пришлось многое вспоминать и пройти через когнитивные муки
 от созерцания непонятного кода и попыток понять, что именно надо вспомнить. х)
 В итоге опять опаздываю (не думаю, что легко пронесусь по 5-му спринту),
 боюсь снова придётся менять когорту, ну посмотрим...

 Оставил Main для собственной проверки.

 Спасибо за твою работу.
*/

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int uniqueId = 0;
    private static HashMap<Integer, SingleTask> singleTasks = new HashMap<>();
    private static HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    private static HashMap<Integer, SubTask> subTasks = new HashMap<>();

    // ДОБАВЛЕНИЕ
    public static int addTask(SingleTask task) {
        final int id = ++uniqueId;
        task.setId(id);
        singleTasks.put(id, task);
        return id;
    }

    public static int addTask(EpicTask task) {
        final int id = ++uniqueId;
        task.setId(id);
        epicTasks.put(id, task);
        return id;
    }

    // У меня сделано так, что Подзадачи не могут существовать отдельно от Эпика, потому:
    public static int addTask(SubTask task) {
        EpicTask epic = getEpicTask(task.getEpicId()); // 1. Проверяется, есть ли Эпик с ID, равным epicId у Подзадачи.
        if (epic == null) { // 2. Если таких Эпиков не создано, то метод будет завершаться на этом во избежание ошибки.
            return -1; // А Подзадача не будет добавлена.
        }
        final int id = ++uniqueId; // 3. Только после этого можно дальше продолжать обычное добавление.
        task.setId(id);
        epic.addSubTasksId(id); // 4. В Список полученного Эпика добавляется ID Подзадачи.
        subTasks.put(task.id, task);
        updateEpicStatus(epic.getId());
        return task.id;
    }

    // ОБНОВЛЕНИЕ
    public static void updateTask(SingleTask task) {
        singleTasks.put(task.id, task);
    }

    public static void updateTask(EpicTask task) {
        epicTasks.put(task.id, task);
    }

    public static void updateTask(SubTask task) {
        subTasks.put(task.id, task);
        updateEpicStatus(task.getEpicId());
    }

    // Обновление статуса Эпика реализовано счётчиками.
    // 3 часа сидел и думал как извертеться покомпактнее, в итоге выдохся на этом... х)
    static void updateEpicStatus(int id) {
        EpicTask epic = getEpicTask(id);
        ArrayList<SubTask> subTasksToCheck = getEpicSubTasks(id);

        if (subTasksToCheck.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        int countNEW = 0;
        int countDONE = 0;
        for (SubTask subTask : subTasksToCheck) {
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

    // ПОЛУЧЕНИЕ
    public static SingleTask getSingleTask(int id) {
        return singleTasks.get(id);
    }

    public static EpicTask getEpicTask(int id) {
        return epicTasks.get(id);
    }

    public static SubTask getSubTask(int id) {
        return subTasks.get(id);
    }

    public static ArrayList<SingleTask> getAllSingleTasks() {
        return new ArrayList<>(singleTasks.values());
    }

    public static ArrayList<EpicTask> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    public static ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    // Получение списка Подзадач Эпика, надеюсь не перемудрил...
    public static ArrayList<SubTask> getEpicSubTasks(int id) {
        EpicTask epic = getEpicTask(id);
        ArrayList<SubTask> subTasksToShow = new ArrayList<>();

        for (Integer key : subTasks.keySet()) { // Перебор всех ключей Подзадач, чтобы сравнить их со Списком ID.
            for (Integer taskId : epic.getSubTasksIds()) { // Тут и перебор полученного Списка.
                if (taskId.equals(key)) { // Если одинаковые, значит Подзадача принадлежит Эпику.
                    subTasksToShow.add(getSubTask(taskId));
                }
            }
        }
        return subTasksToShow;
    }

    // УДАЛЕНИЕ
    public static void removeSingleTask(int id) {
        singleTasks.remove(id);
    }

    public static void removeEpicTask(int id) {
        // Вместе с Эпиком также удаляются все его Подзадачи.
        ArrayList<Integer> subTasksToRemove = new ArrayList<>();

        for (SubTask sub : subTasks.values()) {
            if (sub.getEpicId() == id) {
                subTasksToRemove.add(sub.getId());
            }
        }
        for (Integer subId : subTasksToRemove) {
            subTasks.remove(subId);
        } // Так как возникала ошибка при удалении Подзадачи сразу - в первом цикле, то запомнил подходящие ID в Список.
        epicTasks.remove(id);
    }

    public static void removeSubTask(int id) {
        // Так как после удаления Подзадачи уже не узнаём её EpicId, то надо его запомнить для обновления Статуса.
        int epicId = subTasks.get(id).getEpicId();
        subTasks.remove(id);
        updateEpicStatus(epicId);
    }

    public static void removeAll() {
        singleTasks.clear();
        epicTasks.clear();
        subTasks.clear();
    }
}