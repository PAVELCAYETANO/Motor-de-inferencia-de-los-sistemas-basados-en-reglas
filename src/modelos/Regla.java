package modelos;

import java.util.ArrayList;

public class Regla {

    private String id;
    private ArrayList<Condicion> antecedentes;
    private Hecho consecuente;

    public Regla(String id, ArrayList<Condicion> antecedentes, Hecho consecuente) {
        this.id = id;
        this.antecedentes = antecedentes;
        this.consecuente = consecuente;
    }

    // Getters
    public String getId() {
        return id;
    }

    public ArrayList<Condicion> getAntecedentes() {
        return antecedentes;
    }

    public Hecho getConsecuente() {
        return consecuente;
    }

    // Sobreescribimos un toString para un mejor formateo a la hora de imprimir
    // para que se vea como: "Evaluando regla R1: ..."
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(": ");

        for (int i = 0; i < antecedentes.size(); i++) {
            sb.append(antecedentes.get(i).toString());
            // Agregamos la "Y" entre antecedentes, menos la ultima
            if (i < antecedentes.size() - 1) {
                sb.append(" Y ");
            }
        }

        sb.append(" ENTONCES ").append(consecuente.toString());
        return sb.toString();
    }
}
