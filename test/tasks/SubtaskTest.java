package tasks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    @Test
    void isSubsAreEqualsIfTheIdIs() {
        final Subtask subtask1 = new Subtask("Пододелывать ТЗ-5 вечерком", "Нупододелывовай", TaskStatus.NEW, 2, 1);
        final Subtask subtask2 = new Subtask("Или не сопротивляться сну", "и досмотреть в нём сериал <Кажущиеся необходимости. Не просыпайся пока не сделаешь>", TaskStatus.IN_PROGRESS, 3, 1);
        assertEquals(subtask1, subtask2, "Задачи с одинаковым id разные, как зебра и тигр!");
    }

}