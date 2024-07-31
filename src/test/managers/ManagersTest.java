package managers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import tasks.Task;
import java.util.List;

class ManagersTest {
    static TaskManager taskManager = Managers.getDefaultManager();
    static HistoryManager historyManager = Managers.getDefaultHistory();
    static Task task;

    @BeforeAll
    static void beforeAll(){
        task = new Task("TaskovProdoyote?", "NetTolьkoTestiruem...Ozadachennoe");
    }

    @Test
    void isTaskManagerWorksAtLeastABit() {
        taskManager.addTask(task);
        final Task returnedTask = taskManager.getTask(1);
        final List<Task> tasks = taskManager.getAllTasks();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
        assertNotNull(returnedTask, "Задача не найдена.");
        assertEquals(task, returnedTask, "Задачи не совпадают.");
    }

    @Test
    void isHistoryManagerRememberSomething() {
        historyManager.addTask(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }
}