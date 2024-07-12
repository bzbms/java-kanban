public class Main {

    public static void main(String[] args) {

        SingleTask TASK1 = new SingleTask("Zadacha", "Opisanie", TaskStatus.NEW);
        SingleTask TASK2 = new SingleTask("Zadacha2", "Opisanie", TaskStatus.NEW);
        SingleTask TASK3 = new SingleTask("Zadacha", "Opisanie", TaskStatus.NEW);

        EpicTask TASKE1 = new EpicTask("1EPICZadacha", "Opisanie", TaskStatus.NEW);
        EpicTask TASKE2 = new EpicTask("2EPICZadacha", "Opisanie", TaskStatus.NEW);
        EpicTask TASKE3 = new EpicTask("3EPICZadacha", "Opisanie", TaskStatus.NEW);

        System.out.println("Добавления Эпиков, должно возвращать ID:");
        System.out.println(TaskManager.addTask(TASKE1));
        System.out.println(TaskManager.addTask(TASKE2));
        System.out.println(TaskManager.addTask(TASKE3));

        SubTask st11 = new SubTask("1SubZadacha", "Opisanie", TaskStatus.NEW, 1);
        SubTask st12 = new SubTask("1Sub2Zadacha", "Opisanie", TaskStatus.NEW, 1);
        SubTask st13 = new SubTask("1Sub3Zadacha", "Opisanie", TaskStatus.NEW, 1);

        SubTask st21 = new SubTask("2SubZadacha", "Opisanie", TaskStatus.DONE, 2);
        SubTask st22 = new SubTask("2Sub2Zadacha", "Opisanie", TaskStatus.DONE, 2);
        SubTask st23 = new SubTask("2Sub3Zadacha", "Opisanie", TaskStatus.DONE, 2);

        SubTask st31 = new SubTask("3SubZadacha", "Opisanie", TaskStatus.NEW, 3);
        SubTask st32 = new SubTask("3Sub2Zadacha", "Opisanie", TaskStatus.DONE, 3);
        SubTask st33 = new SubTask("3Sub3Zadacha", "Opisanie", TaskStatus.NEW, 3);

        // Добавления Подзадач:
        TaskManager.addTask(st11);
        TaskManager.addTask(st12);
        TaskManager.addTask(st13);

        TaskManager.addTask(st21);
        TaskManager.addTask(st22);
        TaskManager.addTask(st23);

        TaskManager.addTask(st31);
        TaskManager.addTask(st32);
        TaskManager.addTask(st33);
        System.out.println("Вывод Эпиков для проверки Статусов:");
        System.out.println(TASKE1);
        System.out.println(TASKE2);
        System.out.println(TASKE3);
        System.out.println();
        System.out.println("Получение списка Подзадач Эпика:");
        System.out.println(TaskManager.getEpicSubTasks(2));

    }
}
