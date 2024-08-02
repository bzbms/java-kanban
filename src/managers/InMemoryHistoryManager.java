package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final short HISTORY_LIMIT = 10;
    private final List<Task> history = new ArrayList<>();

    @Override
    public void addTask(Task task) {
        if (task != null) {
            history.add(task);
        } else {
            return;
        }
        if (history.size() > HISTORY_LIMIT) {
            history.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        final List<Task> historyCopy = history;
        return historyCopy;
    }
}
