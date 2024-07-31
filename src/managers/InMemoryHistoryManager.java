package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    static final short historyLimit = 10;
    private final List<Task> history = new ArrayList<>();

    @Override
    public <T extends Task> void addTask(T task) { // Можем добавлять любые экземпляры классов, наследуемых от Task.
        history.add(task);
        while (history.size() > historyLimit) {
            history.removeFirst(); // Чистим историю от первых записей, пока их там более 10.
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
