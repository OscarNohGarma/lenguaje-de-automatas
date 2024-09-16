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
            if (!transitions.get(currentState).containsKey(symbol)
                    && !transitions.get(currentState).containsKey('L')
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
            // System.out.println(symbol);
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

            // Concatenación +
            if (currentState == 1) {
                finalState = 1;
            }
            // Tipos de datos: entero, flotante
            else if (currentState == 19) {
                finalState = 2;
            }

            else if (currentState == 26) {
                finalState = 3;

                // Palabras reservadas: print
            } else if (currentState == 27) {
                finalState = 4;

            }

            // Variables
            else if (currentState == 13) {
                finalState = 5;

                // Números
            } else if (currentState == 14 || currentState == 31) {
                finalState = 6;

            }
            // Igualación =
            else if (currentState == 5) {
                finalState = 7;
            }

            // Signo de agrupación (
            else if (currentState == 6) {
                finalState = 8;
            }

            // Signo de agrupación )
            else if (currentState == 7) {
                finalState = 9;
            }

            // Signo de agrupación [
            else if (currentState == 8) {
                finalState = 10;
            }

            // Signo de agrupación ]
            else if (currentState == 9) {
                finalState = 11;
            }

            // -
            else if (currentState == 2) {
                finalState = 12;
            }

            // *
            else if (currentState == 3) {
                finalState = 13;
            }

            // /
            else if (currentState == 4) {
                finalState = 14;
            }

            return finalState;
        } else {
            return -1;
        }

    }
}