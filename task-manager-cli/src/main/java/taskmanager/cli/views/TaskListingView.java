package taskmanager.cli.views;

import java.util.ArrayList;
import java.util.List;

import taskmanager.core.models.Task;

public class TaskListingView extends ViewBase {
    private List<Task> tasks;

    public TaskListingView(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void show() {
        for (Task task : tasks) {
            System.out.println(String.format("""
                    1.
                    \tTitle: %s\
                    \tPriority: %s
                    \tStatus: %s
                    \tDue: %s
                    """, 
                    task.getTitle(),
                    task.getPriority().toString(),
                    task.getStatus().toString(),
                    task.getDueDate().isPresent() ? task.getDueDate().toString() : "-"));
        }
    }
}
