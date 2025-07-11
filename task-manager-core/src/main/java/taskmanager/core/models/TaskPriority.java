package taskmanager.core.models;

public enum TaskPriority {
    NEGLIGIBLE("Negligible"),
    LOW("Low"),
    MODERATE("Moderate"),
    HIGH("High"),
    CRITICAL("Critical");

    private final String stringValue;

    private TaskPriority(String stringValue) {
        this.stringValue = stringValue;
    }

    public static TaskPriority valueOfLabel(String valueString) {
        for (TaskPriority tp : values()) {
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
