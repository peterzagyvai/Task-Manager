package taskmanager.core.exceptions.repository;

public class ObjectNotInRepository extends ObjectRepositoryException {

    public ObjectNotInRepository(String message) {
        super(message);
    }

    public ObjectNotInRepository() {
        super("Object is not in repository");
    }
}
