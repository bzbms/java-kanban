package servers;

import managers.TaskType;

public class NotFoundException extends RuntimeException {
    String exc;

    public NotFoundException(TaskType type, int id) {
        exc = "Не найден объект " + type + " с id: " + id;
    }

    @Override
    public String getMessage() {
        return exc;
    }

}
