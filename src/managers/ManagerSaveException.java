package managers;

import java.io.IOException;

public class ManagerSaveException extends IOException {
    String message;

    public ManagerSaveException(String message, Exception e) {
        super(e);
        this.message = message;
    }

    public void getDetailedMessage() {
        System.out.println("Ошибка сохранения в файл: " + getMessage());
    }

}
