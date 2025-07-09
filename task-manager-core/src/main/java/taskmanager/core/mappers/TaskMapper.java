package taskmanager.core.mappers;


import java.lang.foreign.Linker.Option;
import java.util.Optional;

import taskmanager.core.models.SerializableTask;
import taskmanager.core.models.Task;

public class TaskMapper {
    private TaskMapper() {
    }

    public static Task serializableToTask(SerializableTask serializableTask) {
        Task task = new Task(serializableTask.getId());
        
        task.setTitle(serializableTask.getTitle());
        task.setDescription(Optional.ofNullable(serializableTask.getDescription()));
        task.setDueDate(Optional.ofNullable(serializableTask.getDueDate()));
        task.setPriority(serializableTask.getPriority());
        task.setStatus(serializableTask.getStatus());
        task.setCategory(Optional.ofNullable(serializableTask.getCategory()));
        
        return task;
    }

    public static SerializableTask taskToSerializable(Task task) {
        SerializableTask serializableTask = new SerializableTask();

        serializableTask.setId(task.getId());
        serializableTask.setTitle(task.getTitle());
        serializableTask.setDescription(task.getDescription().orElseGet(() -> null));
        serializableTask.setDueDate(task.getDueDate().orElseGet(() -> null));
        serializableTask.setPriority(task.getPriority());
        serializableTask.setStatus(task.getStatus());
        serializableTask.setCategory(task.getCategory().orElseGet(() -> null));

        return serializableTask;
    }
}
