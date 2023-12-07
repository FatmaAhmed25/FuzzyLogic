import java.util.ArrayList;

class Variable
{
    String name;
    String type;
    int upperBound;
    int lowerBound;
    double crisp_value;
    ArrayList<FuzzySet> fuzzy_sets = new ArrayList<>();
}