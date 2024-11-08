package servers;

import managers.TaskType;

public class NotFoundException extends RuntimeException {
    String noTask;

    public NotFoundException(TaskType Type, int id) {
        noTask = "Не найден объект " + Type + " с id: " + id;
    }

    @Override
    public String getMessage() {
        return noTask;
    }

}
