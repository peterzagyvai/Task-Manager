package taskmanager.core.models;

import java.time.LocalDateTime;
import java.util.Optional;

public class Task {
    private int id;
    private String title;
    private Optional<String> description;
    private Optional<LocalDateTime> dueDate;
    private TaskPriority priority;
    private TaskStatus status;
    private Optional<String> category;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Optional<String> getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = Optional.ofNullable(description);
    }

    public Optional<LocalDateTime> getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = Optional.ofNullable(dueDate);
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Optional<String> getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = Optional.ofNullable(category);
    }

    
}
