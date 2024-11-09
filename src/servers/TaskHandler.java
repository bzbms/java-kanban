package servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.ManagerSaveException;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler {
    private final Gson gson;
    private final TaskManager taskManager;

    TaskHandler(Gson gson, TaskManager taskManager) {
        this.gson = gson;
        this.taskManager = taskManager;
    }

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