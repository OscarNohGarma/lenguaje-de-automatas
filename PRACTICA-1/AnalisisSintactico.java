import java.util.ArrayList;

public class AnalisisSintactico {

    private ArrayList<Token> tokens;
    private String currentToken;
    private String currentTokVal;
    private int contador;
    private int currentLine;

    public AnalisisSintactico(ArrayList<Token> tokens) {
        this.tokens = tokens;
        currentToken = tokens.get(contador).getTipo();
        currentToken = tokens.get(contador).getTipo();
        currentTokVal = tokens.get(contador).getValor();
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

    public void P() {
        try {
            D();
            F();
        } catch (Exception e) {

        }

    }

    public void D() {
        // System.out.println(currentToken);
        T();
        if (currentToken == "id") {
            nextToken();
            if (currentToken == "[") {
                nextToken();
                if (currentToken == "num") {
                    nextToken();
                    if (currentToken == "]") {
                        nextToken();
                        if (currentToken == ";") {
                            // !
                            System.out.println("SENTENCIA ACEPTADA");
                            nextToken();
                            OptionalD();
                            return;
                        } else {
                            error(";");
                        }
                    } else {
                        error("]");
                    }
                } else {
                    error("num");
                }
            }
            C();

            if (currentToken == ";") {
                // !
                System.out.println("SENTENCIA ACEPTADA");
                nextToken();
                OptionalD();
                return;
            } else {
                error(";");
            }

        }
    }

    public void OptionalD() {
        if (currentToken == "entero" || currentToken == "flotante") {
            D();
        }
    }

    public void C() {
        if (currentToken == ",") {
            nextToken();
            if (currentToken == "id") {
                nextToken();
                C();
            } else {
                error("id");
            }
        }
    }

    public void F() {
        if (currentToken == "print") {
            nextToken();
            E();
            // * IMPRIMIR
            if (currentToken == ";") {
                // !
                System.out.println("sentencia aceptada");
                nextToken();
                OptionalF();
                return;
            } else {
                error(";");
            }
        } else if (currentToken == "id") {
            nextToken();
            if (currentToken == "[") {
                nextToken();
                E();
                if (currentToken == "]") {
                    nextToken();
                    if (currentToken == "=") {
                        nextToken();
                        E();
                        if (currentToken == ";") {
                            // !
                            System.out.println("SENTENCIA ACEPTADA");
                            nextToken();
                            OptionalF();
                            return;
                        } else {
                            error(";");
                        }
                    } else {
                        error("=");
                    }
                } else {
                    error("]");
                }

            }
            if (currentToken == "=") {
                nextToken();
                E();
                if (currentToken == ";") {
                    // !
                    System.out.println("SENTENCIA ACEPTADA");
                    nextToken();
                    OptionalF();
                    return;
                } else {
                    error(";");
                }
            } else {
                error("=");
            }

        } else {
            error("print or id");
        }
    }

    public void OptionalF() {
        if (currentToken == "id" || currentToken == "print") {
            F();
        } else if (currentToken != "FIN") {
            error("print or id");
        }
    }

    public void T() {
        if (currentToken == "entero") {
            nextToken();
        } else if (currentToken == "flotante") {
            nextToken();
        } else {
            error("Valid type");
        }
    }

    public void E() {
        if (currentToken == "id") {
            nextToken();
            if (currentToken == "[") {
                nextToken();
                E();
                if (currentToken == "]") {
                    nextToken();
                    // return;
                } else {
                    error("]");
                }

            }
            return;
        } else if (currentToken == "num") {
            nextToken();
            // return;
        } else if (currentToken == "-") {
            nextToken();
            E();
            // return;
        } else if (currentToken == "(") {
            nextToken();
            E();
            if (currentToken == ")") {
                nextToken();
                // return;
            } else {
                error(")");
            }
            E();
            // return;
        }
        switch (currentToken) {
            case "+":
                nextToken();
                E();
                break;
            case "*":
                nextToken();
                E();
                break;
            case "/":
                nextToken();
                E();
                break;
            case "-":
                nextToken();
                E();
                break;
            default:
                break;
        }
    }

    public void error(String tipo) {
        System.out.println("An error occurred during compilation.");
        System.out.println("Error in line " + (currentLine) + ".");
        System.out.println("'" + currentTokVal + "'. " + tipo + " is expected.");
        System.exit(0);
    }
}