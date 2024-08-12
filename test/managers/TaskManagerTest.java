package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskManagerTest {
    static TaskManager taskManager;
    static Task task;
    static Epic epic;
    static Subtask subtask;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefaultManager();
        task = new Task("TTask", "DescriptionT");
        epic = new Epic("Etask", "DescriptionE");
        subtask = new Subtask("Stask", "DescriptionS", 2);

        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(subtask);
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
        final Task task2 = new Task("TTask2", "Description", TaskStatus.DONE, 1);
        final Epic epic2 = new Epic("Etask2", "Description2", TaskStatus.DONE, 2);
        final Subtask subtask2 = new Subtask("Stask2", "Description", TaskStatus.DONE, 2, 3);
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
    void removingShouldReturnSameTasksAndNull() {
        assertEquals(task, taskManager.removeTask(1), "Задача не та же.");
        assertNull(taskManager.getTask(1), "Задача не удалилась.");
        assertEquals(subtask, taskManager.removeSubtask(3), "Подзадача не та же.");
        assertNull(taskManager.getSubtask(3), "Подзадача не удалилась.");
        assertEquals(epic, taskManager.removeEpic(2), "Эпик не тот же.");
        assertNull(taskManager.getEpic(2), "Эпик не удалился.");
    }

    @Test
    void removingAllShouldBeNull() {
        taskManager.removeAllTasks();
        assertNull(taskManager.getTask(1), "Задача не удалилась.");
        taskManager.removeAllSubtasks();
        assertNull(taskManager.getSubtask(3), "Подзадача не удалилась.");
        taskManager.removeAllEpics();
        assertNull(taskManager.getEpic(2), "Эпик не удалился.");
    }

    @Test
    void checkingIsSubtaskCanBeLikeEpicForSelf() {
        final Subtask subtaskFakeEpic = new Subtask("FakeEpic", "Description", TaskStatus.NEW, 4, 4);

        assertEquals(-1, taskManager.addTask(subtaskFakeEpic), "Подзадача-лжеЭпик как-то добавилась. О_о");
    }

    @Test
    void addingTasksWithIdWithoutConflicts() {
        final Task taskWithId = new Task("TTask", "Description", TaskStatus.NEW, 1);
        final Epic epicWithId = new Epic("Etask", "Description", TaskStatus.NEW, 2);
        final Subtask subtaskWithId = new Subtask("Stask", "Description", TaskStatus.NEW, 2, 3);

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
