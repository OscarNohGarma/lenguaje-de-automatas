import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CatScanner {

    private File file;
    private ArrayList<Token> tokens;
    private ArrayList<Character> simbolos;
    private String codigo;
    private Stack<String> keys;
    private int currentLinea;
    private int currentCol;

    public CatScanner(File file) {
        this.file = file;
        keys = new Stack<>();
        tokens = new ArrayList<>();
        simbolos = new ArrayList<>();
        currentLinea = 1;
    }

    // * Lee el archivo y coloca cada caracter en un espacio del arreglo
    // simbolo*/
    public void leerSimbolos() {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int charCode;
            while ((charCode = br.read()) != -1) {
                char caracter = (char) charCode;
                simbolos.add(caracter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Character> getSimbolos() {
        return simbolos;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    // * Valida los caracteres y asigna los tokens a la tabla de simbolos
    public boolean validarTokens() {
        int punteroInicial = 0;
        int punteroRecorrido = 0;
        char next = simbolos.get(punteroInicial);
        int i = 0;
        boolean error = false;
        StringBuilder lexema;

        currentCol = 0;

        try {
            while (i < simbolos.size() && !error) {
                i++;
                next = simbolos.get(punteroInicial);
                simbolos.get(punteroInicial + 1);
                lexema = new StringBuilder();
                // currentCol += 1;
                // ? Validación de palabras reservadas
                if (Character.isLetter(next)) {
                    punteroRecorrido = punteroInicial;

                    while (punteroRecorrido < simbolos.size()) {

                        currentCol++;

                        next = simbolos.get(punteroRecorrido);
                        if (Character.isWhitespace(next)) {
                            // System.out.println("CADENA ACEPTADA: " + lexema);
                            punteroInicial = punteroRecorrido + 1;

                            // Lógica del autómata
                            Automata automata = new Automata(lexema.toString());
                            classifyString(automata.checkAutomata(), lexema.toString());
                            // System.out.println("puntero inicial ahora es :" + punteroInicial);
                            // System.out.println("\n");
                            // System.out.println("currenCol: " + currentCol);
                            break;
                        }
                        if (!Character.isLetter(next) && !Character.isDigit(next)) {
                            // !Error
                            error = true;
                            System.out.println(error(currentLinea, currentCol, next));
                            return false;
                        }

                        lexema.append(next);
                        punteroRecorrido += 1;

                    }
                } else if (Character.isDigit(next)) {
                    punteroRecorrido = punteroInicial;

                    while (punteroRecorrido < simbolos.size()) {

                        currentCol += 1;
                        next = simbolos.get(punteroRecorrido);
                        if (Character.isWhitespace(next)) {
                            // System.out.println("CADENA ACEPTADA: " + lexema);
                            punteroInicial = punteroRecorrido + 1;

                            // Lógica del autómata
                            Automata automata = new Automata(lexema.toString());
                            classifyString(automata.checkAutomata(), lexema.toString());
                            // System.out.println("puntero inicial ahora es :" + punteroInicial);
                            // System.out.println("\n");
                            // System.out.println();

                            break;
                        }
                        if (!Character.isDigit(next)) {
                            // !Error
                            error = true;
                            System.out.println(error(currentLinea, currentCol, next));
                            return false;
                        }

                        lexema.append(next);
                        punteroRecorrido += 1;
                    }
                } else if (Character.isISOControl(next)) {
                    if (next == '\n') {
                        punteroInicial += 1;
                        currentLinea += 1;
                        currentCol = 0;
                    }
                    if (next == '\r') {
                        // System.out.println(next + "RETORNO DE CARRO");
                        punteroInicial += 1;

                    }
                    if (next == '\t') {
                        // System.out.println(next + "TAB.\n");
                        punteroInicial += 3;
                        // currentLinea += 1;
                        // currentCol = 0;
                    }
                } else if (Character.isWhitespace(next)) {
                    // System.out.println(next + "ESPACIO EN BLANCO");
                    currentCol++;
                    punteroInicial++;
                } else if (next == '=' || next == '+' || next == '!') {

                    punteroRecorrido = punteroInicial;
                    while (punteroRecorrido < simbolos.size()) {
                        currentCol += 1;

                        next = simbolos.get(punteroRecorrido);
                        if (Character.isWhitespace(next)) {
                            // System.out.println("CADENA ACEPTADA: " + lexema);
                            punteroInicial = punteroRecorrido + 1;

                            // Lógica del autómata
                            Automata automata = new Automata(lexema.toString());
                            classifyString(automata.checkAutomata(), lexema.toString());
                            // System.out.println("puntero inicial ahora es :" + punteroInicial);
                            // System.out.println("\n");

                            break;
                        }
                        if (next != '=' && next != '+' && next != '!') {
                            // !Error
                            error = true;
                            System.out.println(error(currentLinea, currentCol, next));
                            return false;
                        }
                        lexema.append(next);
                        punteroRecorrido += 1;
                    }
                } else if (next == '(' || next == ')' || next == '{' || next == '}') {
                    punteroRecorrido = punteroInicial;

                    while (punteroRecorrido < simbolos.size()) {

                        currentCol += 1;
                        lexema.append(next);
                        next = simbolos.get(punteroRecorrido + 1);
                        if (Character.isWhitespace(next)) {
                            // System.out.println("CADENA ACEPTADA: " + lexema);
                            punteroInicial = punteroRecorrido + 1;

                            // Lógica del autómata
                            Automata automata = new Automata(lexema.toString());
                            classifyString(automata.checkAutomata(), lexema.toString());
                            // System.out.println("puntero inicial ahora es :" + punteroInicial);
                            // System.out.println("\n");

                            punteroRecorrido += 1;
                            break;
                        }
                        // !Error

                        currentCol += 1;
                        error = true;
                        System.out.println(error(currentLinea, currentCol, next));
                        return false;

                    }
                } else if (next == '"') {
                    currentCol++;
                    punteroRecorrido = punteroInicial;
                    next = simbolos.get(punteroRecorrido);
                    lexema.append(next);

                    while (punteroRecorrido < simbolos.size()) {
                        currentCol += 1;

                        punteroRecorrido += 1;
                        next = simbolos.get(punteroRecorrido);
                        lexema.append(next);
                        if (simbolos.get(punteroRecorrido + 1) == '"' && simbolos.get(punteroRecorrido + 2) == ' ') {

                            currentCol += 2;
                            lexema.append(simbolos.get(punteroRecorrido + 1));
                            punteroInicial = punteroRecorrido + 3;

                            // Lógica del autómata
                            // Automata automata = new Automata(lexema.toString());
                            // classifyString(automata.checkAutomata());

                            // System.out.println("CADENA ACEPTADA: " + lexema);
                            classifyString(9, lexema.toString());
                            // System.out.println("puntero inicial ahora es :" + punteroInicial);
                            // System.out.println("\n");

                            break;
                        } else if (simbolos.get(punteroRecorrido + 1) == '"'
                                && simbolos.get(punteroRecorrido + 2) != ' ') {
                            // !Error
                            currentCol += 2;
                            error = true;
                            System.out.println(error(currentLinea, currentCol, simbolos.get(punteroRecorrido + 2)));
                            return false;
                        }
                        if (next != ' ' && next != '"' && !Character.isLetter(next) && !Character.isDigit(next)
                                && next != '.' && next != ',' && next != '?' && next != '\u00BF'
                                && next != '!' && next != '/' && next != '*' && next != '-') {
                            // !Error
                            error = true;
                            System.out.println(error(currentLinea, currentCol, next));
                            return false;
                        }

                    }
                } else if (next == ';') {

                    punteroRecorrido = punteroInicial;
                    while (punteroRecorrido < simbolos.size()) {
                        currentCol += 1;

                        next = simbolos.get(punteroRecorrido);
                        if (Character.isWhitespace(next)) {
                            // System.out.println("CADENA ACEPTADA: " + lexema);
                            punteroInicial = punteroRecorrido + 1;

                            // Lógica del autómata
                            // Automata automata = new Automata(lexema.toString());
                            classifyString(15, ";");
                            // System.out.println("puntero inicial ahora es :" + punteroInicial);
                            // System.out.println("\n");

                            break;
                        }
                        if (next != ';') {
                            // !Error
                            error = true;
                            System.out.println(error(currentLinea, currentCol, next));
                            return false;
                        }
                        lexema.append(next);
                        punteroRecorrido += 1;
                    }
                } else if (next == ',') {

                    punteroRecorrido = punteroInicial;
                    while (punteroRecorrido < simbolos.size()) {
                        currentCol += 1;

                        next = simbolos.get(punteroRecorrido + 1);

                        if (Character.isWhitespace(next)) {
                            // System.out.println("CADENA ACEPTADA: " + lexema);
                            punteroInicial = punteroRecorrido + 1;

                            // Lógica del autómata
                            // Automata automata = new Automata(lexema.toString());
                            classifyString(18, ",");
                            // System.out.println("puntero inicial ahora es :" + punteroInicial);
                            // System.out.println("\n");

                            lexema.append(next);
                            punteroRecorrido += 1;
                            break;
                        }
                        // !Error
                        currentCol += 1;
                        error = true;
                        System.out.println(error(currentLinea, currentCol, next));
                        return false;
                    }
                } else {

                    // !Error
                    currentCol++;
                    System.out.println(error(currentLinea, currentCol, next));
                    return false;

                }
            }
        } catch (NullPointerException e) {
            System.out.println(error(currentLinea, currentCol, next));
        } catch (Exception e) {

            // System.out.println("Fin del archivo");
        }
        return true;

    }

    public String error(int linea, int col, char character) {
        return "An error occurred during compilation.\nError in line " + linea + ", col " + col + ".\n" + character
                + " is not valid, delete this character or separate it with a whitespace.";
    }

    public void classifyString(int input, String lexema) {
        switch (input) {
            case 1:
                // System.out.println(lexema + " : Es una concatenacion");
                tokens.add(new Token(lexema, "+"));
                break;

            case 2:
                // System.out.println(lexema + " : Es un Operador Lógico");
                tokens.add(new Token(lexema, "op logico"));
                break;

            case 3:
                // System.out.println(lexema + " : Es un Tipo de Dato");
                tokens.add(new Token(lexema, "tipo"));
                break;

            case 4:
                // System.out.println(lexema + " : Es una Palabra reservada");
                tokens.add(new Token(lexema, "reservada"));
                break;

            case 6:
                // System.out.println(lexema + " : Es una Variable");
                tokens.add(new Token(lexema, "id"));
                break;

            case 7:
                // System.out.println(lexema + " : Son Números");
                tokens.add(new Token(lexema, "numero"));
                break;

            case 8:
                // System.out.println(lexema + " : Es un valor de tipo Booleano");
                tokens.add(new Token(lexema, "booleano"));
                break;

            case 9:
                // System.out.println(lexema + " : Es una cadena de texto");
                tokens.add(new Token(lexema, "cadena"));
                break;
            case 10:
                // System.out.println(lexema + " : Es una igualación");
                tokens.add(new Token(lexema, "="));
                break;
            case 11:
                // System.out.println(lexema + " : Es un Signo de Agrupación (");
                tokens.add(new Token(lexema, "("));
                break;
            case 12:
                // System.out.println(lexema + " : Es un Signo de Agrupación )");
                tokens.add(new Token(lexema, ")"));
                break;
            case 13:
                // System.out.println(lexema + " : Es un Signo de Agrupación {");
                tokens.add(new Token(lexema, "{"));
                keys.push(lexema);
                break;
            case 14:
                // System.out.println(lexema + " : Es un Signo de Agrupación }");
                tokens.add(new Token(lexema, "}"));
                if (keys.size() == 0) {
                    System.out.println("An error occurred during compilation.");
                    System.out.println("Error in line " + currentLinea + ",  col " + currentCol
                            + "\n } is not valid (close without an open key), delete this token ");
                    System.exit(0);
                }
                keys.pop();
                break;
            case 15:
                // System.out.println(lexema + " : Fin de sentencia");
                tokens.add(new Token(lexema, ";"));
                break;
            case 16:
                // System.out.println(lexema + " : Operador de comparación");
                tokens.add(new Token(lexema, "op comp"));
                break;
            case 17:
                // System.out.println(lexema + " : Estructuras de control de flujo");
                tokens.add(new Token(lexema, "ctrl flujo"));
                break;
            case 18:
                // System.out.println(lexema + " : Separador de parametros");
                tokens.add(new Token(lexema, ","));
                break;
            case 19:
                // System.out.println(lexema + " : Lectura de archivo");
                tokens.add(new Token(lexema, "read"));
                break;
            default:
                // System.out.println(lexema + " : ERROR CADENA NO VALIDA PARA CLASIFICAR");
                // tokens.add(new Token(lexema, "NO RECONOCIDO: " + lexema));
                System.out.println("An error occurred during compilation.");
                System.out.println("Error in line " + currentLinea + ",  col " + (currentCol - lexema.length())
                        + "\nInvalid id name");
                System.exit(0);

                break;
        }
    }

}

class Token {
    private String valor;
    private String tipo;

    public Token(String valor, String tipo) {
        this.valor = valor;
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public String getTipo() {
        return tipo;
    }
}

class MainCat {
    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("No input file specified.");
            return;
        }


        // File file = new File("codigo.cat");
        File file = new File(args[0]);
        if (!file.exists()) {
            System.out.println("File not found: " + args[0]);
            return;
        }

        CatScanner cs = new CatScanner(file);
        cs.leerSimbolos();
        boolean val = cs.validarTokens();
        AnalisisSintactico as = new AnalisisSintactico(cs.getTokens());

        if (val) {
            as.Codigo();
            System.out.println("Successfully compiled.\n");

            System.out.println("+-------------Tabla de símbolos-------------+");
            System.out.printf("| %-20s| %-20s|%n", "Valor", "Tipo");
            System.out.println("+---------------------+---------------------+");
            for (Token token : as.getTokens()) {
                System.out.printf("| %-20s| %-20s|%n", token.getValor(), token.getTipo());
            }
            System.out.println("+---------------------+---------------------+");
        }
    }
}
