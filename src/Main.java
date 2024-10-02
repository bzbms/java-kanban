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

        Task task1 = new Task("Zadacha", "Opisanie", "10:00 27.09.2024", 59);
        Task task2 = new Task("Zadacha2", "Opisanie", "11:00 27.09.2024", 0);
        Task task3 = new Task("Zadacha3", "Opisanie", "11:00 27.09.2024", 0);

        Epic taskE1 = new Epic("1EPICZadacha", "Opisanie", "10:00 28.09.2024", 59);
        Epic taskE2 = new Epic("2EPICZadacha", "Opisanie", "12:00 27.09.2024", 59);
        Epic taskE3 = new Epic("3EPICZadacha", "Opisanie", "11:00 28.09.2024", 59);

        Subtask st41 = new Subtask("4SubZadacha", "Opisanie", "10:00 29.09.2024", 59, 4);
        Subtask st42 = new Subtask("4Sub2Zadacha", "Opisanie", "12:00 29.09.2024", 59, 4);
        Subtask st51 = new Subtask("5Sub3Zadacha", "Opisanie", "12:00 28.09.2024", 59, 5);

        System.out.println(fileBackedTaskManager.addTask(task1));
        System.out.println(fileBackedTaskManager.addTask(task2));
        System.out.println(fileBackedTaskManager.addTask(task3));

        System.out.println(fileBackedTaskManager.addTask(taskE1));
        System.out.println(fileBackedTaskManager.addTask(taskE2));
        System.out.println(fileBackedTaskManager.addTask(taskE3));

        System.out.println(fileBackedTaskManager.addTask(st41));
        System.out.println(fileBackedTaskManager.addTask(st42));
        System.out.println(fileBackedTaskManager.addTask(st51));

        st42 = new Subtask("4Sub2Zadacha", "Opisanie", TaskStatus.DONE, 4, 8, "12:00 29.09.2024", 59);
        fileBackedTaskManager.updateTask(st42);

        fileBackedTaskManager.getTask(2);
        fileBackedTaskManager.getEpic(3);
        fileBackedTaskManager.getSubtask(5);
        fileBackedTaskManager.getTask(1);
        fileBackedTaskManager.getEpic(4);
        fileBackedTaskManager.getSubtask(8);
        fileBackedTaskManager.getTask(3);
        fileBackedTaskManager.getEpic(6);
        fileBackedTaskManager.getSubtask(9);
        fileBackedTaskManager.getTask(2);
        fileBackedTaskManager.getEpic(4);
        fileBackedTaskManager.getSubtask(7);

       // printAllTasks(fileBackedTaskManager);
        System.out.println(fileBackedTaskManager.getEpic(4));
        System.out.println(fileBackedTaskManager.getEpicSubtasks(4));
        System.out.println(fileBackedTaskManager.getEpic(4).updateTime(fileBackedTaskManager.getEpicSubtasks(4)));

        System.out.println();
        System.out.println();
        fileBackedTaskManager.removeAllTasks();
        System.out.println(fileBackedTaskManager.getPrioritizedTasks());

/*

        printAllTasks(fileBackedTaskManager);
        System.out.println();
        System.out.println();
        for (Task prioritizedTask : taskManager.getPrioritizedTasks()) {
            System.out.println(prioritizedTask);
        }
*/


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
