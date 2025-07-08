package taskmanager.core.models;

public enum TaskStatus {
    NOT_STARTED("Not Started"),
    STARTED("Started"),
    COMPLETED("Completed"),
    NOT_COMPLETED("Not Completed");

    private final String stringValue;

    private TaskStatus(String stringValue) {
        this.stringValue = stringValue;
    }

    public static TaskStatus valueOfLabel(String valueString) {
        for (TaskStatus tp : values()) {
            if (tp.stringValue.equals(valueString)) {
                return tp;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
