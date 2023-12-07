import java.util.ArrayList;

public class Variable {
    public String name;
    public String type;
    public int upperBound;
    public int lowerBound;
    public double crisp_value;
    public ArrayList<FuzzySet> fuzzy_sets = new ArrayList<>();

}
