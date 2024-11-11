package lenguaje;

import java.util.ArrayList;
import java.util.Stack;

public class AnalisisSintactico {

    private ArrayList<Token> tokens;
    private String currentToken;
    private String currentTokVal;
    private int contador;
    private int currentLine;
    private Stack<String> keys;
    private AnalisisSemantico analizadorSemantico;
    private Valor valor;
    private StringBuilder intermediateCode;

    public AnalisisSintactico(ArrayList<Token> tokens) {
        this.tokens = tokens;
        // currentToken = tokens.get(contador).getTipo();
        currentToken = tokens.get(contador).getTipo();
        currentTokVal = tokens.get(contador).getValor();
        keys = new Stack<>();
        contador = 0;
        currentLine = 1;
        this.analizadorSemantico = new AnalisisSemantico();
        this.valor = new Valor("", "");
        intermediateCode = new StringBuilder();
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public void nextToken() {
        try {
            contador++;
            currentToken = tokens.get(contador).getTipo();
            currentTokVal = tokens.get(contador).getValor();

            if (currentToken.equals("}") || currentToken.equals(";") || currentToken.equals("{")) {
                currentLine++; // Incrementa el contador de línea en cada '}', '{' o ';'
            }
        } catch (IndexOutOfBoundsException e) { // Captura específicamente la excepción de índice
            currentToken = "FIN"; // Finaliza el análisis cuando no hay más tokens
        }
    }

    public void prevToken() {
        contador--;
        currentToken = tokens.get(contador).getTipo();
        currentTokVal = tokens.get(contador).getValor();
    }

    public void Codigo() {
        try {
            while (true) {
                // currentLine++;
                String code = Sentencia();
                intermediateCode.append(code);
                // Sentencia();
                if (currentToken.equals("}") || currentToken.equals("FIN")) {
                    break;
                }
            }
            // if (currentToken != " ") {
            // Codigo();
            // }
        } catch (Exception e) {

        }
        System.out.println("Código generado:\n" + intermediateCode.toString());
    }

    public String Sentencia() {
        StringBuilder code = new StringBuilder();

        // System.out.println(currentToken);
        if (currentToken == "tipo") {
            String tipo = currentTokVal;
            nextToken();
            if (currentToken == "id") {
                String id = currentTokVal;
                nextToken();
                if (currentToken == "=") {
                    nextToken();
                    // String valor = currentTokVal;
                    this.valor = Valor();

                    // String valorCodigo = obtenerCodigoValor();
                    // String valorLugar = obtenerLugarValor();

                    analizadorSemantico.verificarDeclaracion(id, tipo);
                    analizadorSemantico.verificarAsignacion(id, this.valor);

                    String codigoSentencia = analizadorSemantico.procesarSentenciaTipoIdAsignacion(id, tipo,
                            this.valor.codigo,
                            this.valor.lugar);

                    code.append(codigoSentencia);

                    if (currentToken == ";") {
                        nextToken();

                    } else {
                        error(";");
                    }
                } else {
                    error("=");
                }
            } else {
                error("Identifier");
            }

        } else if (currentToken == "reservada") {
            String resEval = currentTokVal;
            nextToken();
            if (currentToken == "(") {
                nextToken();
                ArrayList<Valor> parametros = Parametros();
                if (currentToken == ")") {
                    nextToken();
                    if (currentToken == ";") {
                        // System.out.println("sentencia aceptada");
                        // Pasar el P.codigo
                        String codigoIntermedioReser = analizadorSemantico.ejecutarReservada(resEval, parametros,
                                currentLine, "");

                        code.append(codigoIntermedioReser);
                        nextToken();
                    } else {
                        error(";");
                    }
                } else {
                    error("Too much parameters or )");
                }

            } else {

                error("(");
            }
        } else if (currentToken == "ctrl flujo") {
            nextToken();
            if (currentToken == "(") {
                nextToken();
                // ArrayList<Valor> parametros = Parametros();
                Valor resultadoCondicion = Validacion();

                if (currentToken == ")") {
                    nextToken();
                    if (currentToken == "{") {
                        // System.out.println(currentToken);
                        // System.out.println("PUSH");
                        keys.push(currentToken);
                        nextToken();
                        analizadorSemantico.abrirAmbito();

                        verificarBloque();

                        // Si la condición del if es verdadero o falso
                        if (resultadoCondicionEsVerdadera(resultadoCondicion)) {
                            Codigo();
                            String ctrl = analizadorSemantico.ejecutarCtrlFlujo("",
                                    intermediateCode.toString(), 0);
                            code.append(ctrl);
                        } else {
                            saltarBloque();
                        }

                        analizadorSemantico.cerrarAmbito();

                        if (currentToken == "}") {
                            // System.out.println(currentToken);
                            keys.pop();
                            // System.out.println("POP");
                            // System.out.println("sentencia aceptada");
                            nextToken();

                            if (currentTokVal.equals("else")) {
                                nextToken();
                                if (currentToken.equals("{")) {
                                    keys.push(currentToken);
                                    nextToken();

                                    analizadorSemantico.abrirAmbito();

                                    // Si la condición anterior fue falsa
                                    if (!resultadoCondicionEsVerdadera(resultadoCondicion)) {

                                        Codigo(); // Ejecuta el bloque de código del else
                                    } else {
                                        saltarBloque(); // Si la condición del if fue verdadera, saltamos el else
                                    }

                                    analizadorSemantico.cerrarAmbito();

                                    if (currentToken.equals("}")) {
                                        keys.pop();
                                        nextToken();
                                    } else {
                                        error("} before else");
                                    }
                                } else {
                                    error("{ after else");
                                }
                            }

                            // Codigo();
                            // prevToken();
                            // System.out.println("CUR" + currentToken);
                        } else {
                            // currentLine++;
                            error("Uncomplete flow control structure, } ");
                        }
                    } else {
                        error("{");
                    }
                } else {
                    error(")");
                }

            } else {
                error("(");
            }
        } else {
            if (currentToken != "}" && currentToken != "FIN") {
                error("Datatype, reserved word or flow control");
            }
        }

        return code.toString();
    }

    public Valor Valor() {
        String lexema = currentTokVal;

        if (currentToken == "numero") {
            nextToken();
            return analizadorSemantico.verificarValor(lexema);
        } else if (currentToken == "booleano") {
            nextToken();
            return analizadorSemantico.verificarValor(lexema);
        } else if (currentToken == "id") {
            nextToken();
            return analizadorSemantico.verificarValor(lexema);
        } else if (currentToken == "read") {
            nextToken();
            if (currentToken == "(") {
                nextToken();
                if (currentToken == "cadena") {
                    String cadena = currentTokVal;
                    nextToken();
                    if (currentToken == ")") {
                        nextToken();
                        return analizadorSemantico.procesarRead(cadena);
                    } else {
                        error(")");
                    }
                } else {
                    error("File name in string form");
                }
            } else {
                error("(");
            }
        } else if (currentToken == "cadena") {
            Valor mensajeValor = Mensaje();

            return new Valor(mensajeValor.tipo, mensajeValor.valor);
        }
        error("Valid data type");
        return null;

    }

    public Valor Mensaje() {
        String mensajeCompleto = "";

        if (currentToken == "cadena") {
            mensajeCompleto = currentTokVal.substring(1, currentTokVal.length() - 1);
            ;
            nextToken();
        }

        while (currentToken == "+") {
            nextToken();
            if (currentToken == "cadena") {
                mensajeCompleto += currentTokVal.substring(1, currentTokVal.length() - 1);
                nextToken();
            } else {
                error("Expected string after '+'");
            }
        }
        return new Valor("string", mensajeCompleto);
    }

    public ArrayList<Valor> Parametros() {
        ArrayList<Valor> parametros = new ArrayList<>();
        Valor val1 = Valor();
        parametros.add(val1);
        // System.out.println(val.valor);
        // System.out.println(val.tipo);
        if (currentToken == ")") {
            return parametros;
        }
        if (currentToken == ",") {
            nextToken();
            Valor val2 = Valor();
            parametros.add(val2);
            return parametros;
        }
        prevToken();
        parametros.clear();
        parametros.add(Validacion());

        return parametros;

    }

    public Valor Validacion() {
        Valor C = Condicion();

        if (currentToken == ")") {
            return C;

        } else if (currentToken == "op logico") {
            String opL = currentTokVal;
            nextToken();
            Valor vali = Validacion();
            return analizadorSemantico.validacion(C, vali, opL);

        } else {
            error("Logical operator");
        }
        // System.out.println(currentToken);
        return null;

    }

    public Valor Condicion() {
        Valor v1 = Valor();
        if (currentToken == "op comp") {
            String opC = currentTokVal;
            nextToken();
            Valor v2 = Valor();
            return analizadorSemantico.comparacion(v1, v2, opC);
        } else {
            error("Comparation operator");
        }
        return null;
    }

    private boolean resultadoCondicionEsVerdadera(Valor resultadoCondicion) {
        return resultadoCondicion.tipo.equals("boolean") && (boolean) resultadoCondicion.valor;
    }

    public void saltarBloque() {
        int contadorLlaves = 1;

        while (contadorLlaves > 0 && currentToken != null) {
            nextToken();
            if (currentToken.equals("{")) {
                contadorLlaves++;
            }

            else if (currentToken.equals("}")) {
                contadorLlaves--;
            }
        }

        if (contadorLlaves != 0) {
            error("Missing closing brace ");
        }
    }

    public void verificarBloque() {

    }

    public void error(String tipo) {
        System.out.println("An error occurred during compilation.");
        System.out.println("Error in line " + (currentLine) + ".");
        System.out.println("'" + currentTokVal + "'. " + tipo + " is expected.");
        System.exit(0);
    }
}