package taskmanager.core.services;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
            nextId = Collections.max(repository.getAll(), (t1, t2) -> t1.getId() - t2.getId()).getId() + 1;
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

    public List<Task> get(int pageNumber, int pageSize, Optional<Predicate<Task>> filter, Optional<List<TaskOrder>> orderBy) throws ObjectRepositoryException {
        Stream<Task> tasks;
        List<SerializableTask> dataRead;

        // Get data
        if (filter.isPresent()) {
            Predicate<SerializableTask> serializableFilter = st -> filter.get().test(TaskMapper.serializableToTask(st));
            dataRead = repository.get(serializableFilter);
        }
        else {
            dataRead = repository.getAll();
        }

        // map
        tasks = dataRead.stream().map(TaskMapper::serializableToTask);

        // sort
        tasks = sortTasks(tasks, orderBy);

        // pagination
        List<Task> list = tasks.toList();

        // set min
        pageNumber = (pageNumber >= 1 ? pageNumber : 1);

        // set max
        int maxPageNumber = list.size() / pageSize + 1;
        pageNumber = (pageNumber <= maxPageNumber ? pageNumber : maxPageNumber);

        int firstIndex = (pageNumber - 1) * pageSize;
        int lastIndex = pageNumber * pageSize;
        if (lastIndex > list.size() - 1) {
            lastIndex = list.size();
        }
        
        return list.subList(firstIndex, lastIndex);
    }

    private static Stream<Task> sortTasks(Stream<Task> originalTasks, Optional<List<TaskOrder>> orderBy) {
        Stream<Task> tasks = originalTasks;

        if (!orderBy.isPresent()) {
            return tasks;
        }

        List<TaskOrder> orderList = orderBy.get();

        for (int i = orderList.size(); i >= 0; i--) {
            Comparator<Task> cmp = null;

            switch (orderList.get(i)) {
                case TITLE:
                    cmp = (t1, t2) -> t1.getTitle().compareTo(t2.getTitle());
                    break;
                case DUE_DATE:
                    cmp = (t1, t2) -> {
                        LocalDateTime t1Date = t1.getDueDate().orElse(LocalDateTime.MIN);
                        LocalDateTime t2Date = t1.getDueDate().orElse(LocalDateTime.MIN);

                        return t1Date.compareTo(t2Date);
                    };
                    break;
            
                case PRIORITY:
                    cmp = (t1, t2) -> t1.getPriority().compareTo(t2.getPriority());
                    break;
                    
                case STATUS: 
                    cmp = (t1, t2) -> t1.getStatus().compareTo(t2.getStatus());
                    break;

                case CATEGORY:
                    cmp = (t1, t2) -> {
                        String t1Category = t1.getCategory().orElse("");
                        String t2Category = t2.getCategory().orElse("");

                        return t1Category.compareTo(t2Category);
                    };
                    break;
            
                default:
                    break;
            }

            if (cmp != null) {
                tasks = tasks.sorted(cmp);
            }
        }

        return tasks;
    }
}

