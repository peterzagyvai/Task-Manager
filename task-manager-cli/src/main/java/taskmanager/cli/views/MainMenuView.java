package taskmanager.cli.views;

public class MainMenuView extends ViewBase {

    @Override
    public void show() {
        System.out.println("""
                Select an option by typing its number:
                1. Add new Task
                2. List all tasks
                3. Detail a task (also edit or delete)
                0. Exit
                """);
    }
}
