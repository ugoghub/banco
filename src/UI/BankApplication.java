package UI;

import UI.menu.MainMenu;
import service.ApplicationService;

import java.util.Scanner;

public class BankApplication {

    private final Scanner scanner;
    private final ApplicationService applicationService;
    private final MainMenu mainMenu;

    public BankApplication() {
        this.scanner = new Scanner(System.in);
        this.applicationService = new ApplicationService();
        this.mainMenu = new MainMenu(scanner, applicationService);
    }

    public void start() {
        mainMenu.show();
    }
}