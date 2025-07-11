package taskmanager.cli.controllers;

import taskmanager.cli.Program;

public abstract class ControllerBase {
    protected Program program;
    
    protected ControllerBase(Program program) {
        this.program = program;
    }

    public abstract void processUserInput(String input);

    protected void onUnrecognizedCommand() {
        System.out.println("Command is not recognized");
    }
}
