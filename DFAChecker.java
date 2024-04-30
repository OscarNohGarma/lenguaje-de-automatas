import java.util.Map;
import java.util.Set;

public class DFAChecker {
    private Map<Integer, Map<Character, Integer>> transitions;
    private Set<Integer> finalStates;
    private int initialState;

    public DFAChecker(Map<Integer, Map<Character, Integer>> transitions, Set<Integer> finalStates, int initialState) {
        this.transitions = transitions;
        this.finalStates = finalStates;
        this.initialState = initialState;
    }

    public boolean checkString(String input) {
        int currentState = initialState;

        for (char symbol : input.toCharArray()) {
            if (!transitions.get(currentState).containsKey(symbol) && !transitions.get(currentState).containsKey('L')
                    && !transitions.get(currentState).containsKey('D')) {
                return false;
            }

            if (transitions.get(currentState).containsKey(symbol)) {
                currentState = transitions.get(currentState).get(symbol);
            } else if (transitions.get(currentState).containsKey('L') && Character.isLetter(symbol)) {
                currentState = transitions.get(currentState).get('L');
            } else if (transitions.get(currentState).containsKey('D') && Character.isDigit(symbol)) {
                currentState = transitions.get(currentState).get('D');
            } else {
                return false;
            }
        }

        return finalStates.contains(currentState);
    }

    public int checkAutomata(String input) {
        int currentState = initialState;

        for (char symbol : input.toCharArray()) {
            if (!transitions.get(currentState).containsKey(symbol) && !transitions.get(currentState).containsKey('L')
                    && !transitions.get(currentState).containsKey('D')) {
                return -1;
            }

            if (transitions.get(currentState).containsKey(symbol)) {
                currentState = transitions.get(currentState).get(symbol);
            } else if (transitions.get(currentState).containsKey('L') && Character.isLetter(symbol)) {
                currentState = transitions.get(currentState).get('L');
            } else if (transitions.get(currentState).containsKey('D') && Character.isDigit(symbol)) {
                currentState = transitions.get(currentState).get('D');
            } else {
                return -1;
            }
        }

        if (finalStates.contains(currentState)) {
            int finalState = 0;
            // Operadores aritméticos
            if (currentState == 1 || currentState == 2 || currentState == 3 || currentState == 4
                    || currentState == 5 || currentState == 6) {
                finalState = 1;

                // Operadores lógicos
            } else if (currentState == 9 || currentState == 11 || currentState == 12 || currentState == 13) {
                finalState = 2;

                // Tipos de datos
            } else if (currentState == 19 || currentState == 22 || currentState == 27 || currentState == 38
                    || currentState == 41) {
                finalState = 3;

                // Palabras reservadas
            } else if (currentState == 31 || currentState == 45 || currentState == 46 || currentState == 50
                    || currentState == 55 || currentState == 57 || currentState == 61 || currentState == 73
                    || currentState == 66 || currentState == 85 || currentState == 89) {
                finalState = 4;

                // Signos de agrupación
            } else if (currentState == 90 || currentState == 91 || currentState == 92 || currentState == 93) {
                finalState = 5;

                // Variables
            } else if (currentState == 96) {
                finalState = 6;

                // Números
            } else if (currentState == 97) {
                finalState = 7;
            }
            // else if (currentState == 98) {
            // // 98 es "
            // finalState = 8;
            // }

            return finalState;
        } else {
            return -1;
        }

    }
}