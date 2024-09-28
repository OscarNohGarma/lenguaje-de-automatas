package lenguaje;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import java.io.BufferedReader;
import java.io.FileReader;

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
                        ejecutarReservada(resEval, parametros);
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

    private void ejecutarReservada(String resEval, ArrayList<Valor> parametros) {
        // contador = contador - 4;
        // currentTokVal = tokens.get(contador).getValor();
        // System.out.println("Evalacuión: " + resEval);
        switch (resEval) {
            case "generateFile":
                generateFile(parametros);
                break;
            case "print":
                print(parametros);
                break;
            case "search":
                search(parametros);
                break;
            default:
                error("Reservada");
                break;
        }

        // contador = contador + 4;
        // currentTokVal = tokens.get(contador).getValor();
    }

    private void generateFile(ArrayList<Valor> parametros) {

        if (parametros.size() == 2) {
            if (parametros.get(1).tipo.equals("string")) {
                try {
                    PrintWriter writer = new PrintWriter(new File("./" + parametros.get(1).valor.toString()));
                    writer.println(parametros.get(0).valor.toString());
                    writer.close();
                    System.out
                            .println("Archivo generado con éxito. con el nombre " + parametros.get(1).valor.toString());
                } catch (IOException e) {
                    System.out.println("Error al crear el archivo.");
                }
            } else {
                System.out.println(
                        "Error in line " + (currentLine - 1)
                                + ". generateFile: file name needs to be a string");
                System.exit(0);
            }
        } else {
            System.out.println(
                    "Error in line " + (currentLine - 1)
                            + ". generateFile: two parameters (content , file name) are expected");
            System.exit(0);
        }
    }

    private void print(ArrayList<Valor> parametros) {
        if (parametros.size() == 1) {
            if (parametros.get(0).tipo.equals("file")) {
                try (BufferedReader lector = new BufferedReader(new FileReader(parametros.get(0).valor.toString()))) {
                    String linea;

                    // Leer el archivo línea por línea
                    while ((linea = lector.readLine()) != null) {
                        // Imprimir cada línea leída
                        System.out.println(linea);
                    }

                } catch (IOException e) {
                    // Manejar excepción en caso de error
                    System.out.println("Ocurrió un error al leer el archivo: " + e.getMessage());
                }
            } else {

                System.out.println(parametros.get(0).valor);
            }
        } else {
            System.out.println("Error in line " + (currentLine - 1) + ". print: Only one parameter is expected");
            System.exit(0);
        }
    }

    // Busca si existe la palabra en un archivo recibe palabra y nombre del archivo
    private void search(ArrayList<Valor> parametros) {
        if (parametros.size() == 2) {
            if (parametros.get(1).tipo.equals("file")) {
                if (!parametros.get(0).tipo.equals("string")) {
                    System.out.println("Error in line " + (currentLine - 1)
                            + ". seatch:  content needs to be a string type ");
                    System.exit(0);
                }

                try (Scanner scanner = new Scanner(new File(parametros.get(1).valor.toString()))) {
                    boolean encontrado = false;
                    while (scanner.hasNextLine()) {
                        String linea = scanner.nextLine();
                        if (linea.contains(parametros.get(0).valor.toString())) {
                            encontrado = true;
                            break;
                        }
                    }
                    if (encontrado) {
                        System.out.println("La cadena fue encontrada en el archivo.");
                    } else {
                        System.out.println("La cadena no fue encontrada en el archivo.");
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("El archivo no existe.");
                }
            } else {
                System.out.println(
                        "Error in line " + (currentLine - 1)
                                + ". search:  file needs to be a file type");
                System.exit(0);
            }
        } else {
            System.out.println("Error in line " + (currentLine - 1)
                    + ". search: two parameters (content , file) are expected");
            System.exit(0);
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