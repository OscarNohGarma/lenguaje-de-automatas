package lenguaje;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

public class AnalisisSemantico {
    private Stack<Map<String, Simbolo>> pilaDeTablasSimbolos;
    private int temporalCounter;

    public AnalisisSemantico() {
        this.pilaDeTablasSimbolos = new Stack<>();
        this.pilaDeTablasSimbolos.push(new HashMap<>());
        this.temporalCounter = 0;
    }

    // Producción: <codigo> → <sentencia> <codigo>
    // Regla: C.codigo = S.codigo || C1.codigo
    public String procesarSentenciaTipoIdAsignacion(String idLugar, String tipo, String valorCodigo,
            String valorLugar) {
        StringBuilder intermediateCodeSen = new StringBuilder();
        intermediateCodeSen.append(valorCodigo);

        // Generación de código para la definición y asignación
        if (valorCodigo.equals("")) {
            String codigo = "\ndef " + idLugar + ", " + tipo + "\n" + idLugar + " = " + valorLugar;
            intermediateCodeSen.append(codigo);
        } else {
            String codigo = "\ndef " + idLugar + ", " + tipo + "\n" + valorCodigo + "\n" + idLugar + " = " + valorLugar;
            intermediateCodeSen.append(codigo);
        }
        return intermediateCodeSen.toString();
    }

    public String ejecutarCtrlFlujo(String pLugar, String cCodigo, int numTemporal) {
        StringBuilder intermediateCodeCtrl = new StringBuilder();
        String sLugar = "t" + numTemporal;
        numTemporal++;

        sLugar = sLugar + pLugar;

        intermediateCodeCtrl.append(sLugar);
        String code = "\nif_true " + sLugar + " C \nsalto else \netiq C: \n" + cCodigo + "\netiq else;";
        intermediateCodeCtrl.append(code);

        return intermediateCodeCtrl.toString();
    }

    // <sentencia> → tipo id = <valor> ;
    // id.tipo = add(tipo.tipo, id.lex)
    public void verificarDeclaracion(String id, String tipo) {
        for (Map<String, Simbolo> tabla : pilaDeTablasSimbolos) {
            if (tabla.containsKey(id)) {
                System.out.println("Error: Variable " + id + " ya fue declarada.");
                System.exit(0);
            }
        }

        Simbolo simbolo = new Simbolo(id, tipo);
        pilaDeTablasSimbolos.peek().put(id, simbolo);
    }

    // <sentencia> → tipo id = <valor> ;
    // Si id.tipo == <valor>.tipo entonces id.valor = <valor>.valor
    public void verificarAsignacion(String id, Valor valor) {
        Simbolo simbolo = null;
        for (Map<String, Simbolo> tabla : pilaDeTablasSimbolos) {
            if (tabla.containsKey(id)) {
                simbolo = tabla.get(id);
                break;
            }
        }

        if (simbolo == null) {
            System.out.println("Error: Variable " + id + " no ha sido declarada.");
            System.exit(0);
            return;
        }

        // Si los tipos coinciden
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
        String lugar;

        if (lexema.matches("-?\\d+")) {
            tipo = "int";
            lugar = lexema;
            valor = Integer.parseInt(lexema);
            return new Valor(tipo, valor, lugar, "");

        } else if (lexema.equals("true") || lexema.equals("false")) {
            tipo = "boolean";
            valor = Boolean.parseBoolean(lexema);
            lugar = lexema;
            return new Valor(tipo, valor, lexema, "");

        } else if (lexema.startsWith("\"") && lexema.endsWith("\"")) {
            tipo = "string";
            valor = lexema.substring(1, lexema.length() - 1);
            return new Valor(tipo, valor);
        }

        // Busca el lexema en la pila de tablas de símbolos De acuerdo a los ambitos
        for (int i = pilaDeTablasSimbolos.size() - 1; i >= 0; i--) {
            Map<String, Simbolo> tablaSimbolosActual = pilaDeTablasSimbolos.get(i);

            if (tablaSimbolosActual.containsKey(lexema)) {
                Simbolo simbolo = tablaSimbolosActual.get(lexema);
                lugar = lexema;
                return new Valor(simbolo.tipo, simbolo.valor, lugar, "");
            }
        }

        // Si no se encuentra el lexema
        System.out.println("Error: Variable no declarada o tipo de dato desconocido para el lexema: " + lexema);
        System.exit(0);
        return null;
    }

    public String verificarMensaje(String lugar, String codigo) {
        StringBuilder intermediateCodeVM = new StringBuilder();

        intermediateCodeVM.append(lugar);
        intermediateCodeVM.append(codigo);

        return intermediateCodeVM.toString();
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

                String lugarTemporal = generarTemporal();

                // Generamos el código intermedio para la llamada a read
                String codigo = "\nparam " + cadena + "\n" + lugarTemporal + " = call read 1 \nret file";

                // Retornamos el Valor con el tipo "file", el archivo, el lugar temporal y el
                // código generado
                return new Valor("file", archivo, lugarTemporal, codigo);
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

    private String generarTemporal() {
        // Método para generar un nombre único para un temporal, p. ej., t1, t2, t3...
        temporalCounter++;
        return "t" + temporalCounter;
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

    // Para ejectutar:
    // S.codigo = gen(P.codigo
    // 'call ' reservada P.num)
    public String ejecutarReservada(String resEval, ArrayList<Valor> parametros, int currentLine,
            String codigoParametros) {

        // contador = contador - 4;
        // currentTokVal = tokens.get(contador).getValor();
        // System.out.println("Evalacuión: " + resEval);
        StringBuilder intermediateCodeReser = new StringBuilder();
        int numParametros = parametros.size();
        String codigo = codigoParametros + "\ncall " + resEval + " " + numParametros + "\n";
        intermediateCodeReser.append(codigo);

        switch (resEval) {
            case "generateFile":
                generateFile(parametros, currentLine);
                break;
            case "print":
                print(parametros, currentLine);
                break;
            case "search":
                search(parametros, currentLine);
                break;
            default:
                break;
        }

        return intermediateCodeReser.toString();
        // contador = contador + 4;
        // currentTokVal = tokens.get(contador).getValor();
    }

    private void generateFile(ArrayList<Valor> parametros, int currentLine) {

        if (parametros.size() == 2) {
            if (parametros.get(1).tipo.equals("string")) {
                try {
                    PrintWriter writer = new PrintWriter(new File("./" + parametros.get(1).valor.toString()));
                    writer.println(parametros.get(0).valor.toString());
                    writer.close();
                    // System.out
                    // .println("Archivo generado con éxito. con el nombre " +
                    // parametros.get(1).valor.toString());
                } catch (IOException e) {
                    System.out.println("Error al crear el archivo.");
                }
            } else {
                System.out.println(
                        "Error in line " + (currentLine - 1)
                                + ". generateFile: file name needs to be a string");
                System.exit(0);
            }
        } else {
            System.out.println(
                    "Error in line " + (currentLine - 1)
                            + ". generateFile: two parameters (content , file name) are expected");
            System.exit(0);
        }
    }

    private void print(ArrayList<Valor> parametros, int currentLine) {
        /*
         * if (parametros.size() == 1) {
         * if (parametros.get(0).tipo.equals("file")) {
         * try (BufferedReader lector = new BufferedReader(new
         * FileReader(parametros.get(0).valor.toString()))) {
         * String linea;
         * 
         * // Leer el archivo línea por línea
         * while ((linea = lector.readLine()) != null) {
         * // Imprimir cada línea leída
         * System.out.println(linea);
         * }
         * 
         * } catch (IOException e) {
         * // Manejar excepción en caso de error
         * System.out.println("Ocurrió un error al leer el archivo: " + e.getMessage());
         * }
         * } else {
         * 
         * System.out.println(parametros.get(0).valor);
         * }
         * } else {
         * System.out.println("Error in line " + (currentLine - 1) +
         * ". print: Only one parameter is expected");
         * System.exit(0);
         * }
         */
    }

    // Busca si existe la palabra en un archivo recibe palabra y nombre del archivo
    private void search(ArrayList<Valor> parametros, int currentLine) {
        if (parametros.size() == 2) {
            if (parametros.get(1).tipo.equals("file")) {
                if (!parametros.get(0).tipo.equals("string")) {
                    System.out.println("Error in line " + (currentLine - 1)
                            + ". seatch:  content needs to be a string type ");
                    System.exit(0);
                }

                try (Scanner scanner = new Scanner(new File(parametros.get(1).valor.toString()))) {
                    boolean encontrado = false;
                    while (scanner.hasNextLine()) {
                        String linea = scanner.nextLine();
                        if (linea.contains(parametros.get(0).valor.toString())) {
                            encontrado = true;
                            break;
                        }
                    }
                    if (encontrado) {
                        // System.out.println("La cadena fue encontrada en el archivo.");
                    } else {
                        // System.out.println("La cadena no fue encontrada en el archivo.");
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("El archivo no existe.");
                }
            } else {
                System.out.println(
                        "Error in line " + (currentLine - 1)
                                + ". search:  file needs to be a file type");
                System.exit(0);
            }
        } else {
            System.out.println("Error in line " + (currentLine - 1)
                    + ". search: two parameters (content , file) are expected");
            System.exit(0);
        }
    }

    public void abrirAmbito() {
        pilaDeTablasSimbolos.push(new HashMap<>());
    }

    public void cerrarAmbito() {
        pilaDeTablasSimbolos.pop();
    }

}
