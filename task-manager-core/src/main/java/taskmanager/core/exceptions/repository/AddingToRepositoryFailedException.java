package taskmanager.core.exceptions.repository;

public class AddingToRepositoryFailedException extends ObjectRepositoryException {

    public AddingToRepositoryFailedException(String message) {
        super(message);
    }

    public AddingToRepositoryFailedException() {
        super("Adding object to repositry failed");
    }
}
