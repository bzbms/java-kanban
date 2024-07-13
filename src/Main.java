import Managers.TaskManager;
import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;
import Tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager TM = new TaskManager();

        Task TASK1 = new Task("Zadacha", "Opisanie");
        Task TASK2 = new Task("Zadacha2", "Opisanie");
        Task TASK3 = new Task("Zadacha", "Opisanie");

        Epic TASKE1 = new Epic("1EPICZadacha", "Opisanie");
        Epic TASKE2 = new Epic("2EPICZadacha", "Opisanie");
        Epic TASKE3 = new Epic("3EPICZadacha", "Opisanie");

        System.out.println("Добавления Эпиков, должно возвращать ID:");
        System.out.println(TM.addTask(TASKE1));
        System.out.println(TM.addTask(TASKE2));
        System.out.println(TM.addTask(TASKE3));

/*
        Tasks.Subtask st11 = new Tasks.Subtask("1SubZadacha", "Opisanie", Tasks.TaskStatus.NEW, 1);
        Tasks.Subtask st12 = new Tasks.Subtask("1Sub2Zadacha", "Opisanie", Tasks.TaskStatus.NEW, 1);
        Tasks.Subtask st13 = new Tasks.Subtask("1Sub3Zadacha", "Opisanie", Tasks.TaskStatus.NEW, 1);

        Tasks.Subtask st21 = new Tasks.Subtask("2SubZadacha", "Opisanie", Tasks.TaskStatus.DONE, 2);
        Tasks.Subtask st22 = new Tasks.Subtask("2Sub2Zadacha", "Opisanie", Tasks.TaskStatus.DONE, 2);
        Tasks.Subtask st23 = new Tasks.Subtask("2Sub3Zadacha", "Opisanie", Tasks.TaskStatus.DONE, 2);
*/

        Subtask st31 = new Subtask("3SubZadacha", "Opisanie", TaskStatus.NEW, 3, 4);
        Subtask st32 = new Subtask("3Sub2Zadacha", "Opisanie", TaskStatus.DONE, 3, 5);
        Subtask st33 = new Subtask("3Sub3Zadacha", "Opisanie", TaskStatus.NEW, 3, 6);


/*
        // Добавления Подзадач:
        Managers.TaskManager.addTask(st11);
        Managers.TaskManager.addTask(st12);
        Managers.TaskManager.addTask(st13);

        Managers.TaskManager.addTask(st21);
        Managers.TaskManager.addTask(st22);
        Managers.TaskManager.addTask(st23);
*/

        TM.addTask(st31);
        TM.addTask(st32);
        TM.addTask(st33);

        System.out.println("Вывод Эпиков для проверки Статусов:");
  /*      System.out.println(TASKE1);
        System.out.println(TASKE2);*/
        System.out.println(TASKE3);
        System.out.println();
        System.out.println("Получение списка Подзадач Эпика:");
        System.out.println(TM.getEpicSubtasks(3));
        System.out.println(TM.getAllSubtasks());
        System.out.println();
        TM.removeEpic(3);
        System.out.println(TM.getAllSubtasks());
    }
}
