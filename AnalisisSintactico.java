import java.util.ArrayList;

public class AnalisisSintactico {

    private ArrayList<Token> tokens;
    private String currentToken;
    private int contador;
    private int currentLine;

    public AnalisisSintactico(ArrayList<Token> tokens) {
        this.tokens = tokens;
        // currentToken = tokens.get(contador).getTipo();
        currentToken = tokens.get(contador).getTipo();
        contador = 0;
        currentLine = 0;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public void nextToken() {
        contador++;
        currentToken = tokens.get(contador).getTipo();
    }

    public void prevToken() {
        contador--;
        currentToken = tokens.get(contador).getTipo();
    }

    public void Codigo() {
        try {
            while (true) {
                currentLine++;
                Sentencia();
                if (currentToken == "}") {
                    break;
                }
            }
            // if (currentToken != " ") {
            // Codigo();
            // }
        } catch (Exception e) {
            // System.out.println("error");
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
                        System.out.println("sentencia aceptada");
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
                        System.out.println("sentencia aceptada");
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
                        System.out.println(currentToken);
                        nextToken();

                        Codigo();
                        if (currentToken == "}") {
                            System.out.println(currentToken);
                            System.out.println("sentencia aceptada");
                            nextToken();
                            Codigo();
                        } else {
                            error("}");
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
        System.out.println("Error in line " + currentLine);
        System.out.println(tipo + " is expected");
        System.exit(0);
    }
}