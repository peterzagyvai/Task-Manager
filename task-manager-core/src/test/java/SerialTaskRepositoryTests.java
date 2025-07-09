
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import taskmanager.core.exceptions.repository.AlreadyInRepositoryException;
import taskmanager.core.exceptions.repository.ObjectRepositoryException;
import taskmanager.core.models.SerializableTask;
import taskmanager.core.models.TaskPriority;
import taskmanager.core.models.TaskStatus;
import taskmanager.core.repositories.SerialTaskRepository;

public class SerialTaskRepositoryTests {
    
    SerializableTask task;
    SerialTaskRepository repository;
    
    Path saveFile = Path.of("src/test/resources/task.dat");
    Path cacheFile = Path.of("src/test/resources/task_ids.dat");

    @BeforeEach
    void setup() throws ObjectRepositoryException, IOException {
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
    void testSaveTask() throws AlreadyInRepositoryException, ObjectRepositoryException, FileNotFoundException, IOException, ClassNotFoundException {
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
    void testGetById() throws AlreadyInRepositoryException, ObjectRepositoryException {
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
