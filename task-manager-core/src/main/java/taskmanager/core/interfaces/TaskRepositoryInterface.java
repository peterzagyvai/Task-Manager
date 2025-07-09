package taskmanager.core.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import taskmanager.core.exceptions.repository.AlreadyInRepositoryException;
import taskmanager.core.exceptions.repository.ObjectRepositoryException;
import taskmanager.core.models.SerializableTask;

public interface TaskRepositoryInterface {
    void add(SerializableTask task) throws ObjectRepositoryException, AlreadyInRepositoryException;
    void update(int id, SerializableTask task) throws ObjectRepositoryException;
    void delete(int id) throws ObjectRepositoryException;
    List<SerializableTask> get(Predicate<SerializableTask> predicate) throws ObjectRepositoryException;
    List<SerializableTask> getAll() throws ObjectRepositoryException;
    Optional<SerializableTask> get(int id) throws ObjectRepositoryException;
}
