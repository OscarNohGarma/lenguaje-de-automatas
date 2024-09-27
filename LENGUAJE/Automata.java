package lenguaje;

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
        transitionsFrom0.put('+', 1);
        transitionsFrom0.put('=', 2);
        // transitionsFrom0.put('*', 4);
        // transitionsFrom0.put('/', 5);
        // transitionsFrom0.put('-', 6);
        transitionsFrom0.put('a', 7);
        transitionsFrom0.put('o', 10);
        transitionsFrom0.put('!', 12);
        transitionsFrom0.put('s', 14);
        transitionsFrom0.put('i', 20);
        transitionsFrom0.put('f', 23);
        transitionsFrom0.put('b', 32);
        transitionsFrom0.put('e', 42);
        transitionsFrom0.put('r', 47);
        transitionsFrom0.put('p', 51);
        transitionsFrom0.put('g', 74);
        transitionsFrom0.put('t', 86);
        transitionsFrom0.put('(', 90);
        transitionsFrom0.put(')', 91);
        transitionsFrom0.put('{', 92);
        transitionsFrom0.put('}', 93);
        transitionsFrom0.put('L', 96);
        transitionsFrom0.put('D', 97);
        // transitionsFrom0.put('"', 98);

        transitions.put(0, transitionsFrom0);

        transitions.put(2, Map.of('=', 3));

        transitions.put(7, Map.of('n', 8, 'L', 96));
        transitions.put(8, Map.of('d', 9));
        transitions.put(9, Map.of('L', 96));
        transitions.put(10, Map.of('r', 11, 'L', 96));
        transitions.put(11, Map.of('L', 96));
        transitions.put(2, Map.of('=', 13)); // ==
        transitions.put(12, Map.of('=', 13));
        transitions.put(14, Map.of('t', 15, 'e', 62));
        transitions.put(15, Map.of('r', 16, 'L', 96));
        transitions.put(16, Map.of('i', 17));
        transitions.put(17, Map.of('n', 18));
        transitions.put(18, Map.of('g', 19));
        transitions.put(19, Map.of('L', 96));
        transitions.put(20, Map.of('n', 21, 'f', 46, 'L', 96));
        transitions.put(21, Map.of('t', 22));
        transitions.put(22, Map.of('L', 96));
        transitions.put(27, Map.of('L', 96));

        transitions.put(23, Map.of('a', 28, 'i', 39, 'u', 67, 'L', 96));
        transitions.put(28, Map.of('l', 29));
        transitions.put(29, Map.of('s', 30));
        transitions.put(30, Map.of('e', 31));
        transitions.put(31, Map.of('L', 96));

        transitions.put(32, Map.of('o', 33, 'L', 96));
        transitions.put(33, Map.of('o', 34));
        transitions.put(34, Map.of('l', 35));
        transitions.put(35, Map.of('e', 36));
        transitions.put(36, Map.of('a', 37));
        transitions.put(37, Map.of('n', 38));
        transitions.put(38, Map.of('L', 96));

        transitions.put(39, Map.of('l', 40));
        transitions.put(40, Map.of('e', 41));
        transitions.put(41, Map.of('L', 96));

        transitions.put(42, Map.of('l', 43, 'L', 96));
        transitions.put(43, Map.of('s', 44));
        transitions.put(44, Map.of('e', 45));
        transitions.put(45, Map.of('L', 96));

        transitions.put(47, Map.of('e', 48, 'L', 96));
        transitions.put(48, Map.of('a', 49, 't', 58));
        transitions.put(49, Map.of('d', 50));
        transitions.put(50, Map.of('L', 96));

        transitions.put(51, Map.of('r', 52, 'L', 96));
        transitions.put(52, Map.of('i', 53));
        transitions.put(53, Map.of('n', 54));
        transitions.put(54, Map.of('t', 55));
        transitions.put(55, Map.of('L', 96));
        transitions.put(57, Map.of('L', 96));

        transitions.put(58, Map.of('u', 59));
        transitions.put(59, Map.of('r', 60));
        transitions.put(60, Map.of('n', 61));
        transitions.put(61, Map.of('L', 96));

        transitions.put(62, Map.of('a', 63));
        transitions.put(63, Map.of('r', 64));
        transitions.put(64, Map.of('c', 65));
        transitions.put(65, Map.of('h', 66));
        transitions.put(66, Map.of('L', 96));
        transitions.put(67, Map.of('n', 68));
        transitions.put(68, Map.of('c', 69));
        transitions.put(69, Map.of('t', 70));
        transitions.put(70, Map.of('i', 71));
        transitions.put(71, Map.of('o', 72));
        transitions.put(72, Map.of('n', 73));
        transitions.put(73, Map.of('L', 96));

        transitions.put(74, Map.of('e', 75, 'L', 96));
        transitions.put(75, Map.of('n', 76));
        transitions.put(76, Map.of('e', 77));
        transitions.put(77, Map.of('r', 78));
        transitions.put(78, Map.of('a', 79));
        transitions.put(79, Map.of('t', 80));
        transitions.put(80, Map.of('e', 81));
        transitions.put(81, Map.of('F', 82));
        transitions.put(82, Map.of('i', 83));
        transitions.put(83, Map.of('l', 84));
        transitions.put(84, Map.of('e', 85));
        transitions.put(85, Map.of('L', 96));

        transitions.put(86, Map.of('r', 87, 'L', 96));
        transitions.put(87, Map.of('u', 88));
        transitions.put(88, Map.of('e', 89));
        transitions.put(89, Map.of('L', 96));

        transitions.put(96, Map.of('L', 96, 'D', 96));
        transitions.put(97, Map.of('D', 97));
        transitions.put(98, Map.of('l', 99));

        Set<Integer> acceptingStates = Set.of(1, 2, 3, 4, 5, 9, 11, 12, 13, 19, 22, 27, 31, 38, 41, 45, 46, 50, 55,
                61, 66, 73, 85, 89, 90, 91, 92, 93, 94, 95, 96, 97);

        // Autómata
        DFAChecker dfaChecker = new DFAChecker(transitions, acceptingStates, 0);

        return dfaChecker.checkAutomata(input);
    }

}
