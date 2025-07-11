package taskmanager.cli;

import java.util.Scanner;

import taskmanager.cli.controllers.ControllerBase;
import taskmanager.cli.controllers.MainMenuController;
import taskmanager.cli.views.MainMenuView;
import taskmanager.cli.views.ViewBase;

public class Program {
    private Scanner scanner;
    private boolean shouldClose = false;

    private ViewBase view;
    private ControllerBase controller;

    public Program() {
        view = new MainMenuView();
        controller = new MainMenuController(this);
        scanner = new Scanner(System.in);
    }

    public void run() {
        while (!shouldClose) {
            view.show();
            String input = scanner.nextLine();
            controller.processUserInput(input);
        }
    }

    public void close() {
        shouldClose = true;
    }

    public void setController(ControllerBase controller) {
        this.controller = controller;
    }

    public void setView(ViewBase view) {
        this.view = view;
    }

}
