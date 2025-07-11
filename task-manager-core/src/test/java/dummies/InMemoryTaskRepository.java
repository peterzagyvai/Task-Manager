package dummies;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Predicate;

import taskmanager.core.exceptions.repository.AlreadyInRepositoryException;
import taskmanager.core.exceptions.repository.ObjectRepositoryException;
import taskmanager.core.interfaces.TaskRepositoryInterface;
import taskmanager.core.models.SerializableTask;
import taskmanager.core.models.TaskPriority;
import taskmanager.core.models.TaskStatus;

public class InMemoryTaskRepository implements TaskRepositoryInterface {

    private static final int DEFAULT_TASK_SIZE = 100;

    List<SerializableTask> tasks;
    
    public InMemoryTaskRepository(int size) {
        tasks = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            SerializableTask task = new SerializableTask();
            task.setId(i + 1);
            task.setTitle("Task Title " + (i + 1));
            task.setDescription("Task Description " + (i + 1));
            task.setCategory("Task Category " + (i + 1));
            task.setPriority(TaskPriority.MODERATE);
            task.setStatus(TaskStatus.NOT_STARTED);
            task.setDueDate(LocalDateTime.of(LocalDate.of(2025,01,01).plusDays(i), LocalTime.of(00, 00, 00)));

            tasks.add(task);
        }
    }

    public InMemoryTaskRepository() {
        this(DEFAULT_TASK_SIZE);
    }

    @Override
    public void add(SerializableTask task) throws ObjectRepositoryException, AlreadyInRepositoryException {
        for (SerializableTask sTask : tasks) {
            if (sTask.getId() == task.getId()) {
                throw new AlreadyInRepositoryException();
            }
        }
        
        tasks.add(task);
    }

    @Override
    public void update(int id, SerializableTask task) throws ObjectRepositoryException {
        for (SerializableTask serializableTask : tasks) {
            if (serializableTask.getId() == id) {

                serializableTask.setTitle(task.getTitle());
                serializableTask.setDescription(task.getDescription());
                serializableTask.setCategory(task.getCategory());
                serializableTask.setPriority(task.getPriority());
                serializableTask.setStatus(task.getStatus());
                serializableTask.setDueDate(task.getDueDate());

                return;
            }
        }
    }

    @Override
    public void delete(int id) throws ObjectRepositoryException {
        ListIterator<SerializableTask> iter = tasks.listIterator();

        while (iter.hasNext()) {
            if (iter.next().getId() == id){
                iter.remove();

                return;
            }
        }
    }

    @Override
    public List<SerializableTask> get(Predicate<SerializableTask> predicate) throws ObjectRepositoryException {
        return tasks.stream().filter(predicate).toList();
    }

    @Override
    public List<SerializableTask> getAll() throws ObjectRepositoryException {
        return tasks;
    }

    @Override
    public Optional<SerializableTask> get(int id) throws ObjectRepositoryException {
        Optional<SerializableTask> result = Optional.empty();

        for (SerializableTask task : tasks) {
            if (task.getId() == id) {
                result = Optional.of(task);
            }
        }

        return result;
    }

    public List<SerializableTask> getTasks() {
        return tasks;
    }
}
