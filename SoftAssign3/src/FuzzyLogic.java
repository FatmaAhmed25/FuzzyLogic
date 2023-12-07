import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

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
    public void AddVariables(String var)
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
    public  void AddFuzzySets(String varName,String set)
    {
        System.out.println(varName);
        int ind = -1;

        for (int i = 0; i < variables.size(); i++)
        {
            if (variables.get(i).name.equals(varName))
            {
                ind = i;
            }
        }

        String word = "";
        char c;
        int index = 0;
        FuzzySet fuzzy = new FuzzySet();

        for (int i = 0; i < set.length(); i++)
        {
            c = set.charAt(i);
            if (c == ' ' || c == '\t')
            {
                break;
            }

            word += c;
            index++;
        }

        fuzzy.name = word;
        word = "";

        while (set.charAt(index) == ' ' || set.charAt(index) == '\t')
        {
            index++;
        }

        for (int i = index; i < set.length(); i++)
        {
            c = set.charAt(i);
            if (c == ' ' || c == '\t')
            {
                break;
            }

            word += c;
            index++;
        }

        fuzzy.type = word;
        word = "";

        while (set.charAt(index) == ' ' || set.charAt(index) == '\t')
        {
            index++;
        }

        for (int i = index; i < set.length(); i++)
        {
            c = set.charAt(i);
            word += c;
            index++;
        }

        String[] points = word.split(" ");
        fuzzy.a = Integer.parseInt(points[0]);
        fuzzy.b = Integer.parseInt(points[1]);
        fuzzy.c = Integer.parseInt(points[2]);

        if (fuzzy.type.equals("TRAP"))
            fuzzy.d = Integer.parseInt(points[3]);

        variables.get(ind).fuzzy_sets.add(fuzzy);
        }


    public void AddRules(String query)
    {
        System.out.println(query);
        rules.add(query);
    }

    public void Fuzzification(Variable variable)
    {
        for (int i = 0; i < variable.fuzzy_sets.size(); i++)
        {

            Point p1 = new Point();
            Point p2 = new Point();

            if ((variable.crisp_value >= variable.fuzzy_sets.get(i).a) && (variable.crisp_value <= variable.fuzzy_sets.get(i).c))
            {
                if ((variable.crisp_value >= variable.fuzzy_sets.get(i).a) && (variable.crisp_value <= variable.fuzzy_sets.get(i).b))
                {
                    p1.x = variable.fuzzy_sets.get(i).a;
                    p1.y = 0;

                    p2.x = variable.fuzzy_sets.get(i).b;
                    p2.y = 1;
                }

                else
                {
                    if (variable.fuzzy_sets.get(i).type.equals("TRI"))
                    {
                        p1.x = variable.fuzzy_sets.get(i).b;
                        p1.y = 1;

                        p2.x = variable.fuzzy_sets.get(i).c;
                        p2.y = 0;
                    }

                    else
                    {
                        p1.x = variable.fuzzy_sets.get(i).b;
                        p1.y = 1;

                        p2.x = variable.fuzzy_sets.get(i).c;
                        p2.y = 1;
                    }
                }

                double m = (p2.y - p1.y)/(p2.x - p1.x);
                double c = p1.y - (m * p1.x);
                variable.fuzzy_sets.get(i).degree = (variable.crisp_value * m) + c;
            }

            else if ((variable.fuzzy_sets.get(i).type.equals("TRAP")) && (variable.crisp_value >= variable.fuzzy_sets.get(i).c) && (variable.crisp_value <= variable.fuzzy_sets.get(i).d))
            {
                p1.x = variable.fuzzy_sets.get(i).c;
                p1.y = 1;

                p2.x = variable.fuzzy_sets.get(i).d;
                p2.y = 0;

                double m = (p2.y - p1.y)/(p2.x - p1.x);
                double c = p1.y - (m * p1.x);
                variable.fuzzy_sets.get(i).degree = (variable.crisp_value * m) + c;
            }

            else
            {
                variable.fuzzy_sets.get(i).degree = 0;
            }
        }
    }

    public void Inference(String rule)
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
                for (int k = 0; k < variables.get(j).fuzzy_sets.size(); k++)
                {
                    if (variables.get(j).fuzzy_sets.get(k).name.equals(words[i+1]))
                    {
                        val1 = variables.get(j).fuzzy_sets.get(k).degree;
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
                for (int k = 0; k < variables.get(j).fuzzy_sets.size(); k++)
                {
                    if (variables.get(j).fuzzy_sets.get(k).name.equals(words[i+1]))
                    {
                        val2 = variables.get(j).fuzzy_sets.get(k).degree;
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
                for (int k = 0; k < variables.get(j).fuzzy_sets.size(); k++)
                {
                    if (variables.get(j).fuzzy_sets.get(k).name.equals(words[i+1]))
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

        if (variables.get(index).fuzzy_sets.get(ind).degree < val3)
            variables.get(index).fuzzy_sets.get(ind).degree = val3;

    }

    public void Defuzzification() throws IOException {
        double numerator = 0;
        double denominator = 0;

        for (int i = 0; i < variables.size(); i++)
        {
            if (variables.get(i).type.equals("OUT"))
            {
                for (int j = 0; j < variables.get(i).fuzzy_sets.size(); j++)
                {
                    double centroid;
                    if (variables.get(i).fuzzy_sets.get(j).type.equals("TRI"))
                        centroid = (variables.get(i).fuzzy_sets.get(j).a + variables.get(i).fuzzy_sets.get(j).b + variables.get(i).fuzzy_sets.get(j).c)/3.0;
                    else
                        centroid = (variables.get(i).fuzzy_sets.get(j).b + variables.get(i).fuzzy_sets.get(j).c)/2.0;
                    numerator += (variables.get(i).fuzzy_sets.get(j).degree) * centroid;
                    denominator += variables.get(i).fuzzy_sets.get(j).degree;
                }
                variables.get(i).crisp_value = numerator / denominator;
                variables.get(i).crisp_value = Math.round(100 * variables.get(i).crisp_value) / 100.0;
                Fuzzification(variables.get(i));
                double max = -1;
                int index = -1;

                for (int j = 0; j < variables.get(i).fuzzy_sets.size(); j++) {
                    if (variables.get(i).fuzzy_sets.get(j).degree >= max) {
                        max = variables.get(i).fuzzy_sets.get(j).degree;
                        index = j;
                    }
                }
                System.out.println("The predicted " + variables.get(i).name + " is " + variables.get(i).fuzzy_sets.get(index).name +  " (" + variables.get(i).crisp_value + ")");
            }
        }
    }


}