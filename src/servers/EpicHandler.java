package servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.ManagerSaveException;
import managers.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler {
    private final Gson gson;
    private final TaskManager taskManager;

    EpicHandler(Gson gson, TaskManager taskManager) {
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