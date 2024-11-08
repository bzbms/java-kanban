package managers;

import org.junit.jupiter.api.Test;
import servers.NotFoundException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    public void initTasks() {
        task = new Task("TTask", "DescriptionT", TaskStatus.NEW, 1, "11:00 01.10.2024", 30);
        epic = new Epic("Etask", "DescriptionE", TaskStatus.NEW, 2, "12:00 01.10.2024", 25);
        subtask = new Subtask("Stask", "DescriptionS", TaskStatus.NEW, 2, 3, "13:00 01.10.2024", 35);

        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(subtask);
    }

    @Test
    public void epicStatusCalculating() {
        Subtask subtask2 = new Subtask("Stask2", "S2", TaskStatus.NEW, 2, 4, "14:00 01.10.2024", 15);
        Subtask subtask3 = new Subtask("Stask3", "S3", TaskStatus.NEW, 2, 5, "15:00 01.10.2024", 25);
        taskManager.addTask(subtask2);
        taskManager.addTask(subtask3);
        assertEquals(TaskStatus.NEW, taskManager.getEpic(2).getStatus());

        subtask = new Subtask("Stask", "DescriptionS", TaskStatus.DONE, 2, 3, "13:00 01.10.2024", 35);
        subtask2 = new Subtask("Stask2", "S2", TaskStatus.DONE, 2, 4, "14:00 01.10.2024", 15);
        subtask3 = new Subtask("Stask3", "S3", TaskStatus.DONE, 2, 5, "15:00 01.10.2024", 25);
        taskManager.updateTask(subtask);
        taskManager.updateTask(subtask2);
        taskManager.updateTask(subtask3);
        assertEquals(TaskStatus.DONE, taskManager.getEpic(2).getStatus());

        subtask2 = new Subtask("Stask2", "S2", TaskStatus.NEW, 2, 4, "14:00 01.10.2024", 15);
        taskManager.updateTask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(2).getStatus());

        subtask = new Subtask("Stask", "DescriptionS", TaskStatus.IN_PROGRESS, 2, 3, "13:00 01.10.2024", 35);
        subtask2 = new Subtask("Stask2", "S2", TaskStatus.IN_PROGRESS, 2, 4, "14:00 01.10.2024", 15);
        subtask3 = new Subtask("Stask3", "S3", TaskStatus.IN_PROGRESS, 2, 5, "15:00 01.10.2024", 25);
        taskManager.updateTask(subtask);
        taskManager.updateTask(subtask2);
        taskManager.updateTask(subtask3);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(2).getStatus());
    }

    @Test
    public void timeMustIntersect() {
        Task task2 = new Task("TTask", "DescriptionT", TaskStatus.NEW, 4, "11:10 01.10.2024", 30);
        Task task3 = new Task("TTask", "DescriptionT", TaskStatus.NEW, 5, "10:50 01.10.2024", 30);
        Subtask subtask2 = new Subtask("Stask", "DescriptionS", TaskStatus.NEW, 2, 6, "13:00 01.10.2024", 35);

        assertThrows(ManagerSaveException.class, () -> taskManager.addTask(task2));
        assertThrows(ManagerSaveException.class, () -> taskManager.addTask(task3));
        assertThrows(ManagerSaveException.class, () -> taskManager.addTask(subtask2));
    }

    @Test
    public void timeMustNotIntersect() {
        Task task4 = new Task("TTask", "DescriptionT", TaskStatus.NEW, 7, "21:10 01.10.2024", 30);
        Subtask subtask3 = new Subtask("Stask", "DescriptionS", TaskStatus.NEW, 2, 8, "00:00 01.10.2024", 35);

        assertDoesNotThrow(() -> taskManager.addTask(task4));
        assertDoesNotThrow(() -> taskManager.addTask(subtask3));
    }

    @Test
    void managersShouldBeReadyToWork() {
        final HistoryManager historymanager = Managers.getDefaultHistory();

        historymanager.addTask(task);
        assertNotNull(taskManager.getTask(1), "Менеджер задач не работает.");
        assertNotNull(historymanager.getHistory().get(0), "Менеджер истории не работает.");
    }

    @Test
    void gettingShouldReturnSameTasks() {
        assertEquals(task, taskManager.getTask(1), "Задача не та же.");
        assertEquals(epic, taskManager.getEpic(2), "Эпик не тот же.");
        assertEquals(subtask, taskManager.getSubtask(3), "Подзадача не та же.");
    }

    @Test
    void gettingEpicSubtasksShouldReturnSubtask() {
        final ArrayList<Subtask> subtasks = taskManager.getEpicSubtasks(2);
        assertEquals(subtasks.get(0), subtask, "Подзадача не вернулась из списка Эпика.");
    }

    @Test
    void updatingShouldReturnNewTask() {
        final Task task2 = new Task("TTask2", "Description", TaskStatus.DONE, 1, "11:00 01.10.2024", 30);
        final Epic epic2 = new Epic("Etask2", "Description2", TaskStatus.DONE, 2, "12:00 01.10.2024", 25);
        final Subtask subtask2 = new Subtask("Stask2", "Description", TaskStatus.DONE, 2, 3, "13:00 01.10.2024", 35);
        final String taskTitle = task2.getTitle();
        final String epicDescription = epic2.getDescription();
        final TaskStatus subtaskStatus = subtask2.getStatus();

        taskManager.updateTask(task2);
        taskManager.updateTask(epic2);
        taskManager.updateTask(subtask2);
        assertEquals(taskTitle, taskManager.getTask(1).getTitle(), "Название задачи не обновилось.");
        assertEquals(epicDescription, taskManager.getEpic(2).getDescription(), "Описание эпика не обновилось.");
        assertEquals(subtaskStatus, taskManager.getSubtask(3).getStatus(), "Статус подзадачи не обновился.");
    }

    @Test
    void removingShouldReturnSameTasksAndThrow() {
        assertEquals(task, taskManager.removeTask(1), "Задача не та же.");
        assertThrows(NotFoundException.class, () -> taskManager.getTask(1), "Задача не удалилась.");
        assertEquals(subtask, taskManager.removeSubtask(3), "Подзадача не та же.");
        assertThrows(NotFoundException.class, () -> taskManager.getSubtask(3), "Подзадача не удалилась.");
        assertEquals(epic, taskManager.removeEpic(2), "Эпик не тот же.");
        assertThrows(NotFoundException.class, () -> taskManager.getEpic(2), "Эпик не удалился.");
    }

    @Test
    void removingAllShouldBeEmpty() {
        taskManager.removeAllTasks();
        assertEquals(List.of(), taskManager.getAllTasks(), "Задача не удалилась.");
        taskManager.removeAllSubtasks();
        assertEquals(List.of(), taskManager.getAllSubtasks(), "Подзадача не удалилась.");
        taskManager.removeAllEpics();
        assertEquals(List.of(), taskManager.getAllEpics(), "Эпик не удалился.");
    }

    @Test
    void checkingIsSubtaskCanBeLikeEpicForSelf() {
        final Subtask subtaskFakeEpic = new Subtask("FakeEpic", "Description", TaskStatus.NEW, 4, 4);

        assertThrows(NotFoundException.class, () -> taskManager.addTask(subtaskFakeEpic), "Подзадача-лжеЭпик как-то добавилась. О_о");
    }

    @Test
    void addingTasksWithIdWithoutConflicts() {
        final Task taskWithId = new Task("TTask", "Description", TaskStatus.NEW, 1, "11:31 01.10.2024", 30);
        final Epic epicWithId = new Epic("Etask", "Description", TaskStatus.NEW, 2, "12:26 01.10.2024", 25);
        final Subtask subtaskWithId = new Subtask("Stask", "Description", TaskStatus.NEW, 2, 3, "14:00 01.10.2024", 35);

        taskManager.addTask(taskWithId);
        taskManager.addTask(epicWithId);
        taskManager.addTask(subtaskWithId);
        assertEquals(taskWithId, taskManager.getTask(4), "Задача с неверным ID.");
        assertEquals(epicWithId, taskManager.getEpic(5), "Эпик с неверным ID.");
        assertEquals(subtaskWithId, taskManager.getSubtask(6), "Подзадача с неверным ID.");
        assertEquals(subtaskWithId, taskManager.getEpicSubtasks(2).get(1), "Подзадача не в том месте.");
    }

    @Test
    void checkTasksVariablesAfterAdding() {
        assertEquals(task.getTitle(), taskManager.getTask(1).getTitle(), "Название не сходится.");
        assertEquals(task.getDescription(), taskManager.getTask(1).getDescription(), "Описание не соответствует.");
        assertEquals(task.getStatus(), taskManager.getTask(1).getStatus(), "Статус не тот же.");
        assertEquals(task.getId(), taskManager.getTask(1).getId(), "Да куда там ID разъехалось? Как? КАГ???");

        assertEquals(subtask.getEpicId(), taskManager.getSubtask(3).getEpicId(), "Epic-ID Подзадачи - неверный! Предателько!");
        assertEquals(epic.getSubtasksIds().get(0), taskManager.getEpic(2).getSubtasksIds().get(0), "Подзадача в Эпике либо не в Эпике, либо не Подзадача!11");
    }
}