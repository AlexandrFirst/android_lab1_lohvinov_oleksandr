package oleksandr.lohvinov.lab1;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

public class MathModule {

    private static final List<String> parenthesis = Arrays.asList("(", ")");

    private static final List<String> oneCharActions = Arrays.asList("+", "-", "/", "*", "√", "^", "~");

    private static final List<String> multiCharActions = Arrays.asList("sin", "cos");

    private OnWrongInput wrongInputEvent = null;

    public void SetOnWrongInput(OnWrongInput wrongInputEvent) {
        this.wrongInputEvent = wrongInputEvent;
    }

    public PolishNotationTokenizer parseString(String s) {
        return PolishNotationTokenizer.TokenizeInput(s, wrongInputEvent);
    }

    public double CalculatePolishNotation(List<String> calculatingTokens) {
        PolishNotationCalculator calculator =
                PolishNotationCalculator.InitPolishNotationCalculator(calculatingTokens, wrongInputEvent);
        return calculator.CalculatePolishNotation();
    }

    public static class PolishNotationTokenizer {

        private OnWrongInput wrongInputEvent;

        private char[] analyzedString;
        private List<String> tokens;


        private Dictionary<Integer, String> multiCharActionPositions = new Hashtable<>();

        private String currentToken = "";

        private PolishNotationTokenizer(String analyzedString, OnWrongInput wrongInput) {
            this.wrongInputEvent = wrongInput;
            tokens = new ArrayList<>();
            this.analyzedString = analyzedString.trim().toCharArray();

            for (int i = 0; i < multiCharActions.size(); i++) {
                int from = 0;
                int foundIndex = -1;
                while ((foundIndex = analyzedString.indexOf(multiCharActions.get(i), from)) != -1) {
                    multiCharActionPositions.put(foundIndex, multiCharActions.get(i));
                    from = foundIndex + 1;
                }
                if (foundIndex == -1) {
                    continue;
                }
            }

            for (int i = 0; i < this.analyzedString.length; i++) {
                String action = "";
                if ((action = multiCharActionPositions.get(i)) != null) {
                    currentToken = action;
                    tokens.add(currentToken);
                    i += (currentToken.length() - 1);
                    currentToken = "";
                } else if (oneCharActions.contains(String.valueOf(this.analyzedString[i])) ||
                        parenthesis.contains(String.valueOf(this.analyzedString[i]))) {
                    String analyzingToken = String.valueOf(this.analyzedString[i]);

                    if(analyzingToken.equals("-")) {
                        if (i - 1 < 0 || String.valueOf(this.analyzedString[i-1]).equals("(") ||
                                (oneCharActions.contains(String.valueOf(this.analyzedString[i-1])) &&
                                        (!String.valueOf(this.analyzedString[i-1]).equals("-")))) {
                            analyzingToken = "~";
                        }
                    }
                    if (!currentToken.isEmpty()) {
                        tokens.add(currentToken);
                    }
                    tokens.add(analyzingToken);
                    currentToken = "";
                } else {
                    currentToken = currentToken.concat(String.valueOf(this.analyzedString[i]));
                }
            }
            if(!currentToken.equals("")) {
                tokens.add(currentToken);
            }
        }

        public List<String> GetTokenizedString() {
            return tokens;
        }

        public static PolishNotationTokenizer TokenizeInput(String analyzedString, OnWrongInput wrongInput) {
            return new PolishNotationTokenizer(analyzedString, wrongInput);
        }
    }

    private static class PolishNotationCalculator {

        private OnWrongInput wrongInputEvent;

        private List<String> tokenizedString;
        private Stack<String> reversePolishNotation;

        private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

        private PolishNotationCalculator(List<String> tokenizedString, OnWrongInput wrongInputEvent) {
            this.wrongInputEvent = wrongInputEvent;
            this.tokenizedString = tokenizedString;
            reversePolishNotation = new Stack<>();
        }

        public static PolishNotationCalculator InitPolishNotationCalculator(List<String> tokenizedString, OnWrongInput wrongInputEvent) {
            return new PolishNotationCalculator(tokenizedString, wrongInputEvent);
        }

        public double CalculatePolishNotation() {
            List<String> tokens = ConvertInfixToPostfixNotation();
            Stack<Double> calculationStack = new Stack<>();

            for (int i = 0; i < tokens.size(); i++) {
                double result = 0.0d;

                try {
                    if (isNumeric(tokens.get(i))) {
                        Double num = Double.parseDouble(tokens.get(i));
                        calculationStack.push(num);
                    } else {
                        String operation = tokens.get(i);
                        double a, b;
                        switch (operation) {
                            case "+":
                                a = calculationStack.pop();
                                b = calculationStack.pop();
                                result = a + b;
                                break;
                            case "-":
                                a = calculationStack.pop();
                                b = calculationStack.pop();
                                result = b - a;
                                break;
                            case "/":
                                a = calculationStack.pop();
                                b = calculationStack.pop();
                                if(a == 0) {
                                    wrongInputEvent.setError("Error - division on zero");
                                    throw new IllegalArgumentException();
                                }
                                result = b / a;
                                break;
                            case "*":
                                a = calculationStack.pop();
                                b = calculationStack.pop();
                                result = a * b;
                                break;
                            case "^":
                                a = calculationStack.pop();
                                b = calculationStack.pop();
                                result = Math.pow(b, a);
                                break;
                            case "√":
                                a = calculationStack.pop();
                                if(a < 0) {
                                    wrongInputEvent.setError("Error - sqrt of negative num");
                                    throw new IllegalArgumentException();
                                }
                                result = Math.sqrt(a);
                                break;
                            case "~":
                                a = calculationStack.pop();
                                result = -a;
                                break;
                            case "cos":
                                a = calculationStack.pop();
                                result = Math.cos(a);
                                break;
                            case "sin":
                                a = calculationStack.pop();
                                result = Math.sin(a);
                                break;

                        }
                        calculationStack.push(result);
                    }
                } catch (Exception e){
                    if(wrongInputEvent!=null) {
                        wrongInputEvent.setError("Error - Wrong input");
                    }
                    throw new IllegalArgumentException();
                }

            }

            if(!calculationStack.empty())
                return calculationStack.pop();
            throw new IllegalArgumentException();
        }

        private List<String> ConvertInfixToPostfixNotation() {

            List<String> polishNotationString = new ArrayList<>();

            for (int i = 0; i < tokenizedString.size(); i++) {
                String token = tokenizedString.get(i);
                if (oneCharActions.contains(token)) {
                    int currentOperationRank = getOperationRank(token);

                    if (!reversePolishNotation.empty()) {
                        String topStackOperation = reversePolishNotation.peek();
                        while (!reversePolishNotation.empty() && getOperationRank(topStackOperation) >= currentOperationRank) {
                            String currentToke = reversePolishNotation.pop();
                            polishNotationString.add(currentToke);
                        }
                    }
                    reversePolishNotation.push(token);

                } else if (multiCharActions.contains(token) || token.equals("(")) {
                    reversePolishNotation.push(token);
                } else if (String.valueOf(token).equals(")")) {
                    while (!(String.valueOf(reversePolishNotation.peek()).equals("("))) {
                        String currentToke = reversePolishNotation.pop();
                        polishNotationString.add(currentToke);
                        if(reversePolishNotation.empty()){
                            if(wrongInputEvent!=null) {
                                wrongInputEvent.setError("Error - unbalanced parenthesis");
                            }
                            return new ArrayList<>();
                        }
                    }
                    reversePolishNotation.pop();
                    if (!reversePolishNotation.empty() && multiCharActions.contains(reversePolishNotation.peek())) {
                        String currentToke = reversePolishNotation.pop();
                        polishNotationString.add(currentToke);
                    }

                } else if (!token.equals("")) {
                    polishNotationString.add(token);
                }
            }

            while (!reversePolishNotation.empty()) {
                polishNotationString.add(reversePolishNotation.pop());
            }

            return polishNotationString;
        }

        private int getOperationRank(String operation) {
            switch (operation) {
                case "√":
                case "~":
                    return 5;
                case "^":
                    return 4;
                case "*":
                case "/":
                    return 3;
                case "+":
                case "-":
                    return 2;
                case "cos":
                case "sin":
                case "(":
                    return 1;
                default:
                    return -1;
            }
        }


        public boolean isNumeric(String strNum) {
            if (strNum == null) {
                return false;
            }
            return pattern.matcher(strNum).matches();
        }
    }

    public interface OnWrongInput {
        void setError(String message);
    }
}
