import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dummies.InMemoryTaskRepository;
import taskmanager.core.exceptions.repository.AlreadyInRepositoryException;
import taskmanager.core.exceptions.repository.ObjectRepositoryException;
import taskmanager.core.exceptions.service.ServiceInitializationFailedException;
import taskmanager.core.mappers.TaskMapper;
import taskmanager.core.models.Task;
import taskmanager.core.models.TaskPriority;
import taskmanager.core.models.TaskStatus;
import taskmanager.core.services.TaskManagerService;

class TaskManagerServiceTests {
    private InMemoryTaskRepository repository;
    private TaskManagerService taskService;

    @BeforeEach
    void setup() throws ServiceInitializationFailedException {
        repository = new InMemoryTaskRepository();
        taskService = new TaskManagerService(repository);
    }


    @Test
    void testCreate() throws AlreadyInRepositoryException, ObjectRepositoryException {
        taskService.create(
        "New Task",
        Optional.of("New Description"),
        Optional.of(LocalDateTime.of(2025, 01, 01, 0, 0, 0)),
        TaskPriority.LOW,
        TaskStatus.NOT_STARTED,
        Optional.empty());

        Task task = TaskMapper.serializableToTask(repository.get(101).get());
        assertEquals("New Task", task.getTitle());
        assertEquals("New Description", task.getDescription().get());
        assertEquals(LocalDateTime.of(2025, 01, 01, 0, 0, 0), task.getDueDate().get());
        assertEquals(TaskPriority.LOW, task.getPriority());
        assertEquals(TaskStatus.NOT_STARTED, task.getStatus());
        assertFalse(task.getCategory().isPresent());
    }

    @Test
    void testGet() throws ObjectRepositoryException {
        List<Task> tasks = taskService.get(1, 20, Optional.empty(), Optional.empty());

        assertEquals(20, tasks.size());

        for (int i = 1; i <= tasks.size(); i++) {
            Task task = tasks.get(i - 1);
            assertEquals(i, task.getId());
            assertEquals("Task Title " + i, task.getTitle());
            assertEquals("Task Description " + i, task.getDescription().get());
            assertEquals("Task Category " + i, task.getCategory().get());
            assertEquals(TaskPriority.MODERATE, task.getPriority());
            assertEquals(TaskStatus.NOT_STARTED, task.getStatus());
            assertEquals(LocalDateTime.of(2025, 01, 01, 00, 00, 00), task.getDueDate().get());
        }
    }
}
