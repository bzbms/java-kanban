package servers;

import managers.Managers;
import managers.TaskManager;
import com.sun.net.httpserver.HttpServer;
import com.google.gson.Gson;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.net.InetSocketAddress;
import java.io.IOException;

public class HttpTaskServer {
    private final int port = 8080;
    private final HttpServer httpServer;
    private final Gson gson;
    protected final TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefaultManager());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        httpServer = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        httpServer.createContext("/", new BaseHttpHandler());
        httpServer.createContext("/tasks", new TaskHandler(gson, taskManager));
        httpServer.createContext("/epics", new EpicHandler(gson, taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(gson, taskManager));
        httpServer.createContext("/history", new HistoryHandler(gson, taskManager));
        httpServer.createContext("/prioritized", new PriorityHandler(gson, taskManager));
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefaultManager();
        Task task1 = new Task("Zadacha", "Opisanie", "10:00 27.09.2024", 59);
        Task task2 = new Task("Zadacha2", "Opisanie", "11:00 27.09.2024", 0);
        Task task3 = new Task("Zadacha3", "Opisanie", "11:01 27.09.2024", 0);

        Epic taskE1 = new Epic("1EPICZadacha", "Opisanie", "10:00 28.09.2024", 59);
        Epic taskE2 = new Epic("2EPICZadacha", "Opisanie", "12:00 27.09.2024", 59);
        Epic taskE3 = new Epic("3EPICZadacha", "Opisanie", "13:00 27.09.2024", 59);

        Subtask st41 = new Subtask("4SubZadacha", "Opisanie", "10:00 29.09.2024", 59, 4);
        Subtask st42 = new Subtask("4Sub2Zadacha", "Opisanie", "12:00 29.09.2024", 59, 4);
        Subtask st51 = new Subtask("5Sub3Zadacha", "Opisanie", "12:00 28.09.2024", 59, 5);

        System.out.println(taskManager.addTask(task1));
        System.out.println(taskManager.addTask(task2));
        System.out.println(taskManager.addTask(task3));

        System.out.println(taskManager.addTask(taskE1));
        System.out.println(taskManager.addTask(taskE2));
        System.out.println(taskManager.addTask(taskE3));

        System.out.println(taskManager.addTask(st41));
        System.out.println(taskManager.addTask(st42));
        System.out.println(taskManager.addTask(st51));
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.run();
    }

    public void run() {
        System.out.println("Сервер запущен на порту: " + port);
        httpServer.start();
    }

    public void shutdown() {
        httpServer.stop(3);
        System.out.println("Сервер завершил работу.");
    }

}
