import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FuzzyLogic {

    String system_name;
    String system_description;
    ArrayList<Variable> variables = new ArrayList<>();
    ArrayList<String> rules = new ArrayList<>();

    public FuzzyLogic(ArrayList<String> rules,ArrayList<Variable> variables,String system_name,String system_description)
    {
        this.rules=rules;
        this.system_name=system_name;
        this.system_description=system_description;
        this.variables=variables;
    }
    public void addVariables(String var) {
        Variable v = new Variable();

        // pattern like  (proj_funding IN [0, 100])
        Pattern pattern = Pattern.compile("^(\\S+)\\s+(IN|OUT)\\s+\\[(\\d+),\\s*(\\d+)\\]$");

        Matcher matcher = pattern.matcher(var);

        if (matcher.matches()) {
            v.name = matcher.group(1);
            v.type = matcher.group(2);
            v.lowerBound = Integer.parseInt(matcher.group(3));
            v.upperBound = Integer.parseInt(matcher.group(4));

            variables.add(v);
        } else {
            System.out.println("Invalid input format: " + var);
        }
    }
    public void addFuzzySets(String varName, String fuzzySet) {
        Variable variable = variables.stream()
                .filter(v -> v.name.equals(varName))
                .findFirst()
                .orElse(null);

        if (variable != null) {
            String[] parts = fuzzySet.split("\\s+");

            if (parts.length >= 4) {
                FuzzySet fuzzy = new FuzzySet();

                fuzzy.name = parts[0];
                fuzzy.type = parts[1];
                fuzzy.a = Integer.parseInt(parts[2]);
                fuzzy.b = Integer.parseInt(parts[3]);
                fuzzy.c = Integer.parseInt(parts[4]);

                if (fuzzy.type.equals("TRAP") && parts.length == 6) {
                    fuzzy.d = Integer.parseInt(parts[5]);
                }

                variable.fuzzySets.add(fuzzy);
            } else {
                System.out.println("Invalid fuzzy set format: " + fuzzySet);
            }
        } else {
            System.out.println("Variable not found: " + varName);
        }
    }

    public void addRules(String rule)
    {
        rules.add(rule);
    }

    public void fuzzification(Variable variable) {
        for (FuzzySet fuzzySet : variable.fuzzySets)
        {
            double crispValue = variable.crispValue;
            double a = fuzzySet.a;
            double b = fuzzySet.b;
            double c = fuzzySet.c;
            double d = fuzzySet.d;

            if (crispValue >= a && crispValue <= c)
            {
                if (crispValue >= a && crispValue <= b)
                {
                    setLinearPoints(fuzzySet, crispValue, a, 0, b, 1);
                }
                else // between b and c
                {
                    if ("TRI".equals(fuzzySet.type))
                    {
                        setLinearPoints(fuzzySet, crispValue, b, 1, c, 0);
                    }
                    else
                    {   // ---- <- intersect line shklo kda fl trapezoid
                        setLinearPoints(fuzzySet, crispValue, b, 1, c, 1);
                    }
                }
            }
            else if ("TRAP".equals(fuzzySet.type) && crispValue >= c && crispValue <= d)
            {
                // intersect line shklo kda fl trap \
                setLinearPoints(fuzzySet, crispValue, c, 1, d, 0);
            }
            else
            {
                fuzzySet.degree = 0;
            }
        }
    }

    private void setLinearPoints(FuzzySet fuzzySet, double crispValue, double x1, double y1, double x2, double y2) {
        double m = (y2 - y1) / (x2 - x1);
        double c = y1 - (m * x1);
        fuzzySet.degree = (crispValue * m) + c;
    }


    public void inference(String rule) {
        int variableIndex = -1;
        int fuzzySetIndex = -1;
        int i = 0;
        double firstValue = -1;
        double secondValue = -1;
        String operator;
        double resultValue;

        String[] words = rule.split(" ");

        boolean negationFlag = checkNegation(words, i);
        i += negationFlag ? 1 : 0;

        firstValue = getValue(words, i, negationFlag);
        i += 2;

        negationFlag = checkNegation(words, i);
        i += negationFlag ? 1 : 0;

        operator = words[i];
        if (operator.contains("_not")) {
            negationFlag = !negationFlag;
            operator = operator.substring(0, operator.length() - 4);
        }
        i++;

        secondValue = getValue(words, i, negationFlag);
        i += 3;

        negationFlag = false;

        for (int j = 0; j < variables.size(); j++) {
            if (variables.get(j).name.equals(words[i])) {
                variableIndex = j;
                fuzzySetIndex = getFuzzySetIndex(variables.get(j), words[i+1]);
            }
        }

        resultValue = operator.equals("and") ? Math.min(firstValue, secondValue) : Math.max(firstValue, secondValue);

        if (variables.get(variableIndex).fuzzySets.get(fuzzySetIndex).degree < resultValue)
            variables.get(variableIndex).fuzzySets.get(fuzzySetIndex).degree = resultValue;
    }

    private boolean checkNegation(String[] words, int index) {
        return words[index].equals("_not");
    }

    private double getValue(String[] words, int index, boolean negationFlag) {
        double value = -1;
        for (int j = 0; j < variables.size(); j++) {
            if (variables.get(j).name.equals(words[index])) {
                value = getDegree(variables.get(j), words[index+1], negationFlag);
            }
        }
        return value;
    }

    private double getDegree(Variable variable, String fuzzySetName, boolean negationFlag) {
        double degree = -1;
        for (FuzzySet fuzzySet : variable.fuzzySets) {
            if (fuzzySet.name.equals(fuzzySetName)) {
                degree = fuzzySet.degree;
                if (negationFlag)
                    degree = 1 - degree;
                break;
            }
        }
        return degree;
    }

    private int getFuzzySetIndex(Variable variable, String fuzzySetName) {
        int index = -1;
        for (int k = 0; k < variable.fuzzySets.size(); k++) {
            if (variable.fuzzySets.get(k).name.equals(fuzzySetName)) {
                index = k;
            }
        }
        return index;
    }


    public void defuzzification() throws IOException {
        double numerator = 0;
        double denominator = 0;

        for (int i = 0; i < variables.size(); i++)
        {
            if (variables.get(i).type.equals("OUT"))
            {
                for (int j = 0; j < variables.get(i).fuzzySets.size(); j++)
                {
                    double centroid;
                    if (variables.get(i).fuzzySets.get(j).type.equals("TRI"))
                        centroid = (variables.get(i).fuzzySets.get(j).a + variables.get(i).fuzzySets.get(j).b + variables.get(i).fuzzySets.get(j).c)/3.0;
                    else
                        centroid = (variables.get(i).fuzzySets.get(j).b + variables.get(i).fuzzySets.get(j).c)/2.0;
                    numerator += (variables.get(i).fuzzySets.get(j).degree) * centroid;
                    denominator += variables.get(i).fuzzySets.get(j).degree;
                }
                variables.get(i).crispValue = numerator / denominator;
                variables.get(i).crispValue = Math.round(100 * variables.get(i).crispValue) / 100.0;
                fuzzification(variables.get(i));
                double max = -1;
                int index = -1;

                for (int j = 0; j < variables.get(i).fuzzySets.size(); j++) {
                    if (variables.get(i).fuzzySets.get(j).degree >= max) {
                        max = variables.get(i).fuzzySets.get(j).degree;
                        index = j;
                    }
                }
                System.out.println("The predicted " + variables.get(i).name + " is " + variables.get(i).fuzzySets.get(index).name +  " (" + variables.get(i).crispValue + ")");
            }
        }
    }
}