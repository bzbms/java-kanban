package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import servers.DurationAdapter;
import servers.LocalDateTimeAdapter;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

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

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }

}
