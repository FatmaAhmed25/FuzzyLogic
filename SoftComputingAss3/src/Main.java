import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("lab.txt"));
        String systemName ="";
        String systemDescription ="";
        ArrayList<Variable> variables = new ArrayList<>();
        ArrayList<String> rules = new ArrayList<>();
        FuzzyLogic f = null;

        while (true) {
            System.out.println();
            System.out.println("Fuzzy Logic Toolbox");
            System.out.println("===================");
            System.out.println("1- Create a new fuzzy system");
            System.out.println("2- Quit");

            int n;
            try {
                n = Integer.parseInt(s.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                continue;
            }

//            int n = s.nextInt();
//            s.nextLine();


            switch (n) {
                case 1:
                    System.out.println();
                    System.out.println("Enter the system’s name and a brief description:");
                    System.out.println("------------------------------------------------");
                    systemName = s.nextLine();
                    systemDescription = s.nextLine();
                    f = new FuzzyLogic(rules, variables, systemName, systemDescription);

                    while (true) {
                        System.out.println();
                        System.out.println("Main Menu:");
                        System.out.println("==========");
                        System.out.println("1- Add variables.");
                        System.out.println("2- Add fuzzy sets to an existing variable.");
                        System.out.println("3- Add rules.");
                        System.out.println("4- Run the simulation on crisp values.");
                        System.out.println("5- Type 'Close' to exit.");

                        String action = s.nextLine();
                        System.out.println(action);

                        if (action.equalsIgnoreCase("Close")) {
                            rules.clear();
                            variables.clear();
                            break;
                        }
                        else
                        {
                            int choice = Integer.parseInt(action);

                            switch (choice)
                            {
                                case 1:
                                    System.out.println();
                                    System.out.println("Enter the variable’s name, type (IN/OUT), and range ([lower, upper]):");
                                    System.out.println("(Press x to finish)");

                                    while (true)
                                    {
                                        String variable = s.nextLine();
                                        if (variable.equals("x"))
                                            break;

                                        f.addVariables(variable);
                                    }
                                    break;

                                case 2:
                                    System.out.println();
                                    System.out.println("Enter the variable’s name:");
                                    System.out.println("--------------------------");
                                    String name = s.nextLine();
                                    System.out.println("Enter the fuzzy set name, type (TRI/TRAP), and values: (Press x to finish)");
                                    System.out.println("-----------------------------------------------------");

                                    while (true)
                                    {
                                        String query = s.nextLine();
                                        if (query.equals("x"))
                                            break;

                                        f.addFuzzySets(name, query);
                                    }
                                    break;

                                case 3:
                                    System.out.println();
                                    System.out.println("Enter the rules in this format: (Press x to finish)");
                                    System.out.println("IN_variable set operator IN_variable set => OUT_variable set");

                                    while (true)
                                    {
                                        String query = s.nextLine();
                                        if (query.equals("x"))
                                            break;
                                        f.addRules(query);
                                    }
                                    break;

                                case 4:
                                    if (rules.isEmpty() || variables.get(0).fuzzySets.isEmpty())
                                        System.out.println("CAN’T START THE SIMULATION! Please add the fuzzy sets and rules first.");
                                    else {
                                        System.out.println();
                                        System.out.println("Enter the crisp values:");
                                        System.out.println("-----------------------");

                                        for (int i = 0; i < variables.size(); i++)
                                        {
                                            if (variables.get(i).type.equals("IN")) {
                                                System.out.print(variables.get(i).name + ": ");
                                                variables.get(i).crispValue = s.nextInt();
                                                System.out.print(variables.get(i).crispValue);
                                                s.nextLine();
                                                System.out.println();
                                            }
                                        }

                                        System.out.println("Running the simulation…");

                                        for (int i = 0; i < variables.size(); i++) {
                                            if (variables.get(i).type.equals("IN"))
                                                f.fuzzification(variables.get(i));
                                        }

                                        System.out.println("Fuzzification => done");

                                        for (int i = 0; i < rules.size(); i++) {
                                            f.inference(rules.get(i));
                                        }

                                        System.out.println("Inference => done");

                                        System.out.println("Defuzzification => done");

                                        System.out.println();

                                        try {
                                            f.defuzzification();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    break;

                                default:
                                    System.out.println("Please enter only one of the numbers that are available.");
                            }
                        }
                    }
                    break;

                case 2:
                    System.exit(0);

                default:
                    System.out.println("Please enter only one of the numbers that are available.");
            }
        }
    }
}