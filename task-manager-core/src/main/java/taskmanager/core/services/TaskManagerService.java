package taskmanager.core.services;
import java.lang.foreign.Linker.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import taskmanager.core.exceptions.repository.AlreadyInRepositoryException;
import taskmanager.core.exceptions.repository.ObjectRepositoryException;
import taskmanager.core.exceptions.service.ServiceInitializationFailedException;
import taskmanager.core.interfaces.TaskRepositoryInterface;
import taskmanager.core.mappers.TaskMapper;
import taskmanager.core.models.SerializableTask;
import taskmanager.core.models.Task;
import taskmanager.core.models.TaskPriority;
import taskmanager.core.models.TaskStatus;

public class TaskManagerService {
    private int nextId;
    private final TaskRepositoryInterface repository;

    public TaskManagerService(TaskRepositoryInterface repository) throws ServiceInitializationFailedException {
        this.repository = repository;

        try {
            nextId = Collections.max(repository.getAll(), (t1, t2) -> t2.getId() - t1.getId()).getId() + 1;
        } catch (ObjectRepositoryException e) {
            throw new ServiceInitializationFailedException("Initializing Task manager service failed");
        }
    }

    public void create(
        String title,
        Optional<String> description,
        Optional<LocalDateTime> dueDate,
        TaskPriority priority,
        TaskStatus status,
        Optional<String> category) throws AlreadyInRepositoryException, ObjectRepositoryException {
            
        // Create new
        Task task = new Task(nextId++);
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setPriority(priority);
        task.setStatus(status);
        task.setCategory(category);

        // Add to repo
        repository.add(TaskMapper.taskToSerializable(task));
    }

    // TODO: Implement this:
    public List<Task> get(int pageNumber, int pageSize, Optional<Predicate<Task>> filter, Optional<String> orederBy) throws ObjectRepositoryException {
        List<Task> tasks = new ArrayList<>();

        if (filter.isPresent()) {
            Predicate<SerializableTask> serializableFilter = st -> filter.get().test(TaskMapper.serializableToTask(st));
            repository.get(serializableFilter).stream()
                .map(st -> TaskMapper.serializableToTask(st))
                .sorted()
        }


        return tasks;
    }
}

