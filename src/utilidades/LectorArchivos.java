package utilidades;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import modelos.Condicion;
import modelos.Hecho;
import modelos.Regla;

public class LectorArchivos {

    // Lee el archivo de hechos iniciales
    public static ArrayList<Hecho> leerHechos(String rutaArchivo) {
        ArrayList<Hecho> listaHechos = new ArrayList<>();
        
        java.io.InputStream is = LectorArchivos.class.getResourceAsStream(rutaArchivo);
        if (is == null) {
            System.err.println("Error: No se encontró el archivo " + rutaArchivo);
            return listaHechos;
        }

        // try-with-resources cierra el archivo automáticamente al terminar
        try (BufferedReader br = new BufferedReader(new java.io.InputStreamReader(is))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) {
                    continue; // Ignoramos líneas en blanco
                }
                // Esperamos un formato "variable=valor", ej: "llave_gira=si"
                String[] partes = linea.split("=");
                if (partes.length == 2) {
                    listaHechos.add(new Hecho(partes[0].trim(), partes[1].trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de hechos: " + e.getMessage());
        }

        return listaHechos;
    }

    // Método para leer el archivo de reglas
    public static ArrayList<Regla> leerReglas(String rutaArchivo) {
        ArrayList<Regla> listaReglas = new ArrayList<>();

        java.io.InputStream is = LectorArchivos.class.getResourceAsStream(rutaArchivo);
        if (is == null) {
            System.err.println("Error: No se encontró el archivo " + rutaArchivo);
            return listaReglas;
        }

        try (BufferedReader br = new BufferedReader(new java.io.InputStreamReader(is))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) {
                    continue;
                }

                // Ejemplo esperado: 
                // R1: motor_no_arranca=si Y llave_gira=si ENTONCES llamar_mecanico=si
                // 1. Separamos el ID de la regla
                String[] partesId = linea.split(":");
                String idRegla = partesId[0].trim(); // "R1"

                // 2. Separamos antecedentes de consecuente
                String resto = partesId[1].trim();
                String[] partesLogica = resto.split(" ENTONCES ");
                String parteAntecedentes = partesLogica[0].trim();
                String parteConsecuente = partesLogica[1].trim();

                // 3. Procesamos el consecuente (siempre es un hecho que se vuelve verdad)
                String[] consecuentePartes = parteConsecuente.split("=");
                Hecho consecuente = new Hecho(consecuentePartes[0].trim(), consecuentePartes[1].trim());

                // 4. Procesamos los antecedentes (pueden ser varios unidos por " Y ")
                ArrayList<Condicion> antecedentes = new ArrayList<>();
                String[] condicionesStr = parteAntecedentes.split(" Y ");
                for (String condTexto : condicionesStr) {
                    antecedentes.add(parsearCondicion(condTexto.trim()));
                }

                // 5. Armamos la regla completa y la guardamos
                listaReglas.add(new Regla(idRegla, antecedentes, consecuente));
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de reglas: " + e.getMessage());
        }

        return listaReglas;
    }

    // Método auxiliar privado para detectar el operador y armar la Condición
    private static Condicion parsearCondicion(String texto) {
        String operador = "="; // Operador por defecto

        // Buscamos qué operador relacional contiene el texto
        if (texto.contains(">=")) {
            operador = ">=";
        } else if (texto.contains("<=")) {
            operador = "<=";
        } else if (texto.contains(">")) {
            operador = ">";
        } else if (texto.contains("<")) {
            operador = "<";
        } else if (texto.contains("=")) {
            operador = "=";
        }

        // Extraemos la variable y el valor basándonos en la posición del operador
        int indiceOperador = texto.indexOf(operador);
        String variable = texto.substring(0, indiceOperador).trim();
        String valor = texto.substring(indiceOperador + operador.length()).trim();

        return new Condicion(variable, operador, valor);
    }
}
