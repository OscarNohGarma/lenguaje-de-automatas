package lenguaje;

public class Simbolo {
    String id;
    String tipo;
    Object valor;

    public Simbolo(String id, String tipo, Object valor) {
        this.id = id;
        this.tipo = tipo;
        this.valor = null;
    }

    public Simbolo(String id, String tipo) {
        this.id = id;
        this.tipo = tipo;
        this.valor = null;
    }

    public String getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }
}
