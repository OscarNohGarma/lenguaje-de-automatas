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
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public void nextToken() {
        try {
            contador++;
            currentToken = tokens.get(contador).getTipo();
            currentTokVal = tokens.get(contador).getValor();
            if (currentToken == "}" || currentToken == ";" || currentToken == "{") {
                currentLine++;
            }
        } catch (Exception e) {
            if (currentToken == "}")
                ;
            currentToken = "FIN";
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

                Sentencia();
                if (currentToken == "}" || currentToken == "FIN") {
                    break;
                }
            }
            // if (currentToken != " ") {
            // Codigo();
            // }
        } catch (Exception e) {

        }

    }

    public void Sentencia() {
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

                    analizadorSemantico.verificarDeclaracion(id, tipo);
                    analizadorSemantico.verificarAsignacion(id, this.valor);
                    if (currentToken == ";") {
                        // System.out.println("sentencia aceptada");
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
                        analizadorSemantico.ejecutarReservada(resEval, parametros, currentLine);
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
                ArrayList<Valor> parametros = Parametros();
                if (currentToken == ")") {
                    nextToken();
                    if (currentToken == "{") {
                        // System.out.println(currentToken);
                        // System.out.println("PUSH");
                        keys.push(currentToken);
                        nextToken();

                        Codigo();
                        if (currentToken == "}") {
                            // System.out.println(currentToken);
                            keys.pop();
                            // System.out.println("POP");
                            // System.out.println("sentencia aceptada");
                            nextToken();

                            Codigo();
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

    public void error(String tipo) {
        System.out.println("An error occurred during compilation.");
        System.out.println("Error in line " + (currentLine) + ".");
        System.out.println("'" + currentTokVal + "'. " + tipo + " is expected.");
        System.exit(0);
    }
}