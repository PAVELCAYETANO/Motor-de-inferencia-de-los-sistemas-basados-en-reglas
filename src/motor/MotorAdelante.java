package motor;

import java.util.ArrayList;
import modelos.Condicion;
import modelos.Hecho;
import modelos.Regla;

public class MotorAdelante {

    public void ejecutar(ArrayList<Hecho> baseHechos, ArrayList<Regla> baseReglas) {
        // Clonamos la base de hechos inicial para usarla como nuestra Memoria de Trabajo
        ArrayList<Hecho> memoriaTrabajo = new ArrayList<>(baseHechos);

        ArrayList<String> reglasDisparadas = new ArrayList<>();
        ArrayList<String> ordenEvaluacion = new ArrayList<>();
        ArrayList<Hecho> hechosAgregados = new ArrayList<>();

        boolean huboCambios = true;
        int iteracion = 1;

        System.out.println("=========================================");
        System.out.println(" INICIO ENCADENAMIENTO HACIA ADELANTE ");
        System.out.println("=========================================");

        // El motor gira mientras siga descubriendo nuevos hechos
        while (huboCambios) {
            huboCambios = false;
            System.out.println("\n--- Iteracion " + iteracion + " ---");

            for (Regla regla : baseReglas) {
                // Si la regla ya se disparo en una iteracion anterior, la ignoramos
                if (reglasDisparadas.contains(regla.getId())) {
                    continue;
                }

                ordenEvaluacion.add(regla.getId()); // Registramos que la estamos evaluando
                System.out.println("Evaluando " + regla.getId() + ": " + regla.toString());

                boolean seCumple = true;

                // Evaluamos cada antecedente
                for (Condicion condicion : regla.getAntecedentes()) {
                    if (!evaluarCondicion(condicion, memoriaTrabajo)) {
                        seCumple = false;
                        break; // Si falla una condicion, no tiene caso checar las demas
                    }
                }

                // Si todos los antecedentes son verdaderos, disparamos la regla
                if (seCumple) {
                    Hecho nuevoHecho = regla.getConsecuente();
                    System.out.println("  -> ENCONTRADA. Regla " + regla.getId() + " disparada.");
                    System.out.println("  -> Se agrega nuevo hecho: " + nuevoHecho.toString());

                    memoriaTrabajo.add(nuevoHecho);
                    hechosAgregados.add(nuevoHecho);
                    reglasDisparadas.add(regla.getId());
                    huboCambios = true; // Forzamos una nueva iteracion del ciclo while
                }
            }
            iteracion++;
        }

        imprimirReporteFinal(reglasDisparadas, ordenEvaluacion, hechosAgregados);
    }

    // Metodo que busca linealmente en la lista de hechos (Cumpliendo la regla de NO Hash)
    private boolean evaluarCondicion(Condicion cond, ArrayList<Hecho> memoria) {
        for (Hecho hecho : memoria) {
            // Buscamos si la variable existe en nuestra memoria
            if (hecho.getNombre().equals(cond.getVariable())) {

                String op = cond.getOperador();

                // Si es una asignacion directa (ej. llave_gira=si)
                if (op.equals("=")) {
                    return hecho.getValor().equals(cond.getValorObjetivo());
                } // Si es un operador relacional (ej. temperatura>36)
                else {
                    try {
                        double valorMemoria = Double.parseDouble(hecho.getValor());
                        double valorObjetivo = Double.parseDouble(cond.getValorObjetivo());

                        if (op.equals(">")) {
                            return valorMemoria > valorObjetivo;
                        }
                        if (op.equals("<")) {
                            return valorMemoria < valorObjetivo;
                        }
                        if (op.equals(">=")) {
                            return valorMemoria >= valorObjetivo;
                        }
                        if (op.equals("<=")) {
                            return valorMemoria <= valorObjetivo;
                        }
                    } catch (NumberFormatException e) {
                        // Si no son numeros pero usan operadores matematicos, retorna falso
                        return false;
                    }
                }
            }
        }
        // Si recorrio toda la memoria y no encontro la variable, la condicion es falsa.
        // Esto cumple la regla: "si un hecho no existe no significa que sea la negacion del mismo"
        return false;
    }

    // Cumple con la especificacion de mostrar los listados finales
    private void imprimirReporteFinal(ArrayList<String> reglasDisparadas,
            ArrayList<String> ordenEvaluacion,
            ArrayList<Hecho> hechosAgregados) {
        System.out.println("\n=========================================");
        System.out.println("             RESULTADOS FINALES            ");
        System.out.println("=========================================");
        System.out.println("\n1. Orden en que se evaluaron las reglas:");
        System.out.println(String.join(", ", ordenEvaluacion));

        System.out.println("\n2. Listado de reglas disparadas (en orden):");
        System.out.println(reglasDisparadas.isEmpty() ? "Ninguna regla fue disparada." : String.join(", ", reglasDisparadas));

        System.out.println("\n3. Hechos agregados a la base de conocimientos:");
        if (hechosAgregados.isEmpty()) {
            System.out.println("Ningun hecho nuevo fue agregado.");
        } else {
            for (int i = 0; i < hechosAgregados.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + hechosAgregados.get(i).toString());
            }
        }
        System.out.println("=========================================\n");
    }
}
