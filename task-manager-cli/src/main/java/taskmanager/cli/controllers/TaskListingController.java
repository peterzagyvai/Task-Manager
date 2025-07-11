package taskmanager.cli.controllers;

import taskmanager.cli.Program;
import taskmanager.core.services.TaskManagerService;

public class TaskListingController extends ControllerBase {

    private TaskManagerService service;

    protected TaskListingController(Program program) {
        super(program);
    }

    @Override
    public void processUserInput(String input) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processUserInput'");
    }

}
