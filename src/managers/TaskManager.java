package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

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

    List<Task> getPrioritizedTasks();
}
