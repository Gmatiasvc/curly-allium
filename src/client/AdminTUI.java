package client;

import db.Connection;
import db.DatabaseWrapper;
import java.util.List;
import java.util.Scanner;
import objects.Route;
import objects.Stop;
import objects.User;

public class AdminTUI {

    private DatabaseWrapper db;
    private Scanner input;
    private boolean loggedIn = false;
    private String currentAdminEmail = null;

    public AdminTUI() {
        this.db = new DatabaseWrapper(1);
        this.input = new Scanner(System.in);
    }

    public static void main(String[] args) {
        AdminTUI tui = new AdminTUI();
        tui.start();
    }

    public void start() {
        System.out.println("=== Curly Allium (Next (tm)) Admin Console ===");
        
        while (true) {
            if (!loggedIn) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }
 

    private void showLoginMenu() {
        System.out.println("\n--- Menú de Acceso ---");
        System.out.println("1. Iniciar Sesión (Admin)");
        System.out.println("2. Registrar Nuevo Admin");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");

        int choice = getIntInput();

        switch (choice) {
            case 1 -> login();
            case 2 -> registerAdmin();
            case 0 -> exit();
            default -> System.out.println("Opción inválida.");
        }
    }

    private void login() {
        System.out.print("Correo: ");
        String email = input.nextLine();
        System.out.print("Contraseña: ");
        String password = input.nextLine();

        if (db.loginAdmin(email, password)) {
            System.out.println("¡Bienvenido, Administrador!");
            this.loggedIn = true;
            this.currentAdminEmail = email;
        } else {
            System.out.println("Credenciales incorrectas.");
        }
    }

    private void registerAdmin() { {}
        
        System.out.println("\n--- Registro de Nuevo Administrador ---");
        System.out.print("Nombre Completo: ");
        String name = input.nextLine();
        System.out.print("Correo: ");
        String email = input.nextLine();
        System.out.print("Contraseña: ");
        String password = input.nextLine();

        if (db.registerAdmin(name, email, password)) {
            System.out.println("Administrador registrado exitosamente.");
        } else {
            System.out.println("Error al registrar. El correo podría estar en uso.");
        }
    }
 

    private void showMainMenu() {
        System.out.println("\n--- Panel de Control (Admin: " + currentAdminEmail + ") ---");
        System.out.println("1. Gestión de Paraderos");
        System.out.println("2. Gestión de Rutas");
        System.out.println("3. Gestión de Usuarios");
        System.out.println("4. Ver Reportes (Listar Todo)");
        System.out.println("9. Cerrar Sesión");
        System.out.println("0. Salir del Sistema");
        System.out.print("Seleccione una opción: ");

        int choice = getIntInput();

        switch (choice) {
            case 1 -> manageStops();
            case 2 -> manageRoutes();
            case 3 -> manageUsers();
            case 4 -> showReports();
            case 9 -> logout();
            case 0 -> exit();
            default -> System.out.println("Opción inválida.");
        }
    }
 

    private void manageStops() {
        System.out.println("\n--- Gestión de Paraderos ---");
        System.out.println("1. Añadir Paradero");
        System.out.println("2. Eliminar Paradero");
        System.out.println("3. Modificar Paradero");
        System.out.println("4. Listar Paraderos");
        System.out.println("0. Volver");
        System.out.print("Opción: ");

        int choice = getIntInput();

        switch (choice) {
            case 1 -> {
                System.out.print("Nombre: ");
                String nombre = input.nextLine();
                System.out.print("Distrito: ");
                String distrito = input.nextLine();
                System.out.print("Dirección: ");
                String direccion = input.nextLine();
                System.out.print("Latitud: ");
                double lat = getDoubleInput();
                System.out.print("Longitud: ");
                double lon = getDoubleInput();

                if (db.addStop(nombre, distrito, direccion, lat, lon)) {
                    System.out.println("Paradero añadido.");
                } else {
                    System.out.println("Error al añadir paradero.");
                }
            }
            case 2 -> {
                System.out.print("ID del Paradero a eliminar: ");
                int id = getIntInput();
                if (db.removeStop(id)) {
                    System.out.println("Paradero eliminado.");
                } else {
                    System.out.println("Error al eliminar (Verifique ID o dependencias).");
                }
            }
            case 3 -> {
                System.out.print("ID del Paradero a modificar: ");
                int id = getIntInput();
                System.out.print("Nuevo Nombre: ");
                String nombre = input.nextLine();
                System.out.print("Nuevo Distrito: ");
                String distrito = input.nextLine();
                System.out.print("Nueva Dirección: ");
                String direccion = input.nextLine();
                System.out.print("Nueva Latitud: ");
                double lat = getDoubleInput();
                System.out.print("Nueva Longitud: ");
                double lon = getDoubleInput();

                if (db.modifyStop(id, nombre, distrito, direccion, lat, lon)) {
                    System.out.println("Paradero actualizado.");
                } else {
                    System.out.println("Error al actualizar paradero.");
                }
            }
            case 4 -> listStops();
            case 0 -> {}
            default -> System.out.println("Opción inválida.");
        }
    }
 

    private void manageRoutes() {
        System.out.println("\n--- Gestión de Rutas ---");
        System.out.println("1. Añadir Ruta");
        System.out.println("2. Eliminar Ruta");
        System.out.println("3. Modificar Ruta");
        System.out.println("4. Listar Rutas");
        System.out.println("0. Volver");
        System.out.print("Opción: ");

        int choice = getIntInput();

        switch (choice) {
            case 1 -> {
                System.out.print("ID Paradero Origen: ");
                int origen = getIntInput();
                System.out.print("ID Paradero Destino: ");
                int destino = getIntInput();
                System.out.print("Tiempo estimado (min): ");
                int tiempo = getIntInput();
                System.out.print("Distancia (km): ");
                double distancia = getDoubleInput();
                
                if (db.addRoute(origen, destino, tiempo, distancia, true)) {
                    System.out.println("Ruta añadida.");
                } else {
                    System.out.println("Error al añadir ruta.");
                }
            }
            case 2 -> {
                System.out.print("ID Origen: ");
                int origen = getIntInput();
                System.out.print("ID Destino: ");
                int destino = getIntInput();
                if (db.removeRoute(origen, destino)) {
                    System.out.println("Ruta eliminada.");
                } else {
                    System.out.println("Error al eliminar ruta.");
                }
            }
            case 3 -> {
                System.out.print("ID Origen de la ruta a modificar: ");
                int origen = getIntInput();
                System.out.print("ID Destino de la ruta a modificar: ");
                int destino = getIntInput();
                System.out.print("Nuevo Tiempo (min): ");
                int tiempo = getIntInput();
                System.out.print("Nueva Distancia (km): ");
                double distancia = getDoubleInput();
                System.out.print("¿Activo? (true/false): ");
                boolean estado = Boolean.parseBoolean(input.nextLine());

                if (db.modifyRoute(origen, destino, tiempo, distancia, estado)) {
                    System.out.println("Ruta modificada.");
                } else {
                    System.out.println("Error al modificar ruta.");
                }
            }
            case 4 -> listRoutes();
            case 0 -> {}
            default -> System.out.println("Opción inválida.");
        }
    
 }

    private void manageUsers() {
        System.out.println("\n--- Gestión de Usuarios ---");
        System.out.println("1. Consultar Datos de Usuario");
        System.out.println("2. Suspender Usuario");
        System.out.println("3. Ver Historial de Usuario (Simulado)");
        System.out.println("0. Volver");
        System.out.print("Opción: ");

        int choice = getIntInput();

        switch (choice) {
            case 1 -> {
                System.out.print("Nombre de usuario: ");
                String username = input.nextLine();
                User u = db.getUserData(username);
                if (u != null) {
                    System.out.println("Nombre: " + u.getName());
                    System.out.println("Correo: " + u.getEmail());
                    System.out.println("Estado: " + (u.isActive() ? "Activo" : "Suspendido"));
                    System.out.println("Registro: " + u.getCreatedAtUT());
                } else {
                    System.out.println("Usuario no encontrado.");
                }
            }
            case 2 -> {
                System.out.print("Nombre de usuario a SUSPENDER: ");
                String username = input.nextLine();
                System.out.print("¿Está seguro? (s/n): ");
                String confirm = input.nextLine();
                if (confirm.equalsIgnoreCase("s")) {
                    if (db.suspendUser(username)) {
                        System.out.println("Usuario suspendido correctamente.");
                    } else {
                        System.out.println("Error al suspender usuario.");
                    }
                }
            }
            case 3 -> { {}
                System.out.println("Funcionalidad requiere ID interno del usuario.");
            }
            case 0 -> {}
            default -> System.out.println("Opción inválida.");
        }
    }
 

    private void showReports() {
        System.out.println("\n--- Reporte General ---");
        listStops();
        System.out.println("-----------------------");
        listRoutes();
    }

    private void listStops() {
        List<Stop> stops = db.getAllStops();
        System.out.println("\nListado de Paraderos (" + stops.size() + "):");
        for (Stop s : stops) { {}
            System.out.printf("- %s (%s) [%s, %s]\n", s.getName(), s.getLocation(), s.getLatitude(), s.getLongitude());
        }
    }

    private void listRoutes() {
        List<Route> routes = db.getAllRoutes();
        System.out.println("\nListado de Rutas (" + routes.size() + "):");
        for (Route r : routes) {
            System.out.printf("- %s -> %s (%s km, %d min)\n", r.getStart(), r.getEnd(), r.getDistance(), r.getTime());
        }
    }

    private void logout() {
        this.loggedIn = false;
        this.currentAdminEmail = null;
        System.out.println("Sesión cerrada.");
    }

    private void exit() {
        System.out.println("Saliendo del sistema...");
        Connection.close();
        System.exit(0);
    }
 

    private int getIntInput() {
        while (true) {
            try {
                String line = input.nextLine();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Por favor ingrese un número válido: ");
            }
        }
    }

    private double getDoubleInput() {
        while (true) {
            try {
                String line = input.nextLine();
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.print("Por favor ingrese un número decimal válido: ");
            }
        }
    }
}