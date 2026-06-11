
import java.util.ArrayList;
import java.util.Scanner;

import modelos.Hecho;
import modelos.Regla;
import motor.MotorAdelante;
import motor.MotorAtras;
import utilidades.LectorArchivos;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String rutaReglas = "/base_conocimiento/reglas.txt";
        String rutaHechos = "/base_conocimiento/hechos.txt";

        boolean ejecutando = true;

        while (ejecutando) {
            System.out.println("\n=========================================");
            System.out.println("       SISTEMA BASADO EN REGLAS          ");
            System.out.println("=========================================");
            System.out.println(" 1. Encadenamiento hacia Adelante");
            System.out.println(" 2. Encadenamiento hacia Atras");
            System.out.println(" 3. Salir del programa");
            System.out.println("=========================================");
            System.out.print("Selecciona una opcion (1-3): ");

            String opcion = scanner.nextLine().trim();

            // Leemos los archivos de nuevo en cada ciclo para garantizar 
            // que la memoria de trabajo este limpia (reset) entre cada ejecucion.
            ArrayList<Hecho> hechosLimpios = LectorArchivos.leerHechos(rutaHechos);
            ArrayList<Regla> reglasLimpias = LectorArchivos.leerReglas(rutaReglas);

            if ((opcion.equals("1") || opcion.equals("2")) && (hechosLimpios.isEmpty() || reglasLimpias.isEmpty())) {
                System.out.println("Error: No se pudieron cargar los archivos o estan vacios. Revisa las rutas.");
                continue;
            }

            switch (opcion) {
                case "1":
                    MotorAdelante motorAdelante = new MotorAdelante();
                    motorAdelante.ejecutar(hechosLimpios, reglasLimpias);

                    System.out.println("\nPresiona ENTER para volver al menu...");
                    scanner.nextLine();
                    break;

                case "2":
                    System.out.println("\n--- Definir Objetivo ---");
                    System.out.println("Deja el campo vacio y presiona ENTER para usar el objetivo por defecto (reparacion_urgente=si) .");
                    System.out.print("Ingresa la variable objetivo (ej. tanque_vacio): ");
                    String variable = scanner.nextLine().trim();

                    Hecho objetivo;
                    if (variable.isEmpty()) {
                        System.out.println(" -> Usando objetivo por defecto: reparacion_urgente=si");
                        objetivo = new Hecho("reparacion_urgente", "si");
                    } else {
                        System.out.print("Ingresa el valor esperado (ej. si, no, 38): ");
                        String valor = scanner.nextLine().trim();
                        objetivo = new Hecho(variable, valor);
                    }

                    MotorAtras motorAtras = new MotorAtras();
                    motorAtras.ejecutar(hechosLimpios, reglasLimpias, objetivo);

                    System.out.println("\nPresiona ENTER para volver al menu...");
                    scanner.nextLine();
                    break;

                case "3":
                    System.out.println("Cerrando el motor de inferencia.");
                    ejecutando = false;
                    System.exit(0);
                    break;

                default:
                    System.out.println("Opcion no valida. Por favor, ingresa 1, 2 o 3.");
            }
        }

        scanner.close();
    }
}
