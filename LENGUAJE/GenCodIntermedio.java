package lenguaje;

import java.util.ArrayList;

public class GenCodIntermedio {

    public ArrayList<TresDirecciones> temporales;
    public ArrayList<String> temporalesStr;

    public GenCodIntermedio() {
        temporales = new ArrayList<>();
        temporalesStr = new ArrayList<>();
    }

    public String newTemporal() {
        temporalesStr.add("t" + (temporalesStr.size() + 1));
        return "t" + (temporalesStr.size());
    }
}

class TresDirecciones {
    private String temporal;
    private String operando1;
    private String operando2;
    private String operador;

    public TresDirecciones(String temporal, String operando1, String operador, String operando2) {
        this.temporal = temporal;
        this.operando1 = operando1;
        this.operando2 = operando2;
        this.operador = operador;
    }

    public TresDirecciones(String temporal, String operando) {
        this.temporal = temporal;
        this.operando1 = operando;
        this.operando2 = "";
        this.operador = "";
    }

    public String getTemporal() {
        return temporal;
    }

    public String getOperando1() {
        return operando1;
    }

    public String getOperando2() {
        return operando2;
    }

    public String getOperador() {
        return operador;
    }
}