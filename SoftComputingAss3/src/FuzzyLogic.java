import java.io.IOException;
import java.util.ArrayList;

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
    public void addVariables(String var)
    {
        System.out.println(var);
        //var IN [0,100]
        String current = "";
        char c;
        int currentIndex = 0; //counter 3shan n3raf ehna fe anye index fel string dlw2te
        Variable v = new Variable();

        for (int i = 0; i < var.length(); i++) //nakhod awl string "var"
        {
            c = var.charAt(i);
            if (c == ' ' || c == '\t') //stop at space aw new line
            {
                break;
            }

            current += c;
            currentIndex++;
        }

        v.name = current; //nset elname bta3 el variable
        current = "";

        while (var.charAt(currentIndex) == ' ' || var.charAt(currentIndex) == '\t') //ignore spaces/newlines
        {
            currentIndex++;
        }

        for (int i = currentIndex; i < var.length(); i++) //nakhod tany string "IN" ->type
        {
            c = var.charAt(i);
            if (c == ' ' || c == '\t')
            {
                break;
            }

            current += c;
            currentIndex++;
        }

        v.type = current; //nset eltype bta3 el variable
        current = "";

        while (var.charAt(currentIndex) == ' ' || var.charAt(currentIndex) == '\t')
        {
            currentIndex++;
        }

        for (int i = currentIndex; i < var.length(); i++) //gets the range [0, 100]
        {
            c = var.charAt(i);
            if (c == ' ' || c == '\t')
            {
                continue; //hyakhod le had el akher
            }

            current += c;
            currentIndex++;
        }

        String[] range = current.split(",");  //ysplit at ","
        String lower = range[0].substring(1); //yakhod men b3d el [
        String upper = range[1].substring(0, range[1].length() - 1); //yakhod men el awl lehad ]

        v.lowerBound = Integer.parseInt(lower);
        v.upperBound = Integer.parseInt(upper);

        variables.add(v);
    }
    public  void addFuzzySets(String varName, String fuzzySet)
    {
        System.out.println(varName);
        int vIndex = -1; //if it remains -1 after the loop that means there is no variable with this name

        for (int i = 0; i < variables.size(); i++) //to get the variable with varName
        {
            if (variables.get(i).name.equals(varName))
            {
                vIndex = i;
            }
        }

        String current = "";
        char c;
        int currentIndex = 0;
        FuzzySet fuzzy = new FuzzySet();

        for (int i = 0; i < fuzzySet.length(); i++) //to get the fuzzy set name
        {
            c = fuzzySet.charAt(i);
            if (c == ' ' || c == '\t')
            {
                break;
            }

            current += c;
            currentIndex++;
        }

        fuzzy.name = current;
        current = "";

        while (fuzzySet.charAt(currentIndex) == ' ' || fuzzySet.charAt(currentIndex) == '\t') //skips spaces\newlines
        {
            currentIndex++;
        }

        for (int i = currentIndex; i < fuzzySet.length(); i++) //to get the type(TRI/TRAP)
        {
            c = fuzzySet.charAt(i);
            if (c == ' ' || c == '\t')
            {
                break;
            }

            current += c;
            currentIndex++;
        }

        fuzzy.type = current;
        current = "";

        while (fuzzySet.charAt(currentIndex) == ' ' || fuzzySet.charAt(currentIndex) == '\t')
        {
            currentIndex++;
        }

        for (int i = currentIndex; i < fuzzySet.length(); i++) //to get the values
        {
            c = fuzzySet.charAt(i);
            current += c;
            currentIndex++;
        }

        String[] points = current.split(" ");
        fuzzy.a = Integer.parseInt(points[0]);
        fuzzy.b = Integer.parseInt(points[1]);
        fuzzy.c = Integer.parseInt(points[2]);

        if (fuzzy.type.equals("TRAP")) //if type is TRAP then it will take a forth point
            fuzzy.d = Integer.parseInt(points[3]);

        variables.get(vIndex).fuzzySets.add(fuzzy);
    }


    public void addRules(String rule)
    {
        System.out.println(rule);
        rules.add(rule);
    }

    public void fuzzification(Variable variable)
    {
        for (int i = 0; i < variable.fuzzySets.size(); i++)
        {

            Point p1 = new Point();
            Point p2 = new Point();

            if ((variable.crisp_value >= variable.fuzzySets.get(i).a) && (variable.crisp_value <= variable.fuzzySets.get(i).c))
            {
                if ((variable.crisp_value >= variable.fuzzySets.get(i).a) && (variable.crisp_value <= variable.fuzzySets.get(i).b))
                {
                    p1.x = variable.fuzzySets.get(i).a;
                    p1.y = 0;

                    p2.x = variable.fuzzySets.get(i).b;
                    p2.y = 1;
                }

                else
                {
                    if (variable.fuzzySets.get(i).type.equals("TRI"))
                    {
                        p1.x = variable.fuzzySets.get(i).b;
                        p1.y = 1;

                        p2.x = variable.fuzzySets.get(i).c;
                        p2.y = 0;
                    }

                    else
                    {
                        p1.x = variable.fuzzySets.get(i).b;
                        p1.y = 1;

                        p2.x = variable.fuzzySets.get(i).c;
                        p2.y = 1;
                    }
                }

                double m = (p2.y - p1.y)/(p2.x - p1.x);
                double c = p1.y - (m * p1.x);
                variable.fuzzySets.get(i).degree = (variable.crisp_value * m) + c;
            }

            else if ((variable.fuzzySets.get(i).type.equals("TRAP")) && (variable.crisp_value >= variable.fuzzySets.get(i).c) && (variable.crisp_value <= variable.fuzzySets.get(i).d))
            {
                p1.x = variable.fuzzySets.get(i).c;
                p1.y = 1;

                p2.x = variable.fuzzySets.get(i).d;
                p2.y = 0;

                double m = (p2.y - p1.y)/(p2.x - p1.x);
                double c = p1.y - (m * p1.x);
                variable.fuzzySets.get(i).degree = (variable.crisp_value * m) + c;
            }

            else
            {
                variable.fuzzySets.get(i).degree = 0;
            }
        }
    }

    public void inference(String rule)
    {
        int index = -1;
        int ind = -1;
        boolean nott = false;
        int i = 0;
        double val1 = -1;
        double val2 = -1;
        String operator;
        double val3;

        String[] words = rule.split(" ");

        while (true)
        {
            if (!(words[i].equals("_not")))
                break;
            else
            {
                nott = !nott;
                i++;
            }
        }

        for (int j = 0; j < variables.size(); j++)
        {
            if (variables.get(j).name.equals(words[i]))
            {
                for (int k = 0; k < variables.get(j).fuzzySets.size(); k++)
                {
                    if (variables.get(j).fuzzySets.get(k).name.equals(words[i+1]))
                    {
                        val1 = variables.get(j).fuzzySets.get(k).degree;
                        if (nott)
                            val1 = 1 - val1;
                        break;
                    }
                }
            }
        }

        i += 2;
        nott = false;

        while (true)
        {
            if (!(words[i].equals("_not")))
                break;
            else
            {
                nott = !nott;
                i++;
            }
        }

        operator = words[i];

        if (!(words[i].equals("_not")) && (words[i].contains("_not")))
        {
            nott = !nott;
            operator = words[i].substring(0,words[i].length() - 4);
        }

        i++;

        for (int j = 0; j < variables.size(); j++)
        {
            if (variables.get(j).name.equals(words[i]))
            {
                for (int k = 0; k < variables.get(j).fuzzySets.size(); k++)
                {
                    if (variables.get(j).fuzzySets.get(k).name.equals(words[i+1]))
                    {
                        val2 = variables.get(j).fuzzySets.get(k).degree;
                        if (nott)
                            val2 = 1 - val2;
                        break;
                    }
                }
            }
        }

        i += 3;
        nott = false;

        for (int j = 0; j < variables.size(); j++)
        {
            if (variables.get(j).name.equals(words[i]))
            {
                index = j;
                for (int k = 0; k < variables.get(j).fuzzySets.size(); k++)
                {
                    if (variables.get(j).fuzzySets.get(k).name.equals(words[i+1]))
                    {
                        ind = k;
                    }
                }
            }
        }

        if (operator.equals("and"))
        {
            val3 = Math.min(val1,val2);
        }

        else
        {
            val3 = Math.max(val1,val2);
        }

        if (variables.get(index).fuzzySets.get(ind).degree < val3)
            variables.get(index).fuzzySets.get(ind).degree = val3;

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
                variables.get(i).crisp_value = numerator / denominator;
                variables.get(i).crisp_value = Math.round(100 * variables.get(i).crisp_value) / 100.0;
                fuzzification(variables.get(i));
                double max = -1;
                int index = -1;

                for (int j = 0; j < variables.get(i).fuzzySets.size(); j++) {
                    if (variables.get(i).fuzzySets.get(j).degree >= max) {
                        max = variables.get(i).fuzzySets.get(j).degree;
                        index = j;
                    }
                }
                System.out.println("The predicted " + variables.get(i).name + " is " + variables.get(i).fuzzySets.get(index).name +  " (" + variables.get(i).crisp_value + ")");
            }
        }
    }


}