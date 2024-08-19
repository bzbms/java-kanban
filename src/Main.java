import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager TM = Managers.getDefaultManager();

        Task TASK1 = new Task("Zadacha", "Opisanie");
        Task TASK2 = new Task("Zadacha2", "Opisanie");
        Task TASK3 = new Task("Zadacha3", "Opisanie");

        Epic TASKE1 = new Epic("1EPICZadacha", "Opisanie");
        Epic TASKE2 = new Epic("2EPICZadacha", "Opisanie");
        Epic TASKE3 = new Epic("3EPICZadacha", "Opisanie");

        Subtask st41 = new Subtask("4SubZadacha", "Opisanie", 4);
        Subtask st42 = new Subtask("4Sub2Zadacha", "Opisanie", 4);
        Subtask st51 = new Subtask("5Sub3Zadacha", "Opisanie", 5);

        System.out.println(TM.addTask(TASK1));
        System.out.println(TM.addTask(TASK2));
        System.out.println(TM.addTask(TASK3));

        System.out.println(TM.addTask(TASKE1));
        System.out.println(TM.addTask(TASKE2));
        System.out.println(TM.addTask(TASKE3));

        System.out.println(TM.addTask(st41));
        System.out.println(TM.addTask(st42));
        System.out.println(TM.addTask(st51));

        st42 = new Subtask("4Sub2Zadacha", "Opisanie", TaskStatus.DONE, 4, 8);
        TM.updateTask(st42);

        TM.getTask(2);
        TM.getEpic(4);
        TM.getSubtask(7);
        TM.getTask(1);
        TM.getEpic(5);
        TM.getSubtask(8);
        TM.getTask(3);
        TM.getEpic(6);
        TM.getSubtask(9);
        TM.getTask(2);
        TM.getEpic(4);
        TM.getSubtask(7);

        printAllTasks(TM);

        TM.removeTask(1);

        printAllTasks(TM);

        TM.removeEpic(4);

        printAllTasks(TM);
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
