package lenguaje;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
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
                    String valor = currentTokVal;
                    this.valor = Valor();

                    analizadorSemantico.verificarDeclaracion(id, tipo);
                    analizadorSemantico.verificarAsignacion(id, valor);
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
                        ejecutarReservada();
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

    private void ejecutarReservada() {
        contador = contador - 4;
        currentTokVal = tokens.get(contador).getValor();
        String reservada = currentTokVal;
        System.out.println("valor actual");
        System.out.println(currentTokVal);
        switch (reservada) {
            case "generateFile":
                generateFile();
                break;
            case "print":
                print();
                break;
            case "search":
                search();
                break;
            default:
                error("Reservada");
                break;
        }

        contador = contador + 4;
        currentTokVal = tokens.get(contador).getValor();
    }

    private void generateFile() {
        String texto = "";
        try {
            PrintWriter writer = new PrintWriter(new File("output.txt"));
            writer.println(texto);
            writer.close();
            System.out.println("Archivo generado con éxito. con el nombre output.txt");
        } catch (IOException e) {
            System.out.println("Error al crear el archivo.");
        }
    }

    private void print() {
        System.out.println(currentTokVal);
    }

    // Busca si existe la palabra en un archivo recibe palabra y nombre del archivo
    private void search() {
        String palabra = "";
        String nombreArchivo = "";

        try (Scanner scanner = new Scanner(new File(nombreArchivo))) {
            boolean encontrado = false;
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                if (linea.contains(palabra)) {
                    encontrado = true;
                    break;
                }
            }
            if (encontrado) {
                System.out.println("La palabra fue encontrada en el archivo.");
            } else {
                System.out.println("La palabra no fue encontrada en el archivo.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("El archivo no existe.");
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
                    String cadena = currentToken;
                    nextToken();
                    if (currentToken == ")") {
                        nextToken();
                        return analizadorSemantico.verificarValor(cadena);
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
            String cadenaCompleta = Mensaje();
            return analizadorSemantico.verificarValor(cadenaCompleta);
        }
        error("Valid data type");
        return null;

    }

    public String Mensaje() {
        String mensajeCompleto = "";

        if (currentToken == "cadena") {
            mensajeCompleto = currentTokVal;
            nextToken();
        }
        while (currentToken == "+") {
            nextToken();
            if (currentToken == "cadena") {
                mensajeCompleto += currentTokVal;
                nextToken();
            } else {
                error("Expected string after '+'");
            }
        }

        return mensajeCompleto;
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