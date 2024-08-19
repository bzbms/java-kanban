package tasks;

import java.util.Objects;

public class Task {
    protected String title;
    protected String description;
    protected int id;
    protected TaskStatus status;

    public Task(String title, String description) {
        this(title, description, TaskStatus.NEW, 0);
    }

    public Task(String title, String description, TaskStatus status, int id) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus (TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus () {
        return status;
    }

    public void setId(int id) {
        this.id = id;
  /*  По ТЗ-6 тут тоже хорошо бы ограничить возможность менять ID, но так ломаются некоторые старые тесты с конструктором,
  содержащим ID заранее. И вот я не уверен тогда насколько мне стоит всё перелопатить и удалить ли эти тесты уже?
       if (this.id == 0) {
            this.id = id;
        } else {
            System.out.println("ID Задачи уже установлен.");
        }*/
    }

    public int getId() {
        return id;
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
        Task task = (Task) obj;
        return id == task.id;
    }// По условию TaskManager должен считать задачи с одинаковым id одним и тем же.
    // Видимо даже если у них другие параметры каким-то образом не совпадают.

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
