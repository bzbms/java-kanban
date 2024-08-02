package tasks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @Test
    void isEpicsAreEqualsIfTheIdIs() {
        final Epic epic1 = new Epic("Заняться ТЗ-5", "Надо бы", TaskStatus.IN_PROGRESS, 1);
        final Epic epic2 = new Epic("Или пойти в сарай и доделать штопицот летних дел", "штопицотадно!", TaskStatus.NEW, 1);
        assertEquals(epic1, epic2, "Задачи с одинаковым id разные, как же скроешь свои разбиения на тёмную и светлую стороны?!");
    }

}