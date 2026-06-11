package modelos;

public class Hecho {

    private String nombre;
    private String valor;

    public Hecho(String nombre, String valor) {
        this.nombre = nombre;
        this.valor = valor;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    // Compara si dos hechos son iguales
    public boolean esIgualA(Hecho otroHecho) {
        return this.nombre.equals(otroHecho.getNombre())
                && this.valor.equals(otroHecho.getValor());
    }

    // Imprimir el hecho en formato: variable=valor
    @Override
    public String toString() {
        return nombre + "=" + valor;
    }
}
