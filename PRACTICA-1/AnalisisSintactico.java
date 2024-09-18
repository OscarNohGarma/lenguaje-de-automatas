import java.util.ArrayList;

public class AnalisisSintactico {

    private ArrayList<Token> tokens;
    private String currentToken;
    private String currentTokVal;
    private int contador;
    private int currentLine;
    private ReglasSemanticas reglasSemanticas;

    public AnalisisSintactico(ArrayList<Token> tokens) {
        this.tokens = tokens;
        currentToken = tokens.get(contador).getTipo();
        currentToken = tokens.get(contador).getTipo();
        currentTokVal = tokens.get(contador).getValor();
        contador = 0;
        currentLine = 1;
        reglasSemanticas = new ReglasSemanticas();
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
        String tTipo = T(); // T.tipo
        if (currentToken == "id") {
            reglasSemanticas.verifyDeclaration(tTipo, currentTokVal); // id.tipo = add(T.tipo, id.lex)
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
            C(tTipo); // C.tipo = T.tipo

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

    public void C(String cTipo) {
        if (currentToken == ",") {
            nextToken();
            if (currentToken == "id") {
                reglasSemanticas.verifyDeclaration(cTipo, currentTokVal); // id.tipo = add(C.tipo, id.lex)
                nextToken();
                C(cTipo); // C.tipo = Cp.tipo
            } else {
                error("id");
            }
        }
    }

    public void F() {
        Symbol E;
        String idAsignment = "";
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
            idAsignment = currentTokVal;
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
                E = E();
                reglasSemanticas.assignIdValue(idAsignment, E.getType(), E.getValue());
                // System.out.println("*****" + E.type + "**** " + E.value);
                // System.out.println(E);
                if (currentToken == ";" || currentToken == "FIN") {
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

    public String T() {
        if (currentToken == "entero") {
            nextToken();
            return reglasSemanticas.getTypeForInteger(); // T.tipo = entero
        } else if (currentToken == "flotante") {
            nextToken();
            return reglasSemanticas.getTypeForFloat(); // T.tipo = flotante
        } else {
            error("Valid type");
        }
        return "";
    }

    public Symbol E() {

        Symbol E1 = new Symbol(null, null);
        Symbol E2 = new Symbol(null, null);
        if (currentToken == "id") {
            // E.valor = table (valor, id.lex)
            // E.tipo = table (tipo, id.lex)
            E1 = reglasSemanticas.processIdE(currentTokVal);
            // System.out.println(E.getValue());
            // System.out.println(E.getType());

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
            // return E1;
        } else if (currentToken == "num") {
            // E.valor = table (valor, num.lex)
            // E.tipo = table (tipo, num.lex)
            E1 = reglasSemanticas.processNum(currentTokVal);
            // System.out.println(E.getValue());
            // System.out.println(E.getType());
            nextToken();

            // return;
        } else if (currentToken == "-") {
            nextToken();
            E1 = E();
            Expression E = reglasSemanticas.evaluateNegation(E1);
            E1 = new Symbol(E.getType(), E.getValue());

            // return;
        } else if (currentToken == "(") {
            nextToken();
            E1 = E();
            if (currentToken == ")") {
                nextToken();
                // return;
            } else {
                error(")");
            }
            // return;
        } else {
            error("id or num");
        }
        System.out.println(E1.getType() + ": " + E1.getValue());
        if (E1.getType() == null) {
            // System.out.println("Tipo ES NULO");
        } else if (E1.getValue() == null) {
            // System.out.println("Valor es nulo");
        }
        Expression E;
        switch (currentToken) {
            case "+":
                nextToken();
                // System.out.println(currentTokVal);
                E2 = E();
                E = reglasSemanticas.evaluateAdd(E1, E2);
                E1 = new Symbol(E.getType(), E.getValue());
                break;
            case "*":
                nextToken();
                E2 = E();
                E = reglasSemanticas.evaluateMul(E1, E2);
                E1 = new Symbol(E.getType(), E.getValue());
                break;
            case "/":
                nextToken();
                E2 = E();
                E = reglasSemanticas.evaluateDiv(E1, E2);
                E1 = new Symbol(E.getType(), E.getValue());

                break;
            case "-":
                nextToken();
                E2 = E();
                E = reglasSemanticas.evaluateSub(E1, E2);
                E1 = new Symbol(E.getType(), E.getValue());

                break;
            default:
                break;
        }
        // if (E1.getType() == E2.getType()) {
        // } else{

        // E1 = E1.getType();
        // }
        return E1;
    }

    public void error(String tipo) {
        System.out.println("An error occurred during compilation.");
        System.out.println("Error in line " + (currentLine) + ".");
        System.out.println("'" + currentTokVal + "'. " + tipo + " is expected.");
        System.exit(0);
    }
}