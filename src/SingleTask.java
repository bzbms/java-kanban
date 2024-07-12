public class SingleTask {
    protected String title;
    protected String description;
    protected int id;
    protected TaskStatus status;

    public SingleTask(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public SingleTask(String title, String description, TaskStatus status, int id) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setStatus (TaskStatus status) {
        this.status = status;
    }
    public TaskStatus getStatus () {
        return status;
    }

    @Override
    public String toString() {
        String showTask = "Задача{" +
                "title=" + title +
                ", description=" + description +
                ", status=" + status +
                ", id=" + id;
        return showTask + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SingleTask task = (SingleTask) obj;
        return id == task.id;
    }// По условию TaskManager должен считать задачи с одинаковым id одним и тем же.
    // Видимо даже если у них другие параметры каким-то образом не совпадают.

    @Override
    public int hashCode() {
        int hash = 17;

        if (title != null) {
            hash = hash + title.hashCode();
        }
        hash = hash * 31 + id;
        return hash;
    }
}
