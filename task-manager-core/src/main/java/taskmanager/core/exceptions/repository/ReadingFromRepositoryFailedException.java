package taskmanager.core.exceptions.repository;

public class ReadingFromRepositoryFailedException extends ObjectRepositoryException {

    public ReadingFromRepositoryFailedException(String message) {
        super(message);
    }

    public ReadingFromRepositoryFailedException() {
        super("Reading object failed");
    }
}
