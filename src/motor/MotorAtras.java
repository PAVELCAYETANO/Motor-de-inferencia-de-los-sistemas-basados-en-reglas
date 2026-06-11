package motor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import modelos.Condicion;
import modelos.Hecho;
import modelos.Regla;

public class MotorAtras {

    private ArrayList<Hecho> memoriaTrabajo;
    private ArrayList<Regla> baseReglas;
    private ArrayList<String> dotLineas; // Para almacenar las lineas del archivo .dot

    //Metodo orquestador que inicia el encadenamiento hacia atras
    public void ejecutar(ArrayList<Hecho> baseHechos, ArrayList<Regla> baseReglas, Hecho objetivo) {
        // Clonamos la base inicial para no afectar la original y la usamos como memoria
        this.memoriaTrabajo = new ArrayList<>(baseHechos);
        this.baseReglas = baseReglas;
        this.dotLineas = new ArrayList<>();

        System.out.println("=========================================");
        System.out.println("  INICIANDO ENCADENAMIENTO HACIA ATRAS   ");
        System.out.println("=========================================\n");
        System.out.println("Objetivo (Meta Principal): Demostrar " + objetivo.toString() + "\n");

        // Lista para evitar algun ciclo infinito
        ArrayList<String> visitados = new ArrayList<>();

        // Una cadena vacia para la indentacion inicial
        boolean exito = evaluarMeta(objetivo, visitados, "");

        System.out.println("\n=========================================");
        if (exito) {
            System.out.println(" RESULTADO FINAL: Se demostro el objetivo con exito");
        } else {
            System.out.println(" RESULTADO FINAL: No se pudo demostrar el objetivo");
        }
        System.out.println("=========================================\n");

        // Generamos el archivo grafico
        try {
            // Obtenemos la ruta real de donde se esta ejecutando esta clase
            String rutaClase = MotorAtras.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            // Subimos un nivel para llegar a la raiz del proyecto
            java.io.File directorioRaiz = new java.io.File(rutaClase).getParentFile();
            // Construimos la ruta final hacia src/diagrama
            java.io.File archivoDestino = new java.io.File(directorioRaiz, "diagrama/arbol_resultados.dot");
            generarArchivoDOT(archivoDestino.getAbsolutePath(), objetivo);
        } catch (Exception e) {
            // Si algo falla, lo guarda en la carpeta por defecto
            generarArchivoDOT("arbol_resultados.dot", objetivo);
        }
    }

    // METODO RECURSIVO, la indentacion es solo para el formato visual en la consola
    private boolean evaluarMeta(Hecho metaActual, ArrayList<String> visitados, String indentacion) {
        String idMeta = metaActual.toString();
        System.out.println(indentacion + "Resolviendo Meta/Sub-meta (" + idMeta + "):");

        // 1. Caso Base: Prevencion de ciclos infinitos
        if (visitados.contains(idMeta)) {
            System.out.println(indentacion + " -> Se detecto un ciclo evaluando " + idMeta + ", abortando esta rama.");
            return false;
        }

        // 2. Caso Base: Verificar si la meta ya es un hecho comprobado en la memoria
        if (existeHechoEnMemoria(metaActual)) {
            System.out.println(indentacion + " -> Revisamos los hechos: " + idMeta + " es un hecho");

            // Agregamos un nodo verde al diagrama para indicar que es un hecho comprobado
            String nodoHecho = "    \"" + idMeta + "\" [shape=box, style=filled, color=lightgreen];";
            if (!dotLineas.contains(nodoHecho)) {
                dotLineas.add(nodoHecho);
            }
            return true;
        }

        // Marcamos la meta actual como visitada para evitar ciclos
        ArrayList<String> nuevosVisitados = new ArrayList<>(visitados);
        nuevosVisitados.add(idMeta);

        boolean encontroAlgunaRegla = false;

        // 3. Caso Recursivo: Buscar reglas que tengan nuestra meta como Consecuente
        for (Regla regla : baseReglas) {
            if (regla.getConsecuente().esIgualA(metaActual)) {
                encontroAlgunaRegla = true;

                // Formateamos las sub-metas para imprimirlas en la consola
                ArrayList<String> nombresSubMetas = new ArrayList<>();
                for (Condicion c : regla.getAntecedentes()) {
                    nombresSubMetas.add(c.getVariable() + c.getOperador() + c.getValorObjetivo());
                }

                System.out.println(indentacion + " -> Buscamos regla que concluya " + idMeta + ". Encontramos " + regla.getId() + ".");
                System.out.println(indentacion + " -> " + regla.getId() + " exige las sub-metas: " + String.join(" y ", nombresSubMetas));

                boolean todosAntecedentesProbados = true;
                ArrayList<String> dotTemp = new ArrayList<>();

                // Evaluamos los antecedentes como sub-metas
                for (Condicion cond : regla.getAntecedentes()) {
                    Hecho subMeta = new Hecho(cond.getVariable(), cond.getValorObjetivo());
                    String idSubMeta = subMeta.toString();

                    // OCURRE LA LLAMADA RECURSIVA
                    // Se agregan 4 espacios a la indentacion para que visualmente se baje un nivel
                    boolean subMetaAlcanzada = evaluarMeta(subMeta, nuevosVisitados, indentacion + "    ");

                    if (subMetaAlcanzada) {
                        String conexion = "    \"" + idMeta + "\" -> \"" + idSubMeta + "\" [label=\"" + regla.getId() + "\"];";
                        dotTemp.add(conexion);
                    } else {
                        System.out.println(indentacion + "    -> Intento por " + regla.getId() + " fracasa (fallo " + idSubMeta + "). Ocurre el Backtracking.");
                        todosAntecedentesProbados = false;
                        break; // Backtracking porque este camino no sirvio
                    }
                }

                // Si pasamos el ciclo for sin romperlo, la regla fue un exito
                if (todosAntecedentesProbados) {
                    System.out.println(indentacion + " -> Exito hacia arriba: Todos los antecedentes de " + regla.getId() + " son verdad -> La meta " + idMeta + " esta resuelta.");

                    memoriaTrabajo.add(metaActual); // Memorizamos para no volver a evaluarla
                    dotLineas.addAll(dotTemp); // Confirmamos las flechas en el diagrama

                    return true;
                }
            }
        }

        // Si revisamos todas las reglas y ninguna pudo probar la meta (ni era un hecho inicial)
        if (!encontroAlgunaRegla) {
            System.out.println(indentacion + " -> No se encontraron reglas que concluyan " + idMeta + " y tampoco es un hecho inicial.");
        }

        return false;
    }

    // Metodo para verificar si un hecho ya existe en la memoria
    private boolean existeHechoEnMemoria(Hecho meta) {
        for (Hecho h : memoriaTrabajo) {
            if (h.esIgualA(meta)) {
                return true;
            }
        }
        return false;
    }

    // Metodo para crear el archivo .dot
    private void generarArchivoDOT(String rutaArchivo, Hecho objetivoPrincipal) {
        File archivo = new File(rutaArchivo);
        // Crea la carpeta "src/diagrama" si no existe
        archivo.getParentFile().mkdirs();

        try (FileWriter fw = new FileWriter(archivo)) {
            fw.write("digraph ArbolInferencia {\n");
            fw.write("    node [fontname=\"Helvetica,Arial,sans-serif\"];\n");
            fw.write("    edge [fontname=\"Helvetica,Arial,sans-serif\"];\n");
            fw.write("    rankdir=TB; // Dibuja de arriba hacia abajo\n\n");

            // Coloreamos el nodo principal de azul
            fw.write("    \"" + objetivoPrincipal.toString() + "\" [shape=ellipse, style=filled, color=lightblue];\n\n");

            // Quitamos lineas duplicadas
            ArrayList<String> lineasUnicas = new ArrayList<>();
            for (String linea : dotLineas) {
                if (!lineasUnicas.contains(linea)) {
                    lineasUnicas.add(linea);
                    fw.write(linea + "\n");
                }
            }

            fw.write("}\n");
            System.out.println("Archivo DOT guardado en: " + archivo.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Error al generar el archivo DOT: " + e.getMessage());
        }
    }
}
