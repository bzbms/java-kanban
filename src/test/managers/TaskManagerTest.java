package managers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskManagerTest {
    static TaskManager taskManager = Managers.getDefaultManager();
    static final Task task = new Task("TTask", "Description");
    static final Epic epic = new Epic("Etask", "Description");
    static final Subtask subtask = new Subtask("Stask", "Description",  2);

    @BeforeAll
    static void beforeAll() {
        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(subtask);
    }

    @Test
    void addingDifferentTasksAndGetThemById() {
        assertEquals(task, taskManager.getTask(1), "Задача не та же.");
        assertEquals(epic, taskManager.getEpic(2),"Эпик не тот же.");
        assertEquals(subtask, taskManager.getSubtask(3),"Подзадача не та же.");
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
        assertEquals(epicWithId, taskManager.getEpic(5),"Эпик с неверным ID.");
        assertEquals(subtaskWithId, taskManager.getSubtask(6),"Подзадача с неверным ID.");
        assertEquals(subtaskWithId, taskManager.getEpicSubtasks(2).get(1),"Подзадача не в том месте.");
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
