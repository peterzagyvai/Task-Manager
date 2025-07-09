package taskmanager.core.interfaces;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import taskmanager.core.exceptions.repository.AlreadyInRepositoryException;
import taskmanager.core.exceptions.repository.ObjectRepositoryException;
import taskmanager.core.models.SerializableTask;

public interface TaskRepositoryInterface {
    void add(SerializableTask task) throws ObjectRepositoryException, AlreadyInRepositoryException;
    void update(int id, SerializableTask task) throws ObjectRepositoryException;
    void delete(int id) throws ObjectRepositoryException;
    Stream<SerializableTask> get(Predicate<SerializableTask> predicate) throws ObjectRepositoryException;
    Stream<SerializableTask> get() throws ObjectRepositoryException;
    Optional<SerializableTask> get(int id) throws ObjectRepositoryException;
}
