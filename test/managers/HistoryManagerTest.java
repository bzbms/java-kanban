package managers;

import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HistoryManagerTest {
    static final HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    void addingToHistory() {
        Task task = new Task("TaskovProdoyote?", "NetTolьkoTestiruem...Ozadachennoe");
        historyManager.addTask(task);
        assertNotNull(historyManager.getHistory().get(0), "Задача не добавилась.");
    }

    @Test
    void isHistoryRememberOnly10Tasks() {
        for (int i = 0; i < 12; i++) {
            Task task = new Task("Task" + (i + 1), "Description" + (i + 1));
            historyManager.addTask(task);
        }
        final List <Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(10, history.size(), "История хранит более 10 задач.");
    }
}