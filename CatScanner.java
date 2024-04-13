import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CatScanner {

    private ArrayList<String> tokens;
    private String codigo;

    public CatScanner(String codigo) {
        this.codigo = codigo;
        tokens = new ArrayList<>();
    }

    public void separar() {
        Pattern pattern = Pattern.compile("\\s+|\\{|\\}|\\[|\\]|\\(|\\)|;|:|,|\"|_|\\+|-|\\*|/|\\\\|>|<|=|`|'");
        Matcher matcher = pattern.matcher(codigo);

        int inicio = 0;
        while (matcher.find()) {
            int fin = matcher.start();
            if (fin > inicio) {
                String token = codigo.substring(inicio, fin);
                tokens.add(token);
            }

            // Agregar el símbolo encontrado como token
            String symbol = matcher.group();
            if (!symbol.trim().isEmpty()) { // Ignorar espacios en blanco
                tokens.add(symbol);
            }

            inicio = matcher.end();
        }

        // Agregar el último token si hay caracteres restantes
        if (inicio < codigo.length()) {
            String token = codigo.substring(inicio);
            tokens.add(token);
        }
    }

    public static void main(String[] args) {
        String codigo = "hola este es\nun   \n ejemplo { de cadena } 2  '   [ ' ]      ()()() \" \"  +*+*= <>     <>/\n;: !  de caracteres";
        CatScanner cs = new CatScanner(codigo);
        cs.separar();

        // Imprimir los tokens sin saltos de línea al final
        for (String token : cs.tokens) {
            System.out.println(token);
        }
    }
}
