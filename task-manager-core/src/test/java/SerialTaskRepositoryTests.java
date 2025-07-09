
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import taskmanager.core.exceptions.repository.AlreadyInRepositoryException;
import taskmanager.core.exceptions.repository.ObjectRepositoryException;
import taskmanager.core.models.SerializableTask;
import taskmanager.core.models.TaskPriority;
import taskmanager.core.models.TaskStatus;
import taskmanager.core.repositories.SerialTaskRepository;

class SerialTaskRepositoryTests {
    
    SerializableTask task;
    SerialTaskRepository repository;
    
    Path saveFile = Path.of("src/test/resources/task.dat");
    Path cacheFile = Path.of("src/test/resources/task_ids.dat");

    @BeforeEach
    void setup() throws IOException, ObjectRepositoryException {
        if (Files.exists(saveFile, LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(saveFile);
        }

        if (Files.exists(cacheFile,  LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(cacheFile);
        }

        repository = new SerialTaskRepository(saveFile.toAbsolutePath().toString(), cacheFile.toAbsolutePath().toString());
        repository.loadIdCache();

        task = new SerializableTask();
        task.setId(1);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setDueDate(LocalDateTime.of(LocalDate.of(2025,01,01), LocalTime.of(0, 0, 0)));
        task.setPriority(TaskPriority.MODERATE);
        task.setStatus(TaskStatus.NOT_STARTED);
        task.setCategory("Test Category");
    }

    @Test
    void testAddTask() throws ObjectRepositoryException, IOException, ClassNotFoundException {
        repository.add(task);
        
        Object object = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile.toString()))) {
            object = ois.readObject();
        } 

        assertNotNull(object);
        assertTrue(object instanceof SerializableTask);

        SerializableTask taskCopy = (SerializableTask) object;

        assertAll(
            () -> assertEquals(1, taskCopy.getId()),
            () -> assertEquals("Test Task", taskCopy.getTitle()),
            () -> assertEquals("Test Description", taskCopy.getDescription()),
            () -> assertEquals("Test Category", taskCopy.getCategory()),
            () -> assertEquals(LocalDateTime.of(2025,01,01,0,0,0), taskCopy.getDueDate()),
            () -> assertEquals(TaskPriority.MODERATE, taskCopy.getPriority()),
            () -> assertEquals(TaskStatus.NOT_STARTED, taskCopy.getStatus())
        );
    } 

    @Test
    void testAddManyTasks() throws ObjectRepositoryException, IOException, ClassNotFoundException {
        SerializableTask task2 = new SerializableTask();
        task.setId(1);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setDueDate(LocalDateTime.of(LocalDate.of(2025,01,01), LocalTime.of(0, 0, 0)));
        task.setPriority(TaskPriority.MODERATE);
        task.setStatus(TaskStatus.NOT_STARTED);
        task.setCategory("Test Category");

        task2 = new SerializableTask();
        task2.setId(2);
        task2.setTitle("Test Task 2");
        task2.setDescription("Test Description 2");
        task2.setDueDate(LocalDateTime.of(LocalDate.of(2025,01,01), LocalTime.of(0, 0, 0)));
        task2.setPriority(TaskPriority.CRITICAL);
        task2.setStatus(TaskStatus.COMPLETED);
        task2.setCategory("Test Category 2");

        repository.add(task);
        repository.add(task2);

        Object object = null;
        List<SerializableTask> tasks = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile.toString()))) {
            while (true) {
                object = ois.readObject();
                
                assertNotNull(object);
                assertTrue(object instanceof SerializableTask);

                tasks.add((SerializableTask) object);
            }
            
        } catch(EOFException e) {

        }

        assertAll(
            () -> assertEquals(1, tasks.get(0).getId()),
            () -> assertEquals("Test Task", tasks.get(0).getTitle()),
            () -> assertEquals("Test Description", tasks.get(0).getDescription()),
            () -> assertEquals("Test Category", tasks.get(0).getCategory()),
            () -> assertEquals(LocalDateTime.of(2025,01,01,0,0,0), tasks.get(0).getDueDate()),
            () -> assertEquals(TaskPriority.MODERATE, tasks.get(0).getPriority()),
            () -> assertEquals(TaskStatus.NOT_STARTED, tasks.get(0).getStatus())
        );

        assertAll(
            () -> assertEquals(2, tasks.get(1).getId()),
            () -> assertEquals("Test Task 2", tasks.get(1).getTitle()),
            () -> assertEquals("Test Description 2", tasks.get(1).getDescription()),
            () -> assertEquals("Test Category 2", tasks.get(1).getCategory()),
            () -> assertEquals(LocalDateTime.of(2025,01,01,0,0,0), tasks.get(1).getDueDate()),
            () -> assertEquals(TaskPriority.CRITICAL, tasks.get(1).getPriority()),
            () -> assertEquals(TaskStatus.COMPLETED, tasks.get(1).getStatus())
        );
    }

    @Test
    void testAddTaskFailExistingId() throws AlreadyInRepositoryException, ObjectRepositoryException {
        repository.add(task);

        assertThrows(AlreadyInRepositoryException.class, () -> {
            repository.add(task);
        });
    }

    @Test
    void getAllTasks() throws ObjectRepositoryException {
        SerializableTask task1 = new SerializableTask();
        task1.setId(1);
        task1.setTitle("Test Task 1");
        task1.setDescription("Test Description 1");
        task1.setCategory("Test Category 1");
        task1.setPriority(TaskPriority.NEGLIGIBLE);
        task1.setStatus(TaskStatus.NOT_STARTED);
        task1.setDueDate(LocalDateTime.of(2025, 01, 01, 0, 0, 0));

        SerializableTask task2 = new SerializableTask();
        task2.setId(2);
        task2.setTitle("Test Task 2");
        task2.setDescription("Test Description 2");
        task2.setCategory("Test Category 2");
        task2.setPriority(TaskPriority.LOW);
        task2.setStatus(TaskStatus.STARTED);
        task2.setDueDate(LocalDateTime.of(2025, 01, 02, 0, 0, 0));

        SerializableTask task3 = new SerializableTask();
        task3.setId(3);
        task3.setTitle("Test Task 3");
        task3.setDescription("Test Description 3");
        task3.setCategory("Test Category 3");
        task3.setPriority(TaskPriority.MODERATE);
        task3.setStatus(TaskStatus.COMPLETED);
        task3.setDueDate(LocalDateTime.of(2025, 01, 03, 0, 0, 0));

        SerializableTask task4 = new SerializableTask();
        task4.setId(4);
        task4.setTitle("Test Task 4");
        task4.setDescription("Test Description 4");
        task4.setCategory("Test Category 4");
        task4.setPriority(TaskPriority.HIGH);
        task4.setStatus(TaskStatus.NOT_COMPLETED);
        task4.setDueDate(LocalDateTime.of(2025, 01, 04, 0, 0, 0));

        SerializableTask task5 = new SerializableTask();
        task5.setId(5);
        task5.setTitle("Test Task 5");
        task5.setDescription("Test Description 5");
        task5.setCategory("Test Category 5");
        task5.setPriority(TaskPriority.CRITICAL);
        task5.setStatus(TaskStatus.NOT_STARTED);
        task5.setDueDate(LocalDateTime.of(2025, 01, 05, 0, 0, 0));

        repository.add(task1);
        repository.add(task2);
        repository.add(task3);
        repository.add(task4);
        repository.add(task5);

        List<SerializableTask> tasks = repository.getAll();
        
        assertEquals(5, tasks.size());

        assertAll(
            () -> assertEquals(1, tasks.get(0).getId()),
            () -> assertEquals("Test Task 1", tasks.get(0).getTitle()),
            () -> assertEquals("Test Description 1", tasks.get(0).getDescription()),
            () -> assertEquals("Test Category 1", tasks.get(0).getCategory()),
            () -> assertEquals(TaskPriority.NEGLIGIBLE, tasks.get(0).getPriority()),
            () -> assertEquals(TaskStatus.NOT_STARTED, tasks.get(0).getStatus()),
            () -> assertEquals(LocalDateTime.of(2025, 1, 1, 0, 0, 0), tasks.get(0).getDueDate()));

        assertAll(
            () -> assertEquals(2, tasks.get(1).getId()),
            () -> assertEquals("Test Task 2", tasks.get(1).getTitle()),
            () -> assertEquals("Test Description 2", tasks.get(1).getDescription()),
            () -> assertEquals("Test Category 2", tasks.get(1).getCategory()),
            () -> assertEquals(TaskPriority.LOW, tasks.get(1).getPriority()),
            () -> assertEquals(TaskStatus.STARTED, tasks.get(1).getStatus()),
            () -> assertEquals(LocalDateTime.of(2025, 1, 2, 0, 0, 0), tasks.get(1).getDueDate()));

        assertAll(
            () -> assertEquals(3, tasks.get(2).getId()),
            () -> assertEquals("Test Task 3", tasks.get(2).getTitle()),
            () -> assertEquals("Test Description 3", tasks.get(2).getDescription()),
            () -> assertEquals("Test Category 3", tasks.get(2).getCategory()),
            () -> assertEquals(TaskPriority.MODERATE, tasks.get(2).getPriority()),
            () -> assertEquals(TaskStatus.COMPLETED, tasks.get(2).getStatus()),
            () -> assertEquals(LocalDateTime.of(2025, 1, 3, 0, 0, 0), tasks.get(2).getDueDate()));

        assertAll(
            () -> assertEquals(4, tasks.get(3).getId()),
            () -> assertEquals("Test Task 4", tasks.get(3).getTitle()),
            () -> assertEquals("Test Description 4", tasks.get(3).getDescription()),
            () -> assertEquals("Test Category 4", tasks.get(3).getCategory()),
            () -> assertEquals(TaskPriority.HIGH, tasks.get(3).getPriority()),
            () -> assertEquals(TaskStatus.NOT_COMPLETED, tasks.get(3).getStatus()),
            () -> assertEquals(LocalDateTime.of(2025, 1, 4, 0, 0, 0), tasks.get(3).getDueDate()));
                
        assertAll(
            () -> assertEquals(5, tasks.get(4).getId()),
            () -> assertEquals("Test Task 5", tasks.get(4).getTitle()),
            () -> assertEquals("Test Description 5", tasks.get(4).getDescription()),
            () -> assertEquals("Test Category 5", tasks.get(4).getCategory()),
            () -> assertEquals(TaskPriority.CRITICAL, tasks.get(4).getPriority()),
            () -> assertEquals(TaskStatus.NOT_STARTED, tasks.get(4).getStatus()),
            () -> assertEquals(LocalDateTime.of(2025, 1, 5, 0, 0, 0), tasks.get(4).getDueDate()));
    }

    @Test
    void testGetTasksPredicate() throws ObjectRepositoryException {
        SerializableTask task1 = new SerializableTask();
        task1.setId(1);
        task1.setTitle("Test Task 1");
        task1.setDescription("Test Description 1");
        task1.setCategory("Test Category 1");
        task1.setPriority(TaskPriority.NEGLIGIBLE);
        task1.setStatus(TaskStatus.NOT_STARTED);
        task1.setDueDate(LocalDateTime.of(2025, 01, 01, 0, 0, 0));

        SerializableTask task2 = new SerializableTask();
        task2.setId(2);
        task2.setTitle("Test Task 2");
        task2.setDescription("Test Description 2");
        task2.setCategory("Test Category 2");
        task2.setPriority(TaskPriority.LOW);
        task2.setStatus(TaskStatus.STARTED);
        task2.setDueDate(LocalDateTime.of(2025, 01, 02, 0, 0, 0));

        SerializableTask task3 = new SerializableTask();
        task3.setId(3);
        task3.setTitle("Test Task 3");
        task3.setDescription("Test Description 3");
        task3.setCategory("Test Category 3");
        task3.setPriority(TaskPriority.MODERATE);
        task3.setStatus(TaskStatus.COMPLETED);
        task3.setDueDate(LocalDateTime.of(2025, 01, 03, 0, 0, 0));

        SerializableTask task4 = new SerializableTask();
        task4.setId(4);
        task4.setTitle("Test Task 4");
        task4.setDescription("Test Description 4");
        task4.setCategory("Test Category 4");
        task4.setPriority(TaskPriority.HIGH);
        task4.setStatus(TaskStatus.NOT_COMPLETED);
        task4.setDueDate(LocalDateTime.of(2025, 01, 04, 0, 0, 0));

        SerializableTask task5 = new SerializableTask();
        task5.setId(5);
        task5.setTitle("Test Task 5");
        task5.setDescription("Test Description 5");
        task5.setCategory("Test Category 5");
        task5.setPriority(TaskPriority.CRITICAL);
        task5.setStatus(TaskStatus.NOT_STARTED);
        task5.setDueDate(LocalDateTime.of(2025, 01, 05, 0, 0, 0));

        repository.add(task1);
        repository.add(task2);
        repository.add(task3);
        repository.add(task4);
        repository.add(task5);
        
        List<SerializableTask> tasks = repository.get(task -> 
            task.getId() == 1 ||
            task.getDescription().equals("Test Description 5") ||
            task.getDueDate().equals(LocalDateTime.of(2025,01,03,00,00,00)));
            
        assertEquals(3, tasks.size());
    }

    @Test
    void testGetTaskPredicativeNoMatch() throws AlreadyInRepositoryException, ObjectRepositoryException {
        SerializableTask task1 = new SerializableTask();
        task1.setId(1);
        task1.setTitle("Test Task 1");
        task1.setDescription("Test Description 1");
        task1.setCategory("Test Category 1");
        task1.setPriority(TaskPriority.NEGLIGIBLE);
        task1.setStatus(TaskStatus.NOT_STARTED);
        task1.setDueDate(LocalDateTime.of(2025, 01, 01, 0, 0, 0));

        SerializableTask task2 = new SerializableTask();
        task2.setId(2);
        task2.setTitle("Test Task 2");
        task2.setDescription("Test Description 2");
        task2.setCategory("Test Category 2");
        task2.setPriority(TaskPriority.LOW);
        task2.setStatus(TaskStatus.STARTED);
        task2.setDueDate(LocalDateTime.of(2025, 01, 02, 0, 0, 0));

        SerializableTask task3 = new SerializableTask();
        task3.setId(3);
        task3.setTitle("Test Task 3");
        task3.setDescription("Test Description 3");
        task3.setCategory("Test Category 3");
        task3.setPriority(TaskPriority.MODERATE);
        task3.setStatus(TaskStatus.COMPLETED);
        task3.setDueDate(LocalDateTime.of(2025, 01, 03, 0, 0, 0));

        SerializableTask task4 = new SerializableTask();
        task4.setId(4);
        task4.setTitle("Test Task 4");
        task4.setDescription("Test Description 4");
        task4.setCategory("Test Category 4");
        task4.setPriority(TaskPriority.HIGH);
        task4.setStatus(TaskStatus.NOT_COMPLETED);
        task4.setDueDate(LocalDateTime.of(2025, 01, 04, 0, 0, 0));

        SerializableTask task5 = new SerializableTask();
        task5.setId(5);
        task5.setTitle("Test Task 5");
        task5.setDescription("Test Description 5");
        task5.setCategory("Test Category 5");
        task5.setPriority(TaskPriority.CRITICAL);
        task5.setStatus(TaskStatus.NOT_STARTED);
        task5.setDueDate(LocalDateTime.of(2025, 01, 05, 0, 0, 0));

        repository.add(task1);
        repository.add(task2);
        repository.add(task3);
        repository.add(task4);
        repository.add(task5);
        
        List<SerializableTask> tasks = repository.get(task -> task.getId() == 1000); 
        assertEquals(0, tasks.size());
    } 

    @Test
    void testGetById() throws ObjectRepositoryException {
        repository.add(task);
        
        Optional<SerializableTask> opt = repository.get(1);
        
        assertTrue(opt.isPresent());
        SerializableTask taskCopy = opt.get();

        assertAll(
            () -> assertEquals(1, taskCopy.getId()),
            () -> assertEquals("Test Task", taskCopy.getTitle()),
            () -> assertEquals("Test Description", taskCopy.getDescription()),
            () -> assertEquals("Test Category", taskCopy.getCategory()),
            () -> assertEquals(LocalDateTime.of(2025,01,01,0,0,0), taskCopy.getDueDate()),
            () -> assertEquals(TaskPriority.MODERATE, taskCopy.getPriority()),
            () -> assertEquals(TaskStatus.NOT_STARTED, taskCopy.getStatus())
        );
    }

}
