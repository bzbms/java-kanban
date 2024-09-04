import managers.FileBackedTaskManager;
import managers.ManagerSaveException;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;

public class Main {

    public static void main(String[] args) throws ManagerSaveException {
        TaskManager taskManager = Managers.getDefaultManager();

        File file = new File("src/SavedTasks.csv");

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        Task task1 = new Task("Zadacha", "Opisanie");
        Task task2 = new Task("Zadacha2", "Opisanie");
        Task task3 = new Task("Zadacha3", "Opisanie");

        Epic taskE1 = new Epic("1EPICZadacha", "Opisanie");
        Epic taskE2 = new Epic("2EPICZadacha", "Opisanie");
        Epic taskE3 = new Epic("3EPICZadacha", "Opisanie");

        Subtask st41 = new Subtask("4SubZadacha", "Opisanie", 4);
        Subtask st42 = new Subtask("4Sub2Zadacha", "Opisanie", 4);
        Subtask st51 = new Subtask("5Sub3Zadacha", "Opisanie", 5);

        System.out.println(taskManager.addTask(task1));
        System.out.println(taskManager.addTask(task2));
        System.out.println(taskManager.addTask(task3));

        System.out.println(taskManager.addTask(taskE1));
        System.out.println(taskManager.addTask(taskE2));
        System.out.println(taskManager.addTask(taskE3));

        System.out.println(taskManager.addTask(st41));
        System.out.println(taskManager.addTask(st42));
        System.out.println(taskManager.addTask(st51));

        st42 = new Subtask("4Sub2Zadacha", "Opisanie", TaskStatus.DONE, 4, 8);
        taskManager.updateTask(st42);

        taskManager.getTask(2);
        taskManager.getEpic(4);
        taskManager.getSubtask(7);
        taskManager.getTask(1);
        taskManager.getEpic(5);
        taskManager.getSubtask(8);
        taskManager.getTask(3);
        taskManager.getEpic(6);
        taskManager.getSubtask(9);
        taskManager.getTask(2);
        taskManager.getEpic(4);
        taskManager.getSubtask(7);

        printAllTasks(taskManager);

        taskManager.removeTask(1);

        printAllTasks(taskManager);

        taskManager.removeEpic(4);

        printAllTasks(taskManager);

    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (int i = 0; i < manager.getHistory().size(); i++) {
            Task task = manager.getHistory().get(i);
            System.out.println((i + 1) + ". " + task);
        }
    }
}
