package projects;

import java.math.BigDecimal;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.ArrayList;

import projects.dao.DbConnection;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {

    // This is the list of menu options we will show to the user
	// @formatter:off
    private List<String> operations = List.of(
        "1) Add a project",
    	"2) List projects",
    	"3) Select a project"
        
    // @formatter:on
        // You can add more options later like "2) List projects", etc.
    );

    // This Scanner reads user input from the terminal
    private Scanner scanner = new Scanner(System.in);

    // This connects to our service layer, which talks to the database
    private ProjectService projectService = new ProjectService();

    // The main method is where the program starts running
    public static void main(String[] args) {
        // First, make sure we can connect to the database
        DbConnection.getConnection();

        // Create a new instance of the app and run the menu
        ProjectsApp app = new ProjectsApp();
        app.processUserSelections();
    }

    // This keeps the app running until the user chooses to exit
    private void processUserSelections() {
        boolean done = false;

        while (!done) {
            try {
                printOperations(); // Show menu options
                int selection = getUserSelection(); // Ask user to choose

                // Handle the menu selection
                switch (selection) {
                    case -1:
                        done = exitMenu(); // User pressed Enter to exit
                        break;
                    case 1:
                        createProject(); // Add new project
                        break;
                    case 2:
                    	listProjects(); // Call method to display all projects
                    	break;
                    case 3:
                    	selectProject();
                    	break;
                    default:
                        System.out.println("\n" + selection + " is not a valid option. Try again.");
                        break;
                }
            } catch (Exception e) {
                System.out.println("\nAn error occurred: " + e.toString());
            }
        }
    }

    // This prints the list of available menu options
    private void printOperations() {
        System.out.println("\nThese are the available selections:\n");

        for (String operation : operations) {
            System.out.println("  " + operation);
        }
    }

    // This gets the user's menu choice as an integer
    private int getUserSelection() {
        Integer input = getIntInput("Enter a menu selection");
        return (input == null) ? -1 : input;
    }

    // This handles exiting the app
    private boolean exitMenu() {
        System.out.println("Exiting the application.");
        return true;
    }

    // This creates a new project by asking the user for details
    private void createProject() {
        String projectName = getStringInput("Enter the project name");
        BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
        BigDecimal actualHours = getDecimalInput("Enter the actual hours");
        Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
        String notes = getStringInput("Enter the project notes");

        // Set all values into a new Project object
        Project project = new Project();
        project.setProjectName(projectName);
        project.setEstimatedHours(estimatedHours);
        project.setActualHours(actualHours);
        project.setDifficulty(difficulty);
        project.setNotes(notes);

        // Send the project to the service layer to save it
        projectService.addProject(project);

        System.out.println("Project successfully created: " + project);
    }

    // This asks the user for a string input
    private String getStringInput(String prompt) {
        System.out.print(prompt + ": ");
        String input = scanner.nextLine();
        return input.isBlank() ? null : input.trim();
    }

    // This asks the user for an integer input and validates it
    private Integer getIntInput(String prompt) {
        String input = getStringInput(prompt);
        if (input == null) {
            return null;
        }

        try {
            return Integer.valueOf(input);
        } catch (NumberFormatException e) {
            throw new DbException(input + " is not a valid number. Try again.");
        }
    }

    // This asks the user for a decimal input and validates it
    private BigDecimal getDecimalInput(String prompt) {
        String input = getStringInput(prompt);
        if (input == null) {
            return null;
        }

        try {
            return new BigDecimal(input).setScale(2);
        } catch (NumberFormatException e) {
            throw new DbException(input + " is not a valid decimal number.");
        }
    }

    private void listProjects() {
        List<Project> projects = projectService.fetchAllProjects();

        System.out.println("\nProjects:");

        projects.forEach(project ->
            System.out.println("  " + project.getProjectId() + ": " + project.getProjectName()));
    }

    private void selectProject() {
        listProjects();

        Integer projectId = getIntInput("Enter a project ID to select");

        Project project = projectService.fetchProjectById(projectId);

        System.out.println("\nYou have selected project:\n" + project);

        List<String> materials = projectService.fetchMaterialsByProjectId(projectId);
        System.out.println("\nMaterials:");
        materials.forEach(material -> System.out.println(" - " + material));

        List<String> steps = projectService.fetchStepsByProjectId(projectId);
        System.out.println("\nSteps:");
        steps.forEach(step -> System.out.println(" - " + step));
        
        List<String> categories = projectService.fetchCategoriesByProjectId(projectId);
        System.out.println("\nCategories:");
        categories.forEach(category -> System.out.println(" - " + category));
    }
}