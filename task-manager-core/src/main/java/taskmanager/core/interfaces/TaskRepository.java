package taskmanager.core.interfaces;

import java.util.List;
import java.util.function.Predicate;

import taskmanager.core.models.Task;

public interface TaskRepository {
    void add(Task task);
    void update(int id, Task task);
    void delete(int id);
    List<Task> get(Predicate<Task> predicate);
    Task get(int id);
}
