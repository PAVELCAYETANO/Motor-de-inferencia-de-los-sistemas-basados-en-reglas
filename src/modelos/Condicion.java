package modelos;

public class Condicion {

    private String variable;
    private String operador;
    private String valorObjetivo;

    public Condicion(String variable, String operador, String valorObjetivo) {
        this.variable = variable;
        this.operador = operador;
        this.valorObjetivo = valorObjetivo;
    }

    //Getters unicamente
    public String getVariable() {
        return variable;
    }

    public String getOperador() {
        return operador;
    }

    public String getValorObjetivo() {
        return valorObjetivo;
    }

    // Imprime en consola: variable + operador + valorObjetivo
    @Override
    public String toString() {
        return variable + operador + valorObjetivo;
    }
}
