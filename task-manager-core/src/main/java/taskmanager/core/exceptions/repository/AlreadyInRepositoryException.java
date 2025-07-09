package taskmanager.core.exceptions.repository;

public class AlreadyInRepositoryException extends ObjectRepositoryException {

    public AlreadyInRepositoryException(String message) {
        super(message);
    }

    public AlreadyInRepositoryException() {
        this("Object already exists in repository");
    }
}
