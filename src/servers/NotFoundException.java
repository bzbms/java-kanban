package servers;

import managers.TaskType;

public class NotFoundException extends RuntimeException {
    String noTask;

    public NotFoundException(TaskType TASK, int id) {
        noTask = "Не найден объект " + TASK + " с id: " + id;
    }

    @Override
    public String getMessage() {
        return noTask;
    }

}
