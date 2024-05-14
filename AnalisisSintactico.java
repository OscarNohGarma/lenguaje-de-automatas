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

    public void Codigo() {
        try {
            currentLine++;
            Sentencia();
            Sentencia();
            Sentencia();
            Sentencia();
            Sentencia();
            // if (currentToken != " ") {
            // Codigo();
            // }
        } catch (Exception e) {

        }

    }

    public void Sentencia() {
        if (currentToken == "tipo") {
            nextToken();
            if (currentToken == "id") {
                nextToken();
                if (currentToken == "=") {
                    nextToken();
                    System.out.println(currentToken);
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
        } else {
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

    public void error(String tipo) {
        System.out.println("Error in line " + currentLine);
        System.out.println(tipo + " is expected");
        System.exit(0);
    }
}