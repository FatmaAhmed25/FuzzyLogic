import java.util.ArrayList;

class Variable
{
    String name;
    String type;
    int upperBound;
    int lowerBound;
    double crispValue;
    ArrayList<FuzzySet> fuzzySets = new ArrayList<>();
}