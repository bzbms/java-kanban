package managers;
/*
Привет, Сергей.
Вот, сдаю работу в режиме опоздуна, но программистское самочувствие значительно лучше - меньше тотальных непоняток...
Благодарю за проверку!
*/

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    int addTask(Task task);

    int addTask(Epic task);

    int addTask(Subtask task);

    int updateTask(Task task);

    int updateTask(Epic task);

    int updateTask(Subtask task);

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Subtask> getEpicSubtasks(int id);

    Task removeTask(int id);

    Epic removeEpic(int id);

    Subtask removeSubtask(int id);

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    List<Task> getHistory();

    // Сергей, стоит ли мне менять код для предыдущего ТЗ, если мне кажется, что я могу его улучшить?
    // Не создаст ли это тебе лишнюю нагрузку при проверке?
    // Например, тут я бы заменил int-счётчики на boolean, так как кол-во задач считать не надо, а только их наличие...
    default void updateEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasksToCheck = getEpicSubtasks(epic.getId());

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
