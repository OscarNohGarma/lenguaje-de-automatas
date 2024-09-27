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
            return;
        }

        Simbolo simbolo = tablaSimbolos.get(id);

        // Si id.tipo == <valor>.tipo
        if (simbolo.getTipo().equals(valor.tipo)) {
            simbolo.setValor(valor.valor);

        } else {
            System.out.println("Error: Tipo de dato incompatible. Se esperaba " + simbolo.getTipo()
                    + " pero se recibió " + valor.tipo);
        }
    }

    // <valor> → numero | <mensaje> | booelano | id
    // <valor>.tipo = table(tipo, lex)
    // <valor>.valor = table(valor, lex)
    public Valor verificarValor(String lexema) {

        if (tablaSimbolos.containsKey(lexema)) {
            Simbolo simbolo = tablaSimbolos.get(lexema);
            return new Valor(simbolo.tipo, simbolo.valor);
        }

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

    // <valor> → read ( cadena )
    // Si cadena.tipo == 'String' entonces cadena.valor = table(cadena.valor,
    // cadena.lex) <valor>.tipo = 'file';
    public Valor procesarRead(String cadena) {
        Valor valorCadena = verificarValor(cadena);

        if (valorCadena != null && valorCadena.tipo.equals("String")) {
            String nombreArchivo = (String) valorCadena.valor;
            File archivo = new File("./lenguaje/" + nombreArchivo);

            if (archivo.exists()) {
                return new Valor("file", archivo);
            } else {
                System.out.println("Error: El archivo " + nombreArchivo + " no existe.");
                return null;
            }
        } else {
            System.out.println("Error: Se esperaba una cadena para read, pero se recibió: " + cadena);
            return null;
        }
    }

}
