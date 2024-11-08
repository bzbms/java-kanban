package servers;

import managers.TaskType;

public class NotFoundException extends RuntimeException {
    String noTask;

    public NotFoundException(TaskType type, int id) {
        noTask = "Не найден объект " + type + " с id: " + id;
    }

    @Override
    public String getMessage() {
        return noTask;
    }

}
