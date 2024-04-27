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

    public CatScanner(File file) {
        this.file = file;
        tokens = new ArrayList<>();
        simbolos = new ArrayList<>();
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

    public void leerArchivo() {
        int punteroInicial = 0;
        int punteroRecorrido = 0;
        char next = simbolos.get(punteroInicial);
        int i = 0;
        boolean error = false;
        StringBuilder lexema;

        try {
            while (i < simbolos.size() && !error) {
                i++;
                next = simbolos.get(punteroInicial);
                System.out.println("---- Caracter actual: " + next + "----");
                lexema = new StringBuilder();
                // ? Validación de palabras reservadas
                if (Character.isLetter(next)) {
                    punteroRecorrido = punteroInicial;

                    while (punteroRecorrido < simbolos.size()) {

                        next = simbolos.get(punteroRecorrido);
                        if (Character.isWhitespace(next) || next == '(' || next == ')' || next == '{' || next == '}'
                                || next == '[' || next == ']') {
                            System.out.println("CADENA ACEPTADA: " + lexema);
                            punteroInicial = punteroRecorrido + 1;
                            System.out.println("puntero inicial ahora es :" + punteroInicial);

                            // Lógica del autómata
                            Automata automata = new Automata(lexema.toString());
                            classifyString(automata.checkAutomata());
                            System.out.println("\n");
                            break;
                        }
                        if (!Character.isLetter(next)) {
                            // !Error
                            System.out.println(next + " NO ES UNA LETRA");
                            error = true;
                            break;
                        }
                        // System.out.println(next);

                        lexema.append(next);
                        punteroRecorrido += 1;
                    }
                } else if (Character.isDigit(next)) {
                    punteroRecorrido = punteroInicial;
                    while (punteroRecorrido < simbolos.size()) {

                        next = simbolos.get(punteroRecorrido);
                        if (Character.isWhitespace(next)) {
                            System.out.println("CADENA ACEPTADA: " + lexema);
                            punteroInicial = punteroRecorrido + 1;
                            System.out.println("puntero inicial ahora es :" + punteroInicial);

                            // Lógica del autómata
                            Automata automata = new Automata(lexema.toString());
                            classifyString(automata.checkAutomata());
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
                        // System.out.println(next);

                        lexema.append(next);
                        punteroRecorrido += 1;
                    }
                } else if (Character.isISOControl(next)) {
                    // punteroRecorrido = punteroInicial;
                    if (next == '\n') {
                        System.out.println(next + "es un salto de linea.");
                        punteroInicial += 1;
                    }
                } else if (Character.isWhitespace(next)) {
                    System.out.println(next + " es un espacio en blanco o salto de linea.");
                } else if (next == '=' || next == '+' || next == '-') {
                    punteroRecorrido = punteroInicial;
                    while (punteroRecorrido < simbolos.size()) {

                        next = simbolos.get(punteroRecorrido);
                        if (Character.isWhitespace(next)) {
                            System.out.println("CADENA ACEPTADA: " + lexema);
                            punteroInicial = punteroRecorrido + 1;
                            System.out.println("puntero inicial ahora es :" + punteroInicial);

                            // Lógica del autómata
                            Automata automata = new Automata(lexema.toString());
                            classifyString(automata.checkAutomata());
                            System.out.println("\n");

                            break;
                        }
                        if (next != '=') {
                            // !Error
                            System.out.println(next + " NO ES VÁLIDO");
                            error = true;
                            break;
                        }
                        lexema.append(next);
                        // System.out.println(next);
                        punteroRecorrido += 1;
                    }
                } else if (next == '(' || next == ')' || next == '{' || next == '}' || next == '[' || next == ']') {
                    punteroRecorrido = punteroInicial;
                    while (punteroRecorrido < simbolos.size()) {

                        next = simbolos.get(punteroRecorrido);
                        if (Character.isWhitespace(next)) {
                            System.out.println("CADENA ACEPTADA: " + lexema);
                            punteroInicial = punteroRecorrido + 1;
                            System.out.println("puntero inicial ahora es :" + punteroInicial);

                            // // Lógica del autómata
                            Automata automata = new Automata(lexema.toString());
                            classifyString(automata.checkAutomata());
                            System.out.println("\n");

                            break;
                        }
                        if (next != '(' && next != ')' && next != '{' && next != '}' && next != '[' && next != ']') {
                            // !Error
                            System.out.println(next + " NO ES VÁLIDO");
                            error = true;
                            break;

                        }

                        lexema.append(next);
                        // System.out.println(next);
                        punteroRecorrido += 1;
                    }
                } else if (next == ';') {
                    punteroRecorrido = punteroInicial;
                    while (punteroRecorrido < simbolos.size()) {

                        next = simbolos.get(punteroRecorrido);
                        if (Character.isWhitespace(next)) {
                            System.out.println("CADENA ACEPTADA: " + lexema);
                            punteroInicial = punteroRecorrido + 1;
                            System.out.println("puntero inicial ahora es :" + punteroInicial);

                            // Lógica del autómata
                            Automata automata = new Automata(lexema.toString());
                            classifyString(automata.checkAutomata());
                            System.out.println("\n");

                            break;
                        }
                        if (next != ';') {
                            // !Error
                            System.out.println(next + " NO ES VÁLIDO");
                            error = true;
                            break;
                        }
                        // System.out.println(next);
                        lexema.append(next);
                        punteroRecorrido += 1;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Fin del archivo");
        }

        // try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        // int charCode;
        // while ((charCode = br.read()) != -1) {
        // next = (char) charCode;
        // // Validar si next es una letra
        // if (Character.isLetter(next)) {
        // System.out.println(next + " " + charCode + " es una letra.");
        // }

        // // Validar si next es un dígito
        // else if (Character.isDigit(next)) {
        // System.out.println(next + " " + charCode + " es un dígito.");
        // }

        // // Validar si next es un símbolo de control
        // else if (Character.isISOControl(next)) {
        // if (next == '\n') {
        // System.out.println(next + " " + charCode + " es un salto de línea.");
        // } else if (next == '\r') {
        // System.out.println(next + " " + charCode + " es un retorno de carro.");
        // } else {
        // System.out.println(next + " " + charCode + " es un símbolo de control.");
        // }
        // } else if (next == ' ') {
        // System.out.println(next + " " + charCode + " es un espacio en blanco.");
        // }
        // // Otros casos
        // else {
        // System.out.println(next + " " + charCode + " es otro tipo de carácter.");
        // }
        // }
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
    }

    public void classifyString(int input) {
        switch (input) {
            case 1:
                System.out.println("Es un Operador Aritmético");
                break;

            case 2:
                System.out.println("Es un Operador Lógico");
                break;

            case 3:
                System.out.println("Es un Tipo de Dato");
                break;

            case 4:
                System.out.println("Es una Palabra reservada");
                break;

            case 5:
                System.out.println("Es un Signo de Agrupación");
                break;

            case 6:
                System.out.println("Es una Variable");
                break;

            case 7:
                System.out.println("Son Números");
                break;

            default:
                System.out.println("ERROR CADENA NO VALIDA PARA CLASIFICAR");
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

        // for (char c : cs.getSimbolos()) {
        // System.out.print(c);
        // }
    }
}
