package taskmanager.core.repositories;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import taskmanager.core.exceptions.repository.AddingToRepositoryFailedException;
import taskmanager.core.exceptions.repository.AlreadyInRepositoryException;
import taskmanager.core.exceptions.repository.ObjectRepositoryException;
import taskmanager.core.exceptions.repository.ReadingFromRepositoryFailedException;
import taskmanager.core.interfaces.TaskRepositoryInterface;
import taskmanager.core.io.AppendableObjectOutputStream;
import taskmanager.core.models.SerializableTask;

public class SerialTaskRepository implements TaskRepositoryInterface, AutoCloseable {

    private static final String DEFAULT_FILE_NAME = "task-manager-core/main/resources/tasks.dat";
    private static final String IDS_FILE_NAME = "task-manager-core/main/resources/tasks_ids.dat";
    private final String saveFile;
    private final String idCache;

    private static final Object syncObject = new Object();
    private static Set<Integer> idSet = null;

    public SerialTaskRepository(String fileName, String idCacheName) {
        this.saveFile = fileName;
        this.idCache = idCacheName;
    }

    public SerialTaskRepository() {
        this(DEFAULT_FILE_NAME, IDS_FILE_NAME);
    }

    @Override
    public void add(SerializableTask task) throws ObjectRepositoryException, AlreadyInRepositoryException{

        // Check if task is already in repo
        if (idSet == null) {
            loadIds();
        }

        if (idSet.contains(task.getId())) {
            throw new AlreadyInRepositoryException();
        }

        // Add if not in repo
        File file = new File(saveFile);
        boolean append = file.exists() && file.length() > 0;

        try (ObjectOutputStream oos = (append ?
            new AppendableObjectOutputStream(new BufferedOutputStream(new FileOutputStream(saveFile, true))) :
            new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(saveFile, true))))) {

            oos.writeObject(task);
            idSet.add(task.getId());
            saveIds();
        } catch (IOException e) {
            throw new AddingToRepositoryFailedException(
                    String.format("Adding task (id: %d) to repository failed.", task.getId())
            );
        }
    }


    @Override
    public void update(int id, SerializableTask task) throws ObjectRepositoryException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(int id) throws ObjectRepositoryException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<SerializableTask> get(Predicate<SerializableTask> predicate) throws ObjectRepositoryException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }
    
    @Override
    public List<SerializableTask> getAll() throws ObjectRepositoryException {
        List<SerializableTask> allTasks = new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
            while (true) {
                Object object = ois.readObject();
                if (object instanceof SerializableTask task) {
                    allTasks.add(task);
                }
            }
        } catch (EOFException e) {
            // Exit while loop
        } catch (IOException | ClassNotFoundException e) {
            throw new ReadingFromRepositoryFailedException("Reading tasks failed");
        }

        return allTasks;
    }

    @Override
    public Optional<SerializableTask> get(int id) throws ObjectRepositoryException {
        if (idSet == null) {
            loadIds();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
            while (true) {
                Object object = ois.readObject();
                if (object instanceof SerializableTask task && task.getId() == id) {
                    return Optional.of(task);
                }
            }
        } catch (EOFException e) {
            return Optional.empty();
        } catch (IOException | ClassNotFoundException e) {
            throw new ReadingFromRepositoryFailedException(String.format("Loading task (id: %d) failed", id));
        }
    }

    @Override
    public void close() throws Exception {
        saveIds();
    }
    
    public void loadIds() throws ObjectRepositoryException {
        synchronized (syncObject) {
            if (idSet != null) {
                return;
            } 
    
            File file = new File(idCache);
    
            try {
                if (file.createNewFile()) {
                    // File did not exist yet
                    idSet = new HashSet<>();
                    saveIds();
                    return;
                } 
    
            } catch (IOException e) {
                throw new ObjectRepositoryException("Caching task ids failed");
            }
    
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(idCache))) {
                idSet = (Set<Integer>) ois.readObject();
            } catch ( IOException | ClassNotFoundException e) {
                throw new ObjectRepositoryException("Caching task ids failed");
            }
        }
    }

    private void saveIds() throws ObjectRepositoryException {
        synchronized (syncObject) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(idCache))) {
                oos.writeObject(idSet);
            } catch (IOException | NoSuchElementException e) {
                throw new ObjectRepositoryException("Cahcing task ids failed");
            }
        }
    }
}
