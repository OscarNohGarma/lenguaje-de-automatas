
import java.util.ArrayList;
import java.util.Stack;

public class AnalisisSintactico {

    private ArrayList<Token> tokens;
    private String currentToken;
    private String currentTokVal;
    private int contador;
    private int currentLine;
    private Stack<String> keys;

    public AnalisisSintactico(ArrayList<Token> tokens) {
        this.tokens = tokens;
        // currentToken = tokens.get(contador).getTipo();
        currentToken = tokens.get(contador).getTipo();
        currentTokVal = tokens.get(contador).getValor();
        keys = new Stack<>();
        contador = 0;
        currentLine = 1;
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
            nextToken();
            if (currentToken == "id") {
                nextToken();
                if (currentToken == "=") {
                    nextToken();
                    Valor();
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
            nextToken();
            if (currentToken == "(") {
                nextToken();
                Parametros();
                if (currentToken == ")") {
                    nextToken();
                    if (currentToken == ";") {
                        // System.out.println("sentencia aceptada");
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
                Parametros();
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

    public void Valor() {

        if (currentToken == "numero") {
            nextToken();
            return;
        } else if (currentToken == "booleano") {
            nextToken();
            return;
        } else if (currentToken == "id") {
            nextToken();
            return;
        } else if (currentToken == "read") {
            nextToken();
            if (currentToken == "(") {
                nextToken();
                if (currentToken == "cadena") {
                    nextToken();
                    if (currentToken == ")") {
                        nextToken();
                        return;
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
            Mensaje();
            return;
        }
        error("Valid data type");
    }

    public void Mensaje() {

        if (currentToken == "cadena") {
            nextToken();
        }
        if (currentToken == "+") {
            nextToken();
            if (currentToken == "cadena") {
                Mensaje();
            } else {
                error("String");
            }
        }
    }

    public void Parametros() {
        Valor();
        if (currentToken == ")") {
            return;
        }
        if (currentToken == ",") {
            nextToken();
            Valor();
            return;
        }
        prevToken();
        Validacion();

    }

    public void Validacion() {
        Condicion();

        if (currentToken == ")") {
            return;

        } else if (currentToken == "op logico") {
            nextToken();
            Validacion();
        } else {
            error("Logical operator");
        }
        // System.out.println(currentToken);

    }

    public void Condicion() {
        Valor();
        if (currentToken == "op comp") {
            nextToken();
            Valor();
        } else {
            error("Comparation operator");
        }
    }

    public void error(String tipo) {
        System.out.println("An error occurred during compilation.");
        System.out.println("Error in line " + (currentLine) + ".");
        System.out.println("'" + currentTokVal + "'. " + tipo + " is expected.");
        System.exit(0);
    }
}