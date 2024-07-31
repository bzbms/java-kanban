package managers;

public class Managers {
    // Приватный конструктор, чтобы не позволять создавать экземпляры этого класса.
    // Так класс становится более утилитарным по словам Наставника, но мы кажется ещё не проходили этого. :В
    private Managers(){}

    public static TaskManager getDefaultManager(){
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
