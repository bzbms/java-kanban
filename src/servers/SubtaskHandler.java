package servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.ManagerSaveException;
import managers.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.util.regex.Pattern;

public class SubtaskHandler extends BaseHttpHandler {
    private final Gson gson;
    private final TaskManager taskManager;

    SubtaskHandler(Gson gson, TaskManager taskManager) {
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
