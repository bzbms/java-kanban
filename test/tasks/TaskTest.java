package tasks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    void isTasksAreEqualsIfTheIdIs() {
        final Task task1 = new Task("Протестировать ТЗ-5", "Давай-давай", TaskStatus.IN_PROGRESS, 1);
        final Task task2 = new Task("Или пойти рубануть во что-нть..", "Динамичненькое, стрелялочьное...", TaskStatus.NEW, 1);
        assertEquals(task1, task2, "Задачи с одинаковым id разные, так низззя!");
    }

}