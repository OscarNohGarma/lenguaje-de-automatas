package lenguaje;

public class Valor {
    String tipo, lugar, codigo;
    Object valor;

    public Valor(String tipo, Object valor, String lugar, String codigo) {
        this.tipo = tipo;
        this.valor = valor;
        this.lugar = lugar;
        this.codigo = codigo;
    }

    public Valor(String tipo, Object valor) {
        this.tipo = tipo;
        this.valor = valor;
    }
}
