package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import java.io.IOException;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    File file;

    @BeforeEach
    void beforeEach() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        taskManager = new FileBackedTaskManager(file);
        initTasks();
    }

    @Test
    void savingEmptyFile() {
        TaskManager taskManager3 = new FileBackedTaskManager(file);
        Task task1 = new Task("Zadacha", "Opisanie", TaskStatus.NEW, 1, "11:00 01.10.2024", 30);

        assertNotNull(taskManager3.getTask(taskManager3.addTask(task1)));
    }

    @Test
    void loadingEmptyFile() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        TaskManager taskManager4 = FileBackedTaskManager.loadFromFile(file);
        assertTrue(taskManager4.getAllTasks().isEmpty());
        assertTrue(taskManager4.getAllEpics().isEmpty());
        assertTrue(taskManager4.getAllSubtasks().isEmpty());
        assertTrue(taskManager4.getHistory().isEmpty());
    }

    @Test
    void loadingTasks() {
        TaskManager taskManager1 = new FileBackedTaskManager(file);
        Task task1 = new Task("Task1", "Description task1", TaskStatus.NEW, 1, "11:00 01.10.2024", 30);
        Epic taskE1 = new Epic("Epic2", "Description epic2", TaskStatus.DONE, 2, "12:00 01.10.2024", 25);
        Subtask taskS1 = new Subtask("Subtask3", "Description subtask3", TaskStatus.DONE, 2, 3, "13:00 01.10.2024", 35);

        taskManager1.addTask(task1);
        taskManager1.addTask(taskE1);
        taskManager1.addTask(taskS1);

        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(file);

        assertEquals(taskManager.getAllTasks(), taskManager2.getAllTasks());
        assertEquals(taskManager.getAllEpics(), taskManager2.getAllEpics());
        assertEquals(taskManager.getAllSubtasks(), taskManager2.getAllSubtasks());
    }

}
