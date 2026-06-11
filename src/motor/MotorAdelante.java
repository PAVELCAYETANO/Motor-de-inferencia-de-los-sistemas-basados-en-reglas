package motor;

import java.util.ArrayList;

import modelos.Condicion;
import modelos.Hecho;
import modelos.Regla;

public class MotorAdelante {

    //Antes de ejecutar el ciclo While, inicializamos las siguientes variables
    //baseReglas: Contiene las 10 reglas (R1 a R10)
    //baseHechos (Entrada): [correa_suelta=si, luces_tenues=si, llave_gira=si]
    //memoriaTrabajo: Es un clon de la base de hechos inicial y se usa como nuestra Memoria de Trabajo
    public void ejecutar(ArrayList<Hecho> baseHechos, ArrayList<Regla> baseReglas) {
        ArrayList<Hecho> memoriaTrabajo = new ArrayList<>(baseHechos);
        ArrayList<String> reglasDisparadas = new ArrayList<>();
        ArrayList<String> ordenEvaluacion = new ArrayList<>();
        ArrayList<Hecho> hechosAgregados = new ArrayList<>();

        boolean huboCambios = true;
        int iteracion = 1;

        System.out.println("=========================================");
        System.out.println(" INICIO ENCADENAMIENTO HACIA ADELANTE ");
        System.out.println("=========================================");

        // Paramos hasta que el motor deje de descubrir nuevos hechos osea cuando huboCambios sea false
        while (huboCambios) {
            huboCambios = false;
            System.out.println("\n--- Iteracion " + iteracion + " ---");

            for (Regla regla : baseReglas) {
                // Ignoramos reglas ya disparadas anteriormente
                if (reglasDisparadas.contains(regla.getId())) {
                    continue;
                }

                // Anotamos que la estamos evaluando
                ordenEvaluacion.add(regla.getId());
                System.out.println("Evaluando " + regla.getId() + ": " + regla.toString());

                boolean seCumple = true;

                // Evaluamos cada antecedente
                for (Condicion condicion : regla.getAntecedentes()) {
                    //Si el antecedente no se encuentra en la base de hechos, no tiene caso checar las demas
                    if (!evaluarCondicion(condicion, memoriaTrabajo)) {
                        seCumple = false;
                        break;
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
                    huboCambios = true; // Vamos por una nueva iteracion
                }
            }
            iteracion++;
        }

        imprimirReporteFinal(reglasDisparadas, ordenEvaluacion, hechosAgregados);
    }

    // Buscamos en la lista de hechos
    private boolean evaluarCondicion(Condicion cond, ArrayList<Hecho> memoria) {
        for (Hecho hecho : memoria) {
            // Buscamos si la variable existe en nuestra memoria
            if (hecho.getNombre().equals(cond.getVariable())) {

                String op = cond.getOperador();

                // Si es una asignacion directa (ejemplo: llave_gira=si)
                if (op.equals("=")) {
                    return hecho.getValor().equals(cond.getValorObjetivo());
                } // Si se llegaran a ocupar operadores relacionales en otro ejemplo. 
                else {
                    try {
                        double valorMemoria = Double.parseDouble(hecho.getValor()); //Conviertimos el String a número
                        double valorObjetivo = Double.parseDouble(cond.getValorObjetivo()); // lo mismo con la condicion

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

    // Metodo que muestra los resultados finales
    private void imprimirReporteFinal(ArrayList<String> reglasDisparadas,
            ArrayList<String> ordenEvaluacion,
            ArrayList<Hecho> hechosAgregados) {
        System.out.println("\n=========================================");
        System.out.println("             RESULTADOS FINALES            ");
        System.out.println("=========================================");
        System.out.println("\n1. Orden en que se evaluaron las reglas:");
        System.out.println(String.join(", ", ordenEvaluacion));

        System.out.println("\n2. Listado de reglas disparadas en orden:");
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
