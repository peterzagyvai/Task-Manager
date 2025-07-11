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

    public Task(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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

    public void setDescription(Optional<String> description) {
        description.ifPresentOrElse(descriptionString -> {
            if (descriptionString.isBlank()) {
                this.description = Optional.empty();
            } else {
                this.description = description;
            }
        }, () -> {
            this.description = Optional.empty();
        });
    }

    public Optional<LocalDateTime> getDueDate() {
        return dueDate;
    }

    public void setDueDate(Optional<LocalDateTime> dueDate) {
        this.dueDate = dueDate;
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

    public void setCategory(Optional<String> category) {
        category.ifPresentOrElse(categoryString -> {
            if (categoryString.isBlank()) {
                this.category = Optional.empty();
            } else {
                this.category = category;
            }
        }, () -> {
            this.category = Optional.empty();
        });
    }

    
}
