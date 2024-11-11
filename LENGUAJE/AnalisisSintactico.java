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
    private CodigoLugarValor valor;
    GenCodIntermedio gci = new GenCodIntermedio();
    private StringBuilder intermediateCode;

    public AnalisisSintactico(ArrayList<Token> tokens) {
        this.tokens = tokens;
        // currentToken = tokens.get(contador).getTipo();
        currentToken = tokens.get(contador).getTipo();
        currentTokVal = tokens.get(contador).getValor();
        keys = new Stack<>();
        contador = 0;
        currentLine = 1;
        this.analizadorSemantico = new AnalisisSemantico();
        this.valor = new CodigoLugarValor(new Valor("", ""), "", "");
        intermediateCode = new StringBuilder();
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

    public String Codigo() {
        try {
            while (true) {
                // currentLine++;
                String code = Sentencia();
                // Sentencia();
                intermediateCode.append(code);
                if (currentToken == "}" || currentToken == "FIN") {

                    break;

                }
            }
            // // if (currentToken != " ") {
            // // Codigo();
            // // }
        } catch (Exception e) {

        }
        return intermediateCode.toString();
    }

    public String Sentencia() {
        StringBuilder code = new StringBuilder();
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
                    // ! Aquíe obtiene V.lugar y V.codigo
                    this.valor = Valor();
                    // System.out.println("tipo: " + valor.getValor().tipo);
                    // System.out.println("valor: " + valor.getValor().valor);
                    // System.out.println("codigo: " + valor.getCodigo());
                    // System.out.println("lugar: " + valor.getLugar() + "\n");

                    analizadorSemantico.verificarDeclaracion(id, tipo);
                    analizadorSemantico.verificarAsignacion(id, this.valor.getValor());
                    String codigoAsignacion = analizadorSemantico.procesarSentenciaTipoIdAsignacion(id, tipo,
                            valor.getCodigo(),
                            valor.getLugar());

                    intermediateCode.append(codigoAsignacion);
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
                // ! Aquí obtiene P.codigo, P.parametros, P.lugar y P.nParametros
                Param parametros = Parametros();
                // ? System.out.println(parametros.getCodigo());
                // ? System.out.println(parametros.getLugar());
                // ? System.out.println(parametros.getnParam());
                if (currentToken == ")") {
                    nextToken();
                    if (currentToken == ";") {
                        // System.out.println("sentencia aceptada");
                        ArrayList<Valor> newParametros = new ArrayList<>();
                        for (CodigoLugarValor codigoLugarValor : parametros.getParametros()) {
                            newParametros.add(codigoLugarValor.getValor());
                        }
                        String codigoReservada = analizadorSemantico.ejecutarReservada(resEval, newParametros,
                                currentLine,
                                parametros.getCodigo());
                        intermediateCode.append(codigoReservada);
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
                // ArrayList<Valor> parametros = Parametros();
                // ! Aqui comentaste Parametros y lo sustituiste por Validacion pero es lo
                // ! mismo, corresponde a la produccion P -> Valid
                // ! Es decir, obtienes P.codigo, y P.lugar y los guardas en resultadoCondicion
                CodigoLugarValor resultadoCondicion = Validacion();
                // ? System.out.println("Codigo de resultado condición " +
                // resultadoCondicion.getCodigo());
                // ? System.out.println("Lugar de resultado condición " +
                // resultadoCondicion.getLugar());

                if (currentToken == ")") {
                    nextToken();
                    if (currentToken == "{") {
                        String cCodigoV, cCodigoF;
                        // System.out.println(currentToken);
                        // System.out.println("PUSH");
                        keys.push(currentToken);
                        nextToken();
                        analizadorSemantico.abrirAmbito();

                        int startLength = intermediateCode.length();

                        // Si la condición del if es verdadero o falso
                        // if (resultadoCondicionEsVerdadera(resultadoCondicion.getValor())) {
                        Codigo();

                        cCodigoV = intermediateCode.substring(startLength);
                        intermediateCode.setLength(startLength);

                        // String codigoCtrl =
                        // analizadorSemantico.ejecutarCtrlFlujo(resultadoCondicion.getCodigo(),
                        // resultadoCondicion.getLugar(), codigoGeneradoIf);

                        // intermediateCode.append(codigoCtrl);

                        // } else {
                        // saltarBloque();
                        // }

                        analizadorSemantico.cerrarAmbito();

                        if (currentToken == "}") {
                            // System.out.println(currentToken);
                            keys.pop();
                            // System.out.println("POP");
                            // System.out.println("sentencia aceptada");
                            nextToken();

                            if (currentTokVal.equals("else")) {
                                nextToken();
                                if (currentToken.equals("{")) {
                                    keys.push(currentToken);
                                    nextToken();

                                    analizadorSemantico.abrirAmbito();

                                    // Si la condición anterior fue falsa
                                    // if (!resultadoCondicionEsVerdadera(resultadoCondicion.getValor())) {

                                    Codigo(); // Ejecuta el bloque de código del else

                                    cCodigoF = intermediateCode.substring(startLength);
                                    intermediateCode.setLength(startLength);

                                    // String codigoCtrl = analizadorSemantico.ejecutarCtrlFlujo(
                                    // resultadoCondicion.getCodigo(),
                                    // resultadoCondicion.getLugar(), codigoGeneradoIf);

                                    // intermediateCode.append(codigoCtrl);

                                    String codigoCtrl = analizadorSemantico.ejecutarCtrlFlujo(
                                            resultadoCondicion.getCodigo(),
                                            resultadoCondicion.getLugar(), cCodigoV, cCodigoF);
                                    intermediateCode.append(codigoCtrl);
                                    // } else {
                                    // saltarBloque(); // Si la condición del if fue verdadera, saltamos el else
                                    // }

                                    analizadorSemantico.cerrarAmbito();

                                    if (currentToken.equals("}")) {
                                        keys.pop();
                                        nextToken();
                                    } else {
                                        error("} before else");
                                    }
                                } else {
                                    error("{ after else");
                                }
                            }

                            // Codigo();
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
        return code.toString();

    }

    // V => id | num | bool | etc
    public CodigoLugarValor Valor() {
        String lexema = currentTokVal;

        if (currentToken == "numero") {
            nextToken();
            Valor newValor = analizadorSemantico.verificarValor(lexema);
            // ! V.lugar = num.lugar
            // ! V.codigo = ""
            return new CodigoLugarValor(newValor, "", lexema);// Verifica el lexema, y devuelve el valor y tipo, el
                                                              // codigo como "" y el lugar como el valor del lexema
        } else if (currentToken == "booleano") {
            // ! V.lugar = boolean.lugar
            // ! V.codigo = ""
            nextToken();
            Valor newValor = analizadorSemantico.verificarValor(lexema);
            return new CodigoLugarValor(newValor, "", lexema);// Verifica el lexema, y devuelve el valor y tipo, el
                                                              // codigo como "" y el lugar como el valor del lexema
        } else if (currentToken == "id") {
            // ! V.lugar = id.lugar
            // ! V.codigo = ""
            nextToken();
            Valor newValor = analizadorSemantico.verificarValor(lexema);
            return new CodigoLugarValor(newValor, "", lexema);// Verifica el lexema, y devuelve el valor y tipo, el
                                                              // codigo como "" y el lugar como el valor del lexema
        } else if (currentToken == "read") {
            nextToken();
            if (currentToken == "(") {
                nextToken();
                if (currentToken == "cadena") {
                    String cadena = currentTokVal;
                    nextToken();
                    if (currentToken == ")") {
                        nextToken();

                        Valor newValor = analizadorSemantico.procesarRead(cadena);
                        // ? REVISAR ----------V
                        return new CodigoLugarValor(newValor, "", cadena);
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
            // ! V.lugar = M.lugar
            // ! V.codigo = M.codigo
            CodigoLugarValor mensajeValor = Mensaje(); // Recupera los atributos de Mensaje y los retorna
            // System.out.println(mensajeValor.getValor().tipo);
            // System.out.println(mensajeValor.getValor().valor);
            // System.out.println(mensajeValor.getCodigo());
            // System.out.println(mensajeValor.getLugar());
            return mensajeValor;
        }
        error("Valid data type");
        return null;

    }

    public CodigoLugarValor Mensaje() {
        String mensajeCompleto = "";
        StringBuffer codigo = new StringBuffer();
        // ! M.codigo = ""
        String cadenaLugar = "";
        String Mplugar = "";
        String Mlugar = "";

        if (currentToken == "cadena") {
            mensajeCompleto = currentTokVal.substring(1, currentTokVal.length() - 1);
            cadenaLugar = '"' + mensajeCompleto + '"';
            // ! M.lugar = cadena.lugar
            Mlugar = Mplugar = cadenaLugar;
            nextToken();
        }
        while (currentToken == "+") {
            nextToken();
            if (currentToken == "cadena") {
                // Genera nuevos temporales
                // ! M.lugar = temporal
                Mplugar = gci.newTemporal();
                mensajeCompleto += currentTokVal.substring(1, currentTokVal.length() - 1);
                cadenaLugar = '"' + currentTokVal.substring(1, currentTokVal.length() - 1) + '"';
                // ! M.codigo = cadena.codigo || M.codigo || gen (Mp.lugar '=' cadena.lugar '+'
                // ! M.lugar)
                codigo.append(Mplugar + " = " + Mlugar + " + " + cadenaLugar + "\n");
                Mlugar = Mplugar;
                nextToken();
            } else {
                error("Expected string after '+'");
            }
        }
        // System.out.println("codigo: \n" + codigo);
        // System.out.println("lugar: " + Mplugar);

        return new CodigoLugarValor(new Valor("string", mensajeCompleto),
                codigo.toString(), Mplugar); // Retorna el
                                             // tipo, valor,
                                             // codigo, y
                                             // lugar de
                                             // Mensaje
    }

    public Param Parametros() {
        StringBuffer codigo = new StringBuffer();
        ArrayList<CodigoLugarValor> parametros = new ArrayList<>();
        CodigoLugarValor val1 = Valor();

        parametros.add(val1);
        // System.out.println(val.valor);
        // System.out.println(val.tipo);
        if (currentToken == ")") {
            // ! P.codigo = V.codigo || gen ('param' V.lugar)
            // ! P.lugar = ""
            // ! P.nParametros = 1
            codigo.append(val1.getCodigo());
            codigo.append("param " + val1.getLugar() + "\n");

            // System.out.println(codigo);
            return new Param(parametros, 1, "", codigo.toString());
        }
        if (currentToken == ",") {
            nextToken();
            CodigoLugarValor val2 = Valor();
            parametros.add(val2);

            // ! P.codigo = V1.codigo || V2.codigo || gen ('param' V1.lugar 'param'
            // ! V2.lugar)
            // ! P.lugar = ""
            // ! P.nParametros = 2
            codigo.append(val1.getCodigo());
            codigo.append(val2.getCodigo());
            codigo.append("param " + val1.getLugar() + "\n");
            codigo.append("param " + val2.getLugar());

            // System.out.println(codigo);
            return new Param(parametros, 2, "", codigo.toString());
        }

        return null;

    }

    public CodigoLugarValor Validacion() {
        StringBuffer codigo = new StringBuffer();

        CodigoLugarValor C = Condicion();

        if (currentToken == ")") {
            // ! Valid.codigo = Comp.codigo
            // ! Valid.lugar = Comp.lugar
            return C;

        } else if (currentToken == "op logico") {
            String opL = currentTokVal;
            nextToken();
            CodigoLugarValor vali = Validacion();
            Valor result = analizadorSemantico.validacion(C.getValor(), vali.getValor(), opL);
            // ! Valid.lugar = temporal
            String newTemp = gci.newTemporal();
            // ! Validp.codigo = Valid.codigo || Comp.codigo || gen (Validp.lugar '='
            // ! Comp.lugar opL Valid.lugar)
            codigo.append(C.getCodigo() + vali.getCodigo() + newTemp + " = " + C.getLugar() + " " + opL + " "
                    + vali.getLugar() + "\n");
            // System.out.println(codigo);
            // ? REVISAR ---------V
            return new CodigoLugarValor(result, codigo.toString(), newTemp);

        } else {
            error("Logical operator");
        }
        // System.out.println(currentToken);
        return null;

    }

    public CodigoLugarValor Condicion() {
        StringBuffer codigo = new StringBuffer();
        CodigoLugarValor v1 = Valor();

        if (currentToken == "op comp") {
            String opC = currentTokVal;
            nextToken();
            CodigoLugarValor v2 = Valor();
            Valor result = analizadorSemantico.comparacion(v1.getValor(), v2.getValor(), opC);
            // ! Comp.lugar = temporal
            String newTemp = gci.newTemporal();
            // ! Comp.codigo = V1.codigo || V2.codigo || gen (Comp.lugar '=' V1.lugar opC
            // ! V2.lugar)
            codigo.append(v1.getCodigo() + v2.getCodigo() + newTemp + " = " + v1.getLugar() + " " + opC + " "
                    + v2.getLugar() + "\n");
            // System.out.println(codigo);
            return new CodigoLugarValor(result, codigo.toString(), newTemp);
        } else {
            error("Comparation operator");
        }
        return null;
    }

    private boolean resultadoCondicionEsVerdadera(Valor resultadoCondicion) {
        return resultadoCondicion.tipo.equals("boolean") && (boolean) resultadoCondicion.valor;
    }

    public void saltarBloque() {
        int contadorLlaves = 1;

        while (contadorLlaves > 0 && currentToken != null) {
            nextToken();
            if (currentToken.equals("{")) {
                contadorLlaves++;
            }

            else if (currentToken.equals("}")) {
                contadorLlaves--;
            }
        }

        if (contadorLlaves != 0) {
            error("Missing closing brace ");
        }
    }

    public void verificarBloque() {

    }

    public void error(String tipo) {
        System.out.println("An error occurred during compilation.");
        System.out.println("Error in line " + (currentLine) + ".");
        System.out.println("'" + currentTokVal + "'. " + tipo + " is expected.");
        System.exit(0);
    }
}