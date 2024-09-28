package lenguaje;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AnalisisSemantico {
    private Map<String, Simbolo> tablaSimbolos;

    public AnalisisSemantico() {
        this.tablaSimbolos = new HashMap<>();
    }

    // <sentencia> → tipo id = <valor> ;
    // id.tipo = add(tipo.tipo, id.lex)
    public void verificarDeclaracion(String id, String tipo) {
        if (tablaSimbolos.containsKey(id)) {
            System.out.println("Error: Variable " + id + " ya fue declarada.");
            System.exit(0);
        } else {
            Simbolo simbolo = new Simbolo(id, tipo);
            tablaSimbolos.put(id, simbolo);
        }
    }

    // <sentencia> → tipo id = <valor> ;
    // Si id.tipo == <valor>.tipo entonces id.valor = <valor>.valor
    public void verificarAsignacion(String id, Valor valor) {
        if (!tablaSimbolos.containsKey(id)) {
            System.out.println("Error: Variable " + id + " no ha sido declarada.");
            System.exit(0);
            return;
        }

        Simbolo simbolo = tablaSimbolos.get(id);

        // Si id.tipo == <valor>.tipo
        if (simbolo.getTipo().equals(valor.tipo)) {
            simbolo.setValor(valor.valor);

        } else {
            System.out.println("Error: Tipo de dato incompatible. Se esperaba " + simbolo.getTipo()
                    + " pero se recibió " + valor.tipo);
            System.exit(0);
        }
    }

    // <valor> → numero | <mensaje> | booelano | id
    // <valor>.tipo = table(tipo, lex)
    // <valor>.valor = table(valor, lex)
    public Valor verificarValor(String lexema) {

        String tipo;
        Object valor;
        if (lexema.matches("-?\\d+")) {
            tipo = "int";
            valor = Integer.parseInt(lexema);
            return new Valor(tipo, valor);

        } else if (lexema.equals("true") || lexema.equals("false")) {
            tipo = "boolean";
            valor = Boolean.parseBoolean(lexema);
            return new Valor(tipo, valor);
        } else if (lexema.startsWith("\"") && lexema.endsWith("\"")) {
            tipo = "string";
            valor = lexema.substring(1, lexema.length() - 1);
        } else if (tablaSimbolos.containsKey(lexema)) {
            Simbolo simbolo = tablaSimbolos.get(lexema);
            return new Valor(simbolo.tipo, simbolo.valor);
        } else {
            System.out.println("Error: Variable no declarada o tipo de dato desconocido para el lexema: " + lexema);
            System.exit(0);
            return null;
        }

        if (tablaSimbolos.containsKey(lexema)) {
            Simbolo simbolo = tablaSimbolos.get(lexema);
            if (!simbolo.tipo.equals(tipo)) {
                System.out.println("Tipo de dato no coincidente para " + lexema);
                System.exit(0);
                return null;
            }
            return new Valor(simbolo.tipo, simbolo.valor);
        } else {
            Simbolo nuevoSimbolo = new Simbolo(lexema, tipo, valor);
            tablaSimbolos.put(lexema, nuevoSimbolo);
            return new Valor(tipo, valor);
        }
    }

    // <valor> → read ( cadena )
    // Si cadena.tipo == 'String' entonces cadena.valor = table(cadena.valor,
    // cadena.lex) <valor>.tipo = 'file';
    public Valor procesarRead(String cadena) {

        Valor valorCadena = verificarValor(cadena);

        if (valorCadena != null && valorCadena.tipo.equals("string")) {
            String nombreArchivo = (String) valorCadena.valor;
            File archivo = new File("./" + nombreArchivo);

            if (archivo.exists()) {
                return new Valor("file", archivo);
            } else {
                System.out.println("Error: El archivo " + nombreArchivo + " no existe.");
                System.exit(0);
                return null;
            }
        } else {
            System.out.println("Error: Se esperaba una cadena para read, pero se recibió: " + cadena);
            System.exit(0);
            return null;
        }
    }

    // <validacion> -> <comparacion> opL <coparacion>

    public Valor validacion(Valor C, Valor Vali, String opL) {

        if (opL.equals("and")) {
            if (Boolean.parseBoolean(C.valor.toString()) && Boolean.parseBoolean(Vali.valor.toString())) {
                return new Valor("boolean", true);
            } else {
                return new Valor("boolean", false);
            }
        } else if (opL.equals("or")) {
            if (Boolean.parseBoolean(C.valor.toString()) || Boolean.parseBoolean(Vali.valor.toString())) {
                return new Valor("boolean", true);
            } else {
                return new Valor("boolean", false);
            }
        } else {
            System.out.println("Operador de comparación no válido");
            System.exit(0);
        }
        return null;
    }

    // <comparacion> -> <valor> opC <valor>
    public Valor comparacion(Valor v1, Valor v2, String opC) {

        if (opC.equals("==")) {
            if (v1.valor.toString().equals(v2.valor.toString()) && v1.tipo.equals(v2.tipo)) {
                return new Valor("boolean", true);
            } else {
                return new Valor("boolean", false);
            }
        } else if (opC.equals("!=")) {
            if (v1.valor.toString().equals(v2.valor.toString()) && v1.tipo.equals(v2.tipo)) {
                return new Valor("boolean", false);
            } else {
                return new Valor("boolean", true);
            }
        } else {
            System.out.println("Operador de comparación no válido");
            System.exit(0);
        }
        return null;
    }
}
