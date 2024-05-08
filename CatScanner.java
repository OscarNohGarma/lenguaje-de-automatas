import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CatScanner {

    private File file;
    private ArrayList<String> tokens;
    private ArrayList<Character> simbolos;
    private String codigo;
    private int currentLinea;
    private int currentCol;

    public CatScanner(File file) {
        this.file = file;
        tokens = new ArrayList<>();
        simbolos = new ArrayList<>();
        currentLinea = 1;
    }

    public void separar() {
        Pattern pattern = Pattern.compile("\\s+|\\{|\\}|\\[|\\]|\\(|\\)|;|:|,|\"|_|\\+|-|\\*|/|\\\\|>|<|=|`|'");
        Matcher matcher = pattern.matcher(codigo);

        int inicio = 0;
        while (matcher.find()) {
            int fin = matcher.start();
            if (fin > inicio) {
                String token = codigo.substring(inicio, fin);
                tokens.add(token);
            }

            // Agregar el símbolo encontrado como token
            String symbol = matcher.group();
            if (!symbol.trim().isEmpty()) { // Ignorar espacios en blanco
                tokens.add(symbol);
            }

            inicio = matcher.end();
        }

        // Agregar el último token si hay caracteres restantes
        if (inicio < codigo.length()) {
            String token = codigo.substring(inicio);
            tokens.add(token);
        }
    }

    // * TEST: Lee el archivo y coloca cada caracter en un espacio del arreglo
    // simbolo*/
    public void leerArchivoTest() {
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

    public ArrayList<String> getTokens() {
        return tokens;
    }

    public void leerArchivo() {
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
                // System.out.println("---- Caracter actual: " + next + "----");
                simbolos.get(punteroInicial + 1);
                lexema = new StringBuilder();
                // currentCol += 1;
                // ? Validación de palabras reservadas
                if (Character.isLetter(next)) {
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
                            System.out.println("puntero inicial ahora es :" + punteroInicial);
                            System.out.println("\n");
                            break;
                        }
                        if (!Character.isLetter(next) && !Character.isDigit(next)) {
                            // !Error
                            System.out.println(next + " NO ES UN CARACTER");
                            error = true;

                            System.out.println(error(currentLinea, currentCol));
                            break;
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
                            System.out.println("puntero inicial ahora es :" + punteroInicial);
                            System.out.println("\n");
                            System.out.println();

                            break;
                        }
                        if (!Character.isDigit(next)) {
                            // !Error
                            System.out.println(next + " NO ES UN DIGITO");
                            error = true;
                            break;
                        }

                        lexema.append(next);
                        punteroRecorrido += 1;
                    }
                } else if (Character.isISOControl(next)) {
                    if (next == '\n') {
                        System.out.println(next + "SALTO DE LÍNEA.\n");
                        punteroInicial += 1;
                        currentLinea += 1;
                        currentCol = 0;
                    }
                    if (next == '\r') {
                        System.out.println(next + "RETORNO DE CARRO");
                        punteroInicial += 1;
                    }
                    if (next == '\t') {
                        System.out.println(next + "TAB.\n");
                        punteroInicial += 3;
                        // currentLinea += 1;
                        // currentCol = 0;
                    }
                } else if (Character.isWhitespace(next)) {
                    System.out.println(next + "ESPACIO EN BLANCO");
                    punteroInicial++;
                } else if (next == '=' || next == '+' || next == '-' || next == '!') {

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
                            System.out.println("puntero inicial ahora es :" + punteroInicial);
                            System.out.println("\n");

                            break;
                        }
                        if (next != '=' && next != '+' && next != '-' && next != '!') {
                            // !Error
                            System.out.println(next + " NO ES VÁLIDO");
                            error = true;
                            break;
                        }
                        lexema.append(next);
                        punteroRecorrido += 1;
                    }
                } else if (next == '(' || next == ')' || next == '{' || next == '}') {
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
                            System.out.println("puntero inicial ahora es :" + punteroInicial);
                            System.out.println("\n");

                            break;
                        }
                        if ((next != '(' && next != ')' && next != '{' && next != '}')
                                || (simbolos.get(punteroRecorrido + 1) == '(')
                                || (simbolos.get(punteroRecorrido + 1) == ')')
                                || (simbolos.get(punteroRecorrido + 1) == '[')
                                || (simbolos.get(punteroRecorrido + 1) == ']')
                                || (simbolos.get(punteroRecorrido + 1) == '{')
                                || (simbolos.get(punteroRecorrido + 1) == '}')) {
                            // !Error
                            System.out.println(next + " NO ES VÁLIDO");
                            error = true;
                            break;

                        }

                        lexema.append(next);
                        punteroRecorrido += 1;
                    }
                } else if (next == '"') {
                    punteroRecorrido = punteroInicial;
                    next = simbolos.get(punteroRecorrido);
                    lexema.append(next);

                    while (punteroRecorrido < simbolos.size()) {
                        currentCol += 1;

                        punteroRecorrido += 1;
                        next = simbolos.get(punteroRecorrido);
                        lexema.append(next);
                        if (simbolos.get(punteroRecorrido + 1) == '"') {

                            lexema.append(simbolos.get(punteroRecorrido + 1));
                            punteroInicial = punteroRecorrido + 3;

                            // Lógica del autómata
                            // Automata automata = new Automata(lexema.toString());
                            // classifyString(automata.checkAutomata());

                            // System.out.println("CADENA ACEPTADA: " + lexema);
                            classifyString(9, lexema.toString());
                            System.out.println("puntero inicial ahora es :" + punteroInicial);
                            System.out.println("\n");

                            break;
                        }
                        if (next != ' ' && next != '"' && !Character.isLetter(next) && !Character.isDigit(next)
                                && next != '.' && next != ',' && next != '?' && next != '¿' && next != '¡'
                                && next != '!') {
                            // !Error
                            System.out.println(next + " NO ES VÁLIDO");
                            error = true;
                            break;
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
                            System.out.println("puntero inicial ahora es :" + punteroInicial);
                            System.out.println("\n");

                            break;
                        }
                        if (next != ';') {
                            // !Error
                            System.out.println(next + " NO ES VÁLIDO");
                            error = true;
                            break;
                        }
                        lexema.append(next);
                        punteroRecorrido += 1;
                    }
                }
            }
        } catch (NullPointerException e) {
            System.out.println(error(currentLinea, currentCol));
        } catch (Exception e) {

            System.out.println("Fin del archivo");
        }

    }

    public String error(int linea, int col) {
        return "Error en la línea " + linea + ", columna " + col;
    }

    public void classifyString(int input, String lexema) {
        switch (input) {
            case 1:
                System.out.println(lexema + " : Es un Operador Aritmético");
                tokens.add("op aritmetico");
                break;

            case 2:
                System.out.println(lexema + " : Es un Operador Lógico");
                tokens.add("op logico");
                break;

            case 3:
                System.out.println(lexema + " : Es un Tipo de Dato");
                tokens.add("tipo");
                break;

            case 4:
                System.out.println(lexema + " : Es una Palabra reservada");
                tokens.add("reservada");
                break;

            case 6:
                System.out.println(lexema + " : Es una Variable");
                tokens.add("id");
                break;

            case 7:
                System.out.println(lexema + " : Son Números");
                tokens.add("numero");
                break;

            case 8:
                System.out.println(lexema + " : Es un valor de tipo Booleano");
                tokens.add("booleano");
                break;

            case 9:
                System.out.println(lexema + " : Es una cadena de texto");
                tokens.add("cadena");
                break;
            case 10:
                System.out.println(lexema + " : Es una igualación");
                tokens.add("=");
                break;
            case 11:
                System.out.println(lexema + " : Es un Signo de Agrupación (");
                tokens.add("(");
                break;
            case 12:
                System.out.println(lexema + " : Es un Signo de Agrupación )");
                tokens.add(")");
                break;
            case 13:
                System.out.println(lexema + " : Es un Signo de Agrupación {");
                tokens.add("{");
                break;
            case 14:
                System.out.println(lexema + " : Es un Signo de Agrupación }");
                tokens.add("}");
                break;
            case 15:
                System.out.println(lexema + " : Fin de sentencia");
                tokens.add(";");
                break;
            case 16:
                System.out.println(lexema + " : Operador de comparación");
                tokens.add("op comp");
                break;
            case 17:
                System.out.println(lexema + " : Estructuras de control de flujo");
                tokens.add("ctrl flujo");
                break;
            default:
                System.out.println(lexema + " : ERROR CADENA NO VALIDA PARA CLASIFICAR");
                tokens.add("NO RECONOCIDO: " + lexema);

                break;
        }
    }

}

class MainCat {
    public static void main(String[] args) {
        File file = new File("codigo.txt");
        CatScanner cs = new CatScanner(file);
        cs.leerArchivoTest();
        cs.leerArchivo();
        System.out.println("\n---- Tabla de símbolos ----\n");
        for (String token : cs.getTokens()) {
            System.out.println(token);
        }
    }
}
