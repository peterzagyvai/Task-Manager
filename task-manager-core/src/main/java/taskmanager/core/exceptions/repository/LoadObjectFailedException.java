package taskmanager.core.exceptions.repository;

public class LoadObjectFailedException extends ObjectRepositoryException {

    public LoadObjectFailedException(String message) {
        super(message);
    }

    public LoadObjectFailedException() {
        super("Loading object failed");
    }
}
