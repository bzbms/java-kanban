package managers;

import java.io.File;

public final class Managers {

    private Managers() {
    }

    public static TaskManager getDefaultManager() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getDefaultManagerFromFile() {
        return new FileBackedTaskManager(new File("src/SavedTasks.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
