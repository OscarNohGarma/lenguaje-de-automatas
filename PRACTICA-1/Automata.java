import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Automata {
    private String input;

    public Automata(String input) {
        this.input = input;
    }

    public int checkAutomata() {
        Map<Integer, Map<Character, Integer>> transitions = new HashMap<>();

        // Transiciones desde q0
        Map<Character, Integer> transitionsFrom0 = new HashMap<>();
        transitionsFrom0.put('+', 1); // +
        transitionsFrom0.put('-', 2); // -
        transitionsFrom0.put('*', 3); // *
        transitionsFrom0.put('/', 4); // /
        transitionsFrom0.put('=', 5); // =
        transitionsFrom0.put('(', 6); // (
        transitionsFrom0.put(')', 7); // )
        transitionsFrom0.put('[', 8); // [
        transitionsFrom0.put(']', 9); // ]
        transitionsFrom0.put('e', 10); // entero se va al 10
        transitionsFrom0.put('f', 11); // flotante se va al 11
        transitionsFrom0.put('p', 12); // print se va al 12
        transitionsFrom0.put('L', 13); // id
        transitionsFrom0.put('D', 14); // num
        transitions.put(0, transitionsFrom0); // Se agrega las transiciones anteriores a transitions

        // Transiciones para palabras reservadas
        // Formamos entero
        transitions.put(10, Map.of('n', 15));
        transitions.put(15, Map.of('t', 16));
        transitions.put(16, Map.of('e', 17));
        transitions.put(17, Map.of('r', 18));
        transitions.put(18, Map.of('o', 19));

        // Formamos flotante
        transitions.put(11, Map.of('l', 20));
        transitions.put(20, Map.of('o', 21));
        transitions.put(21, Map.of('t', 22));
        transitions.put(22, Map.of('a', 23));
        transitions.put(23, Map.of('n', 24));
        transitions.put(24, Map.of('t', 25));
        transitions.put(25, Map.of('e', 26));
        transitions.put(26, Map.of('L', 13));

        // Formamos print
        transitions.put(12, Map.of('r', 26));
        transitions.put(26, Map.of('i', 27));
        transitions.put(27, Map.of('n', 28));
        transitions.put(28, Map.of('t', 29));
        transitions.put(29, Map.of('L', 13));

        // Estados de aceptación
        Set<Integer> acceptingStates = Set.of(1, 2, 3, 4, 5, 6, 7, 8, 13, 9, 19, 26, 29, 13, 14);

        // Autómata
        DFAChecker dfaChecker = new DFAChecker(transitions, acceptingStates, 0);

        return dfaChecker.checkAutomata(input);
    }

}
