package managers;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    <T extends Task> void addTask(T task);

    List<Task> getHistory();
}
