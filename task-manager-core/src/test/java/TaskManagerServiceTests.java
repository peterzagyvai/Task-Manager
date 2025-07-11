import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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
            assertEquals(LocalDateTime.of(2025, 01, 01, 00, 00, 00).plusDays(i - 1), task.getDueDate().get());
        }
    }

    @Test
    void testGetSecondPage20PageSize() throws ObjectRepositoryException {
        List<Task> tasks = taskService.get(2, 20, Optional.empty(), Optional.empty());

        assertEquals(20, tasks.size());

        for (int i = 1; i <= tasks.size(); i++) {
            Task task = tasks.get(i - 1);
            assertEquals(i + 20, task.getId());
            assertEquals("Task Title " + (i + 20), task.getTitle());
            assertEquals("Task Description " + (i + 20), task.getDescription().get());
            assertEquals("Task Category " + (i + 20), task.getCategory().get());
            assertEquals(TaskPriority.MODERATE, task.getPriority());
            assertEquals(TaskStatus.NOT_STARTED, task.getStatus());
            assertEquals(LocalDateTime.of(2025, 01, 01, 00, 00, 00).plusDays(20 + i - 1), task.getDueDate().get());
        }
    }

    @Test
    void testGetLastElementOnLastPage() throws ObjectRepositoryException {
        List<Task> tasks = taskService.get(4, 33, Optional.empty(), Optional.empty());

        assertEquals(1, tasks.size());
        Task task = tasks.get(0);
        assertEquals(100, task.getId());
        assertEquals("Task Title " + (100), task.getTitle());
        assertEquals("Task Description " + (100), task.getDescription().get());
        assertEquals("Task Category " + (100), task.getCategory().get());
        assertEquals(TaskPriority.MODERATE, task.getPriority());
        assertEquals(TaskStatus.NOT_STARTED, task.getStatus());
        assertEquals(LocalDateTime.of(2025, 01, 01, 00, 00, 00).plusDays(99), task.getDueDate().get());
    }

    @Test
    void testGetFiltered() throws ObjectRepositoryException {
        List<Task> tasks = taskService.get(
            1,
            40,
            Optional.of(task -> {
                if (!task.getDueDate().isPresent()) {
                    return false;
                }

                return task.getDueDate().get().isBefore(LocalDateTime.of(2025,02,01,00,00,00));
            }), Optional.empty());

        assertEquals(31, tasks.size());
    }
}
