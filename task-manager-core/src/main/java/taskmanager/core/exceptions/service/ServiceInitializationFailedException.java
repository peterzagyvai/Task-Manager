package taskmanager.core.exceptions.service;

public class ServiceInitializationFailedException extends Exception {
    public ServiceInitializationFailedException(String message) {
        super(message);
    }

    public ServiceInitializationFailedException() {
        super("Initializing Service Failed");
    }
}
