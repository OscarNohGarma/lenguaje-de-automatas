import java.beans.Expression;
import java.util.HashMap;
import java.util.Map;

public class ReglasSemanticas {

    // Aquí se debe almacenar la tabla de simbolos
    private Map<String, Symbol> symbolTable = new HashMap<>();

    public ReglasSemanticas(Map<String, Symbol> symbolTable) {
        this.symbolTable = symbolTable;
    }

    // Sacamos los simbolos individualmente
    public class Symbol {
        String type;
        String value;
        private int baseAddress;

        Symbol(String type) {
            this.type = type;
        }

        public Symbol(String type, String value) {
            this.type = type;
            this.type = value;
        }

        public Symbol(String type, int baseAddress) {
            this.type = type;
            this.baseAddress = baseAddress;
        }

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        public int getBaseAddress() {
            return baseAddress; // Getter for base address
        }

    }

    // Regla para validar D -> T id C ; y D -> T id C, D - con id
    public void verifyDeclarationD(String type, String id) {
        if (symbolTable.containsKey(id)) {
            System.out.println("Error: Variable " + id + " ya fue declarada.");
        } else {
            Symbol symbol = new Symbol(type);
            symbolTable.put(id, symbol);
            System.out.println("Variable " + id + " declarada con tipo " + type);
        }
    }

    // Regla para validar D -> T id C ; y D -> T id C, D - con C
    public void verifyDeclarationDWithC(String type, String[] idsInC) {
        // Por si hay varias Cs
        for (String id : idsInC) {
            if (symbolTable.containsKey(id)) {
                System.out.println("Error: Variable " + id + " Ya fue declarada.");
            } else {
                Symbol symbol = new Symbol(type);
                symbolTable.put(id, symbol);
                System.out.println("Variable " + id + " declarado con " + type);
            }
        }
    }

    // Regla para validar C -> , id C
    public void processC(String parentType, String[] idsInC) {
        // Regla 2: C.tipo = Cpadre.tipo
        String currentCType = parentType;

        for (String id : idsInC) {
            if (symbolTable.containsKey(id)) {
                System.out.println("Error: Variable " + id + " has already been declared.");
            } else {
                Symbol symbol = new Symbol(currentCType);
                symbolTable.put(id, symbol);
                System.out.println("Variable " + id + " declared with type " + currentCType);
            }
        }
    }

    // Regla para E -> id
    public void processIdE(String id) {
        // Verificar si el identificador existe en la tabla de símbolos
        if (symbolTable.containsKey(id)) {
            Symbol symbol = symbolTable.get(id);
            String tipo = symbol.getType();
            String valor = symbol.getValue();

            String eTipo = tipo; // E.tipo = table(Tipo, id.lex)
            String eValor = valor; // E.valor = table(valor, id.lex)

            System.out.println("E.type = " + eTipo);
            System.out.println("E.value = " + eValor);
        } else {
            System.out.println("Error: Variable " + id + " is not declared.");
        }
    }

    // Regla: E -> num
    public void processNum(String numLex) {
        if (symbolTable.containsKey(numLex)) {
            Symbol symbol = symbolTable.get(numLex);
            String tipo = symbol.getType();
            String valor = symbol.getValue();

            String eTipo = tipo; // E.tipo = table(Tipo, num.lex)
            String eValor = valor; // E.valor = table(valor, num.lex)

            System.out.println("E.type = " + eTipo);
            System.out.println("E.value = " + eValor);
        } else {
            // En el caso de que el número no este en la tabla
            if (numLex.matches("\\d+")) { // Entero
                System.out.println("E.type = entero");
                System.out.println("E.value = " + numLex);
            } else if (numLex.matches("\\d+\\.\\d+")) { // Flotante
                System.out.println("E.type = flotante");
                System.out.println("E.value = " + numLex);
            } else {
                System.out.println("Error: Invalid number format.");
            }
        }
    }

    // Regla para: F -> id = E ; F y F -> id = E ;
    public void assignIdValue(String idLex, String eTipo, String eValor) {
        if (symbolTable.containsKey(idLex)) {
            Symbol symbol = symbolTable.get(idLex);
            String idTipo = symbol.getType();

            // Se verifican los tipos
            if (idTipo.equals(eTipo)) {
                symbolTable.put(idLex, new Symbol(idTipo, eValor));
                System.out.println("Assigned value " + eValor + " to " + idLex);
            } else {
                System.out.println("Error: Type mismatch. Cannot assign " + eValor + " to " + idLex +
                        " (id type: " + idTipo + ", E type: " + eTipo + ")");
            }
        } else {
            System.out.println("Error: Variable " + idLex + " is not declared.");
        }
    }

    // Regla para la producción T -> entero
    public String getTypeForInteger() {
        String tType = "entero";
        System.out.println("T.tipo = " + tType);
        return tType;
    }

    // Regla para: T -> flotante
    public String getTypeForFloat() {
        String tType = "flotante";
        System.out.println("T.tipo = " + tType);
        return tType;
    }

    public String assign(String lexeme, int value, String type) {
        System.out.println("Asignación de dirección " + lexeme + " con el valor " + value + " y el tipo " + type);
        return "address";
    }

    // Producción D -> T id [ num ] ;
    public void handleDWithArray(String tipo, String idLex, int numValor, String numTipo) {
        if ("entero".equals(numTipo)) {
            String direccion = assign(idLex, numValor, tipo);
            System.out.println("D.direccion = " + direccion);
        } else {
            System.out.println("Error: num.tipo must be 'entero'. Found: " + numTipo);
        }
    }

    // Mostrar dirección
    public String get(String lexeme, int displacement) {
        if (symbolTable.containsKey(lexeme)) {
            Symbol symbol = symbolTable.get(lexeme);
            int baseAddress = symbol.getBaseAddress(); // Assuming Symbol has a method to get base address

            // Calculate the final address based on displacement
            int finalAddress = baseAddress + displacement;

            System.out.println(
                    "Retrieved address for " + lexeme + " with displacement " + displacement + ": " + finalAddress);
            return String.valueOf(finalAddress); // Return the retrieved address as a string
        } else {
            System.out.println("Error: Variable " + lexeme + " not found in symbol table.");
            return null; // Return null if the variable is not found
        }
    }

    // La producción E -> id [ E ] ;
    public void handleIdWithArrayE(String idLex, String eType, int eValue, String idType) {
        if ("integer".equals(eType)) {
            // Calcular el desplazamiento
            int displacement = Integer.parseInt(idType) * eValue;

            String address = get(idLex, displacement);
            System.out.println("E.direccion = " + address);
        } else {
            System.out.println("Error: E.type must be 'integer'. Found: " + eType);
        }
    }

    public String calculateArrayAddress(String idLex, int numValue, String type) {
        int lowerBound = 0;
        int elementSize = 2;

        // Formula para calcular la dir
        int address = lowerBound + (numValue - lowerBound) * elementSize;

        return assign(idLex, address, type);
    }

    // Method to handle D -> T id [ num ] ;
    public void handleDWithArrayD(String type, String idLex, int numValue, String numType) {
        if ("integer".equals(numType)) {
            // Call the method to calculate the array address
            String address = calculateArrayAddress(idLex, numValue, type);
            System.out.println("D.address = " + address);
        } else {
            System.out.println("Error: num.type must be 'integer'. Found: " + numType);
        }
    }

}
