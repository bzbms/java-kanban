package managers;

import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileBackedTaskManagerTest {

    @Test
    void savingEmptyFile() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        Task task1 = new Task("Zadacha", "Opisanie");

        assertNotNull(taskManager.getTask(taskManager.addTask(task1)));
    }

    @Test
    void loadingEmptyFile() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(file);

        assertTrue(taskManager.getAllTasks().isEmpty());
        assertTrue(taskManager.getAllEpics().isEmpty());
        assertTrue(taskManager.getAllSubtasks().isEmpty());
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void savingTasks() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        Task task1 = new Task("Task1", "Description task1", TaskStatus.NEW, 1);
        Epic taskE1 = new Epic("Epic2", "Description epic2", TaskStatus.DONE, 2);
        Subtask taskS1 = new Subtask("Subtask3", "Description subtask3", TaskStatus.DONE, 2, 3);

        taskManager.addTask(task1);
        taskManager.addTask(taskE1);
        taskManager.addTask(taskS1);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        assertEquals("id,type,name,status,description,epic", reader.readLine());
        assertEquals("1,TASK,Task1,NEW,Description task1,", reader.readLine());
        assertEquals("2,EPIC,Epic2,DONE,Description epic2,", reader.readLine());
        assertEquals("3,SUBTASK,Subtask3,DONE,Description subtask3,2", reader.readLine());
        reader.close();
    }

    @Test
    void loadingTasks() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        Task task1 = new Task("Task1", "Description task1", TaskStatus.NEW, 1);
        Epic taskE1 = new Epic("Epic2", "Description epic2", TaskStatus.DONE, 2);
        Subtask taskS1 = new Subtask("Subtask3", "Description subtask3", TaskStatus.DONE, 2, 3);

        taskManager.addTask(task1);
        taskManager.addTask(taskE1);
        taskManager.addTask(taskS1);

        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(file);

        assertEquals(taskManager.getTask(1), taskManager2.getTask(1));
        assertEquals(taskManager.getTask(2), taskManager2.getTask(2));
        assertEquals(taskManager.getTask(3), taskManager2.getTask(3));
    }

}
