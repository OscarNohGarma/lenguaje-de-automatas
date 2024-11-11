package lenguaje;

public class CodigoLugarValor {

    private Valor valor;
    private String codigo;
    private String lugar;

    public CodigoLugarValor(Valor valor, String codigo, String lugar) {
        this.valor = valor;
        this.codigo = codigo;
        this.lugar = lugar;
    }

    public Valor getValor() {
        return valor;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getLugar() {
        return lugar;
    }
}
