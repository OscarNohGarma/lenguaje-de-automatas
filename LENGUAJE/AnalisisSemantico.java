package lenguaje;

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
        } else {
            Simbolo simbolo = new Simbolo(id, tipo);
            tablaSimbolos.put(id, simbolo);
        }
    }

    // <sentencia> → tipo id = <valor> ;
    // Si id.tipo == <valor>.tipo entonces id.valor = <valor>.valor
    public void verificarAsignacion(String id, String valor) {
        if (!tablaSimbolos.containsKey(id)) {
            System.out.println("Error: Variable " + id + " no ha sido declarada.");
            return;
        }

        Simbolo simbolo = tablaSimbolos.get(id);

        // Si id.tipo == <valor>.tipo
        if (simbolo.getTipo().equals(getTipo(valor))) {
            simbolo.setValor(valor);
        } else {
            System.out.println("Error: Tipo de dato incompatible. Se esperaba " + simbolo.getTipo()
                    + " pero se recibió " + getTipo(valor));
        }
    }

    private String getTipo(String valor) {
        if (valor.equalsIgnoreCase("true") || valor.equalsIgnoreCase("false")) {
            return "boolean"; // Tipo boolean
        }

        try {
            Integer.parseInt(valor);
            return "int"; // Tipo entero
        } catch (NumberFormatException e1) {
            if (valor.endsWith(".txt")) {
                return "file"; // Tipo archivo
            }

            return "String"; // Tipo cadena
        }
    }

    // <valor> → numero
    // <valor>.tipo = table(tipo, num.lex)
    // <valor>.valor = table(valor, num.lex)
    public Valor verificarValor(String lexema) {
        String tipo;
        Object valor;

        if (lexema.matches("-?\\d+")) {
            tipo = "int";
            valor = Integer.parseInt(lexema);
        } else if (lexema.equals("true") || lexema.equals("false")) {
            tipo = "boolean";
            valor = Boolean.parseBoolean(lexema);
        } else if (lexema.startsWith("\"") && lexema.endsWith("\"")) {
            tipo = "String";
            valor = lexema.substring(1, lexema.length() - 1);
        } else {
            System.out.println("Error: Tipo de dato desconocido para el lexema: " + lexema);
            return null;
        }

        if (tablaSimbolos.containsKey(lexema)) {
            Simbolo simbolo = tablaSimbolos.get(lexema);
            if (!simbolo.tipo.equals(tipo)) {
                System.out.println("Tipo de dato no coincidente para " + lexema);
                return null;
            }
            return new Valor(simbolo.tipo, simbolo.valor);
        } else {
            Simbolo nuevoSimbolo = new Simbolo(lexema, tipo, valor);
            tablaSimbolos.put(lexema, nuevoSimbolo);

            return new Valor(tipo, valor);
        }
    }

}
