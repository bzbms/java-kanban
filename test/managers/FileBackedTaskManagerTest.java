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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    File file;

    @BeforeEach
    void beforeEach() throws IOException {
        file = File.createTempFile("tasks", ".csv");
    }

    @Test
    void savingEmptyFile() {
        TaskManager taskManager = new FileBackedTaskManager(file);
        Task task1 = new Task("Zadacha", "Opisanie", TaskStatus.NEW, 1, "11:00 01.10.2024", 30);

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
        Task task1 = new Task("Task1", "Description task1", TaskStatus.NEW, 1, "11:00 01.10.2024", 30);
        Epic taskE1 = new Epic("Epic2", "Description epic2", TaskStatus.DONE, 2, "12:00 01.10.2024", 25);
        Subtask taskS1 = new Subtask("Subtask3", "Description subtask3", TaskStatus.DONE, 2, 3, "13:00 01.10.2024", 35);

        taskManager.addTask(task1);
        taskManager.addTask(taskE1);
        taskManager.addTask(taskS1);

        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(file);

        assertEquals(taskManager.getAllTasks(), taskManager2.getAllTasks());
        assertEquals(taskManager.getAllEpics(), taskManager2.getAllEpics());
        assertEquals(taskManager.getAllSubtasks(), taskManager2.getAllSubtasks());
    }


    @Test
    public void epicStatusCalculating() {
        TaskManager taskManager = new FileBackedTaskManager(file);
        Task task = new Task("TTask", "DescriptionT", TaskStatus.NEW, 1, "11:00 01.10.2024", 30);
        Epic epic = new Epic("Etask", "DescriptionE", TaskStatus.NEW, 2, "12:00 01.10.2024", 25);
        Subtask subtask = new Subtask("Stask", "DescriptionS", TaskStatus.NEW, 2, 3, "13:00 01.10.2024", 35);

        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(subtask);

        Subtask subtask2 = new Subtask("Stask2", "S2", TaskStatus.NEW, 2, 4, "14:00 01.10.2024", 15);
        Subtask subtask3 = new Subtask("Stask3", "S3", TaskStatus.NEW, 2, 5, "15:00 01.10.2024", 25);
        taskManager.addTask(subtask2);
        taskManager.addTask(subtask3);
        assertEquals(TaskStatus.NEW, taskManager.getEpic(2).getStatus());

        subtask = new Subtask("Stask", "DescriptionS", TaskStatus.DONE, 2, 3, "13:00 01.10.2024", 35);
        subtask2 = new Subtask("Stask2", "S2", TaskStatus.DONE, 2, 4, "14:00 01.10.2024", 15);
        subtask3 = new Subtask("Stask3", "S3", TaskStatus.DONE, 2, 5, "15:00 01.10.2024", 25);
        taskManager.updateTask(subtask);
        taskManager.updateTask(subtask2);
        taskManager.updateTask(subtask3);
        assertEquals(TaskStatus.DONE, taskManager.getEpic(2).getStatus());

        subtask2 = new Subtask("Stask2", "S2", TaskStatus.NEW, 2, 4, "14:00 01.10.2024", 15);
        taskManager.updateTask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(2).getStatus());

        subtask = new Subtask("Stask", "DescriptionS", TaskStatus.IN_PROGRESS, 2, 3, "13:00 01.10.2024", 35);
        subtask2 = new Subtask("Stask2", "S2", TaskStatus.IN_PROGRESS, 2, 4, "14:00 01.10.2024", 15);
        subtask3 = new Subtask("Stask3", "S3", TaskStatus.IN_PROGRESS, 2, 5, "15:00 01.10.2024", 25);
        taskManager.updateTask(subtask);
        taskManager.updateTask(subtask2);
        taskManager.updateTask(subtask3);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(2).getStatus());
    }

    @Test
    public void timeMustIntersect() {
        TaskManager taskManager = new FileBackedTaskManager(file);
        Task task = new Task("TTask", "DescriptionT", TaskStatus.NEW, 1, "11:00 01.10.2024", 30);
        Epic epic = new Epic("Etask", "DescriptionE", TaskStatus.NEW, 2, "12:00 01.10.2024", 25);
        Subtask subtask = new Subtask("Stask", "DescriptionS", TaskStatus.NEW, 2, 3, "13:00 01.10.2024", 35);

        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(subtask);

        Task task2 = new Task("TTask", "DescriptionT", TaskStatus.NEW, 4, "11:10 01.10.2024", 30);
        Task task3 = new Task("TTask", "DescriptionT", TaskStatus.NEW, 5, "10:50 01.10.2024", 30);
        Subtask subtask2 = new Subtask("Stask", "DescriptionS", TaskStatus.NEW, 2, 6, "13:00 01.10.2024", 35);

        assertThrows(ManagerSaveException.class, () -> taskManager.addTask(task2));
        assertThrows(ManagerSaveException.class, () -> taskManager.addTask(task3));
        assertThrows(ManagerSaveException.class, () -> taskManager.addTask(subtask2));
    }

    @Test
    public void timeMustNotIntersect() {
        TaskManager taskManager = new FileBackedTaskManager(file);
        Task task = new Task("TTask", "DescriptionT", TaskStatus.NEW, 1, "11:00 01.10.2024", 30);
        Epic epic = new Epic("Etask", "DescriptionE", TaskStatus.NEW, 2, "12:00 01.10.2024", 25);
        Subtask subtask = new Subtask("Stask", "DescriptionS", TaskStatus.NEW, 2, 3, "13:00 01.10.2024", 35);

        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(subtask);

        Task task4 = new Task("TTask", "DescriptionT", TaskStatus.NEW, 7, "21:10 01.10.2024", 30);
        Subtask subtask3 = new Subtask("Stask", "DescriptionS", TaskStatus.NEW, 2, 8, "00:00 01.10.2024", 35);

        assertDoesNotThrow(() -> taskManager.addTask(task4));
        assertDoesNotThrow(() -> taskManager.addTask(subtask3));
    }
}
