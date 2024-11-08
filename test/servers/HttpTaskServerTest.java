package servers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskServerTest {
    HttpTaskServer httpTaskServer;
    Gson gson = Managers.getGson();
    TaskManager taskManager;
    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = Managers.getDefaultManager();
        httpTaskServer = new HttpTaskServer(taskManager);

        task = new Task("TAZGH", "MEGATAZGH", "10:00 27.09.2024", 59);
        epic = new Epic("EPICTAZGH", "EPIEG", "10:00 28.09.2024", 59);
        subtask = new Subtask("PODTAZGH", "PODTAZGHA", "10:00 29.09.2024", 59, 2);

        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(subtask);

        httpTaskServer.run();
    }

    @AfterEach
    void shutdown() {
        httpTaskServer.shutdown();
    }

    @Test
    void getTask() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, httpResponse.statusCode());

        Task taskFromServer = gson.fromJson(httpResponse.body(), Task.class);

        assertNotNull(taskFromServer);
        assertEquals(task, taskFromServer);
    }

    @Test
    void getEpic() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, httpResponse.statusCode());

        Epic epicFromServer = gson.fromJson(httpResponse.body(), Epic.class);

        assertNotNull(epicFromServer);
        assertEquals(epic, epicFromServer);
    }

    @Test
    void getSubtask() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, httpResponse.statusCode());

        Subtask subtaskFromServer = gson.fromJson(httpResponse.body(), Subtask.class);

        assertNotNull(subtaskFromServer);
        assertEquals(subtask, subtaskFromServer);
    }

    @Test
    void getTasksList() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, httpResponse.statusCode());

        Type type = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(httpResponse.body(), type);

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals(taskManager.getAllTasks(), tasks);
    }

    @Test
    void getEpicsList() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, httpResponse.statusCode());

        Type type = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        List<Epic> epics = gson.fromJson(httpResponse.body(), type);

        assertNotNull(epics);
        assertEquals(1, epics.size());
        assertEquals(taskManager.getAllEpics(), epics);
    }

    @Test
    void getSubtasksList() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, httpResponse.statusCode());

        Type type = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> subtasks = gson.fromJson(httpResponse.body(), type);

        assertNotNull(subtasks);
        assertEquals(1, subtasks.size());
        assertEquals(taskManager.getAllSubtasks(), subtasks);
    }

    @Test
    void removeTask() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, httpResponse.statusCode());
    }

    @Test
    void removeAllTasks() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, httpResponse.statusCode());
        assertEquals(List.of(), taskManager.getAllTasks());
    }

    // Что-то не понимаю как мне тут отправить новую задачу через сеть, каким образом добавлять что-то в .POST()
    // Что-то не вижу такого в теории, пока поспрашиваю в Пачке, но может это и не нужно или нужно не так?
  /*  @Test
    void addTask() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        Task task2 = new Task("TAZGH2", "MEGATAZGH", "10:00 27.09.2025", 59);
        String taskS = gson.toJson(task2, Task.class);

        HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).POST().build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, httpResponse.statusCode());
        assertEquals(List.of(), taskManager.getAllTasks());
    }
*/

    // А тут пока затрудняюсь как мне преобразовать из Json список с разными наследниками Task в нём...
    // epicId теряется у Подзадачи...
/*    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        Type type = new TypeToken<ArrayList<Task>>() {}.getType();
        ArrayList<Task> tasks = gson.fromJson(httpResponse.body(), type);

        assertEquals(200, httpResponse.statusCode());
        assertEquals(tasks, taskManager.getPrioritizedTasks());
    }
*/
}