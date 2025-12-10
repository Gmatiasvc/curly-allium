package client;

import db.Connection;
import db.DatabaseWrapper;
import java.util.Scanner;

public class AdminTUI {

    DatabaseWrapper conn = new DatabaseWrapper(1, Connection.getInstance());
    Scanner input = new Scanner(System.in);
    int sel = -1;

    public static void main(String[] args) {
        AdminTUI tui = new AdminTUI();
        tui.menu();
    }

    private void menu() {
        while (true) {
            try {
                sel = input.nextInt();
                input.nextLine();

                switch (sel) {
                    case 0 -> {
                        System.out.println("Exiting...");
                        Connection.close();
                        System.exit(0);
                    }

					case 1 -> {
						System.out.println("Registering new user...");
						System.out.print("Name: ");
						String name = input.nextLine();
						System.out.print("Username: ");
						String username = input.nextLine();
						System.out.print("Email: ");
						String email = input.nextLine();
						System.out.print("Password: ");
						String password = input.nextLine();

						boolean success = conn.registerUser(name, username, email, password);
						if (success) {
							System.out.println("User registered successfully.");
						} else {
							System.out.println("Failed to register user.");
						}
					}

					case 2 -> {
						System.out.println("Registering new admin...");
						System.out.print("Name: ");
						String name = input.nextLine();
						System.out.print("Email: ");
						String email = input.nextLine();
						System.out.print("Password: ");
						String password = input.nextLine();
						boolean success = conn.registerAdmin(name, email, password);
						if (success) {
							System.out.println("Admin registered successfully.");
						} else {
							System.out.println("Failed to register admin.");
						}

					}
                    default ->
                        throw new AssertionError();
                }

            } catch (Exception e) {
            }
        }
    }
}
