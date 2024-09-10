package managers;

public class ManagerSaveException extends RuntimeException {
    String message;

    public ManagerSaveException(String message, Exception e) {
        super(e);
        this.message = message;
    }

}
