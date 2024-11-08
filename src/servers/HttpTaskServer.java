package servers;

import managers.ManagerSaveException;
import managers.Managers;
import managers.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.google.gson.Gson;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.regex.Pattern;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static HttpServer httpServer;
    private final Gson gson;
    protected final TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefaultManager());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/", new BaseHttpHandler());
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.createContext("/epics", new EpicHandler());
        httpServer.createContext("/subtasks", new SubtaskHandler());
        httpServer.createContext("/history", new TaskHandler());
        httpServer.createContext("/prioritized", new TaskHandler());
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
        System.out.println("Сервер запущен на порту: " + PORT);
        httpServer.start();
    }

    public void shutdown() {
        httpServer.stop(3);
        System.out.println("Сервер завершил работу.");
    }

    class TaskHandler extends BaseHttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                String path = httpExchange.getRequestURI().getPath();
                String method = httpExchange.getRequestMethod();
                System.out.println("Началась обработка " + method + " запроса от клиента.");

                String response;
                switch (method) {
                    case "GET":
                        if (Pattern.matches("^/tasks", path) || Pattern.matches("^/tasks/", path)) {
                            response = gson.toJson(taskManager.getAllTasks());
                            sendText(httpExchange, response, 200);
                            break;
                        }
                        if (Pattern.matches("^/tasks/\\d+$", path)) {
                            String pathId = path.replaceFirst("/tasks/", ""); // Убираем из path начало.
                            int id = Integer.parseInt(pathId);
                            if (id != -1 && taskManager.getTask(id) != null) {
                                response = gson.toJson(taskManager.getTask(id));
                                sendText(httpExchange, response, 200);
                            }
                            break;
                        }
                        if (Pattern.matches("^/history", path) || Pattern.matches("^/history/", path)) {
                            response = gson.toJson(taskManager.getHistory());
                            sendText(httpExchange, response, 200);
                            break;
                        }
                        if (Pattern.matches("^/prioritized", path) || Pattern.matches("^/prioritized/", path)) {
                            response = gson.toJson(taskManager.getPrioritizedTasks());
                            sendText(httpExchange, response, 200);
                            break;
                        }
                        break;
                    case "DELETE":
                        if (Pattern.matches("^/tasks", path) || Pattern.matches("^/tasks/", path)) {
                            taskManager.removeAllTasks();
                            sendText(httpExchange, "Все Задачи успешно удалены.", 200);
                            break;
                        }
                        if (Pattern.matches("^/tasks/\\d+$", path)) {
                            String pathId = path.replaceFirst("/tasks/", "");
                            int id = Integer.parseInt(pathId);
                            if (id != -1 && taskManager.getTask(id) != null) {
                                taskManager.removeTask(id);
                                sendText(httpExchange, "Задача успешно удалена.", 200);
                            }
                        }
                        break;
                    case "POST":
                        if (Pattern.matches("^/tasks", path) || Pattern.matches("^/tasks/", path)) {
                            String body = readText(httpExchange);
                            Task task = gson.fromJson(body, Task.class);
                            if (task.getId() == 0) {
                                taskManager.addTask(task);
                                sendText(httpExchange, "Задача успешно добавлена.", 201);
                            } else {
                                taskManager.updateTask(task);
                                sendText(httpExchange, "Задача успешно обновлена.", 201);
                            }
                            break;
                        }
                        break;
                    default: {
                        sendText(httpExchange, "Доступные методы: GET, POST, DELETE" +
                                "\nЗапрошен: " + method, 405);
                    }
                }
            } catch (ManagerSaveException e) {
                sendHasInteractions(httpExchange, e.getMessage());
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                httpExchange.close();
            }
        }
    }

    class EpicHandler extends BaseHttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                String path = httpExchange.getRequestURI().getPath();
                String method = httpExchange.getRequestMethod();
                System.out.println("Началась обработка " + method + " запроса от клиента.");

                String response;
                switch (method) {
                    case "GET":
                        if (Pattern.matches("^/epics", path) || Pattern.matches("^/epics/", path)) {
                            response = gson.toJson(taskManager.getAllEpics());
                            sendText(httpExchange, response, 200);
                            break;
                        }
                        if (Pattern.matches("^/epics/\\d+$", path)) {
                            String pathId = path.replaceFirst("/epics/", "");
                            int id = Integer.parseInt(pathId);
                            if (id != -1 && taskManager.getEpic(id) != null) {
                                response = gson.toJson(taskManager.getEpic(id));
                                sendText(httpExchange, response, 200);
                            }
                            break;
                        }
                        break;
                    case "DELETE":
                        if (Pattern.matches("^/epics", path) || Pattern.matches("^/epics/", path)) {
                            taskManager.removeAllEpics();
                            sendText(httpExchange, "Все Эпики успешно удалены.", 200);
                            break;
                        }
                        if (Pattern.matches("^/epics/\\d+$", path)) {
                            String pathId = path.replaceFirst("/epics/", "");
                            int id = Integer.parseInt(pathId);
                            if (id != -1 && taskManager.getEpic(id) != null) {
                                taskManager.removeEpic(id);
                                sendText(httpExchange, "Эпик успешно удалён.", 200);
                            }
                        }
                        break;
                    case "POST":
                        if (Pattern.matches("^/epics", path) || Pattern.matches("^/epics/", path)) {
                            String body = readText(httpExchange);
                            Epic task = gson.fromJson(body, Epic.class);
                            if (task.getId() == 0) {
                                taskManager.addTask(task);
                                sendText(httpExchange, "Эпик успешно добавлен.", 201);
                            } else {
                                taskManager.updateTask(task);
                                sendText(httpExchange, "Эпик успешно обновлён.", 201);
                            }
                            break;
                        }
                        break;
                    default: {
                        sendText(httpExchange, "Доступные методы: GET, POST, DELETE" +
                                "\nЗапрошен: " + method, 405);
                    }
                }
            } catch (ManagerSaveException e) {
                sendHasInteractions(httpExchange, e.getMessage());
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                httpExchange.close();
            }
        }
    }

    class SubtaskHandler extends BaseHttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                String path = httpExchange.getRequestURI().getPath();
                String method = httpExchange.getRequestMethod();
                System.out.println("Началась обработка " + method + " запроса от клиента.");

                String response;
                switch (method) {
                    case "GET":
                        if (Pattern.matches("^/subtasks", path) || Pattern.matches("^/subtasks/", path)) {
                            response = gson.toJson(taskManager.getAllSubtasks());
                            sendText(httpExchange, response, 200);
                            break;
                        }
                        if (Pattern.matches("^/subtasks/\\d+$", path)) {
                            String pathId = path.replaceFirst("/subtasks/", "");
                            int id = Integer.parseInt(pathId);
                            if (id != -1 && taskManager.getSubtask(id) != null) {
                                response = gson.toJson(taskManager.getSubtask(id));
                                sendText(httpExchange, response, 200);
                            }
                            break;
                        }
                        break;
                    case "DELETE":
                        if (Pattern.matches("^/subtasks", path) || Pattern.matches("^/subtasks/", path)) {
                            taskManager.removeAllSubtasks();
                            sendText(httpExchange, "Все Подзадачи успешно удалены.", 200);
                            break;
                        }
                        if (Pattern.matches("^/subtasks/\\d+$", path)) {
                            String pathId = path.replaceFirst("/subtasks/", "");
                            int id = Integer.parseInt(pathId);
                            if (id != -1 && taskManager.getSubtask(id) != null) {
                                taskManager.removeSubtask(id);
                                sendText(httpExchange, "Подзадача успешно удалена.", 200);
                            }
                        }
                        break;
                    case "POST":
                        if (Pattern.matches("^/subtasks", path) || Pattern.matches("^/subtasks/", path)) {
                            String body = readText(httpExchange);
                            Subtask task = gson.fromJson(body, Subtask.class);
                            if (task.getId() == 0) {
                                taskManager.addTask(task);
                                sendText(httpExchange, "Подзадача успешно добавлена.", 201);
                            } else {
                                taskManager.updateTask(task);
                                sendText(httpExchange, "Подзадача успешно обновлена.", 201);
                            }
                            break;
                        }
                        break;
                    default: {
                        sendText(httpExchange, "Доступные методы: GET, POST, DELETE" +
                                "\nЗапрошен: " + method, 405);
                    }
                }
            } catch (ManagerSaveException e) {
                sendHasInteractions(httpExchange, e.getMessage());
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                httpExchange.close();
            }
        }
    }
}
