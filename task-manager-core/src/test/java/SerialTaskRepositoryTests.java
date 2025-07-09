
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void setup() throws IOException {
        if (Files.exists(saveFile, LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(saveFile);
        }

        if (Files.exists(cacheFile,  LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(cacheFile);
        }

        repository = new SerialTaskRepository(saveFile.toAbsolutePath().toString(), cacheFile.toAbsolutePath().toString());

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
    void testSaveTask() throws ObjectRepositoryException, IOException, ClassNotFoundException {
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
    void testSaveManyTasks() throws ObjectRepositoryException, IOException, ClassNotFoundException {
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
