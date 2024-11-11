package lenguaje;

import java.util.ArrayList;

public class GenCodIntermedio {

    public ArrayList<String> temporalesStr;

    public GenCodIntermedio() {
        temporalesStr = new ArrayList<>();
    }

    public String newTemporal() {
        temporalesStr.add("t" + (temporalesStr.size() + 1));
        return "t" + (temporalesStr.size());
    }
}
