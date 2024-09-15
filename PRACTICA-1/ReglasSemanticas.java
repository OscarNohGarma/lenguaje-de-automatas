import java.util.HashMap;
import java.util.Map;

public class ReglasSemanticas {

    // Aquí se debe almacenar la tabla de simbolos
    private Map<String, Symbol> symbolTable = new HashMap<>();

    // Sacamos los simbolos individualmente
    private class Symbol {
        String type;
        String value;
        int address;

        Symbol(String type, String value, int address) {
            this.type = type;
            this.value = value;
            this.address = address;
        }
    }

    // Metodo para agregar a la tabla de simbolos
    public void add(String lexeme, String type) {
        if (!symbolTable.containsKey(lexeme)) {
            int address = calculateAddress(type);
            symbolTable.put(lexeme, new Symbol(type, null, address));
        } else {
            System.out.println("Error: " + lexeme);
        }
    }

    // Asignamos la direccion del arreglo
    public void assign(String lexeme, String value, String type) {
        if (type.equals("entero")) {
            if (symbolTable.containsKey(lexeme)) {
                Symbol symbol = symbolTable.get(lexeme);
                if (symbol.type.equals(type)) {
                    symbol.value = value;
                } else {
                    System.out.println("Error: " + lexeme);
                }
            } else {
                System.err.println("Error: Identificador no declarado " + lexeme);
            }
        }

    }

    // Método para calcular el desplazamiento
    private int calculateOffset(String type, int index) {
        int size = 0;
        switch (type) {
            case "entero":
                size = 4;
                break;
            case "flotante":
                size = 8;
                break;
            default:
                System.out.println("Error: " + type);
                return -1;
        }
        return size * index; // Se calcula el desplazamientos
    }

    // Obtener dirección del arreglo
    public int getAddress(String lexeme, int index) {
        if (symbolTable.containsKey(lexeme)) {
            Symbol symbol = symbolTable.get(lexeme);
            int offset = calculateOffset(symbol.type, index);
            return symbol.address + offset;
        } else {
            System.out.println("Error: " + lexeme);
            return -1;
        }
    }

    // Calculamos la dirección baso en el tipo
    private int calculateAddress(String type) {
        if (type.equals("entero")) {
            return 2;
        } else if (type.equals("flotante")) {
            return 4;
        }
        return 0;
    }
}
