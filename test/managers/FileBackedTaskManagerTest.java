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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileBackedTaskManagerTest {
    File file;

    @BeforeEach
    void beforeEach() throws IOException {
        file = File.createTempFile("tasks", ".csv");
    }

    @Test
    void savingEmptyFile() {
        TaskManager taskManager = new FileBackedTaskManager(file);
        Task task1 = new Task("Zadacha", "Opisanie");

        assertNotNull(taskManager.getTask(taskManager.addTask(task1)));
    }

    @Test
    void loadingEmptyFile() {
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);

        assertTrue(taskManager.getAllTasks().isEmpty());
        assertTrue(taskManager.getAllEpics().isEmpty());
        assertTrue(taskManager.getAllSubtasks().isEmpty());
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void loadingTasks() {
        TaskManager taskManager = new FileBackedTaskManager(file);
        Task task1 = new Task("Task1", "Description task1", TaskStatus.NEW, 1);
        Epic taskE1 = new Epic("Epic2", "Description epic2", TaskStatus.DONE, 2);
        Subtask taskS1 = new Subtask("Subtask3", "Description subtask3", TaskStatus.DONE, 2, 3);

        taskManager.addTask(task1);
        taskManager.addTask(taskE1);
        taskManager.addTask(taskS1);

        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(file);

        assertEquals(taskManager.getAllTasks(), taskManager2.getAllTasks());
        assertEquals(taskManager.getAllEpics(), taskManager2.getAllEpics());
        assertEquals(taskManager.getAllSubtasks(), taskManager2.getAllSubtasks());
    }

}
