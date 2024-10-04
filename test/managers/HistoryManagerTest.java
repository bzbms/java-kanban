package managers;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class HistoryManagerTest {
    @Test
    void addingToHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("TaskovProdoyote?", "NetTolьkoTestiruem...Ozadachennoe", TaskStatus.NEW, 1, "11:00 01.10.2024", 30);

        historyManager.addTask(task);
        assertNotNull(historyManager.getHistory().get(0), "Задача не добавилась.");
    }

    @Test
    void removingFromHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("TaskovProdoyote?", "NetTolьkoTestiruem...Ozadachennoe", "11:00 01.10.2024", 30);

        historyManager.addTask(task);
        historyManager.removeTask(0);
        assertTrue(historyManager.getHistory().isEmpty(), "В истории осталась задача.");
    }

    @Test
    void linkedListWorksRight() {
        TaskManager taskManager = Managers.getDefaultManager();
        Task task1 = new Task("T1", "", "11:00 01.10.2024", 30);
        Epic taskE1 = new Epic("E1", "", "12:00 01.10.2024", 25);
        Epic taskE2 = new Epic("E2", "", "13:00 01.10.2024", 25);
        Subtask taskS1 = new Subtask("S1", "", "14:00 01.10.2024", 25, 2);
        taskManager.addTask(task1);
        taskManager.addTask(taskE1);
        taskManager.addTask(taskE2);
        taskManager.addTask(taskS1);

        taskManager.getTask(1);
        taskManager.getEpic(2);
        taskManager.getEpic(3);
        taskManager.getSubtask(4);
        taskManager.getEpic(2);
        taskManager.getEpic(3);
        taskManager.getTask(1);

        assertEquals(List.of(taskS1, taskE1, taskE2, task1), taskManager.getHistory(), "История заполняется неверно.");
    }

    @Test
    void removeTaskFromBegin() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task1 = new Task("T1", "", TaskStatus.NEW, 1);
        Task task2 = new Task("T2", "", TaskStatus.NEW, 2);
        Task task3 = new Task("T3", "", TaskStatus.NEW, 3);

        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);
        historyManager.removeTask(1);

        assertEquals(List.of(task2, task3), historyManager.getHistory(), "Первая задача удалилась неправильно.");
    }

    @Test
    void removeTaskFromMiddle() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task1 = new Task("T1", "", TaskStatus.NEW, 1, "11:00 01.10.2024", 30);
        Task task2 = new Task("T2", "", TaskStatus.NEW, 2, "12:00 01.10.2024", 25);
        Task task3 = new Task("T3", "", TaskStatus.NEW, 3, "13:00 01.10.2024", 35);

        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);
        historyManager.removeTask(2);

        assertEquals(List.of(task1, task3), historyManager.getHistory(), "Задача из середины удалилась неправильно.");
    }

    @Test
    void removeTaskFromEnd() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task1 = new Task("T1", "", TaskStatus.NEW, 1, "11:00 01.10.2024", 30);
        Task task2 = new Task("T2", "", TaskStatus.NEW, 2, "12:00 01.10.2024", 25);
        Task task3 = new Task("T3", "", TaskStatus.NEW, 3, "13:00 01.10.2024", 35);

        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);
        historyManager.removeTask(3);

        assertEquals(List.of(task1, task2), historyManager.getHistory(), "Последняя задача удалилась неправильно.");
    }

    @Test
    void removingFromEmptyHistoryShouldHaveNoError() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertTrue(historyManager.getHistory().isEmpty(), "История не была пуста.");
        historyManager.removeTask(0);
    }
}