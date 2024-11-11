package lenguaje;

import java.util.ArrayList;

public class Param {
    private ArrayList<CodigoLugarValor> parametros = new ArrayList<>();
    private int nParam;
    private String lugar;
    private String codigo;

    public Param(ArrayList<CodigoLugarValor> parametros, int nParam, String lugar, String codigo) {
        this.parametros = parametros;
        this.nParam = nParam;
        this.lugar = lugar;
        this.codigo = codigo;
    }

    public ArrayList<CodigoLugarValor> getParametros() {
        return parametros;
    }

    public int getnParam() {
        return nParam;
    }

    public String getLugar() {
        return lugar;
    }

    public String getCodigo() {
        return codigo;
    }
}
