package taskmanager.cli.controllers;

import taskmanager.cli.Program;
import taskmanager.cli.views.AddNewTaskView;
import taskmanager.cli.views.TaskListingView;

public class MainMenuController extends ControllerBase {

    public MainMenuController(Program program) {
        super(program);
    }

    @Override
    public void processUserInput(String input) throws NumberFormatException {
        int command = Integer.parseInt(input);
        switch (command) {
            case 0:
                program.close();
                break;

            case 1:
                program.setController(new AddNewTaskController(program));
                break;

            case 2: 
                program.setController(new TaskListingController(program));
                break;

        
            default:
                onUnrecognizedCommand();
                break;
        }
    }

}
