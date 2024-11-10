package servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;

import java.util.regex.Pattern;

public class HistoryHandler extends BaseHttpHandler {
    private final Gson gson;
    private final TaskManager taskManager;

    HistoryHandler(Gson gson, TaskManager taskManager) {
        this.gson = gson;
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String method = httpExchange.getRequestMethod();
            System.out.println("Началась обработка " + method + " запроса от клиента.");

            String response;
            switch (method) {
                case "GET":
                    if (Pattern.matches("^/prioritized", path) || Pattern.matches("^/prioritized/", path)) {
                        response = gson.toJson(taskManager.getPrioritizedTasks());
                        sendText(httpExchange, response, 200);
                        break;
                    }
                default: {
                    sendText(httpExchange, "Доступные методы: GET" +
                            "\nЗапрошен: " + method, 405);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }
}