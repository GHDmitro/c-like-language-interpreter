/*
 * Konstantine Mushegian. 2014
 * Lexical Analyzer for a C-Like Language
 */
package kmushegi_lexer;

/**
 * ********************************************************************
 * This program analyzes a source file written in a C-like language, parses it
 * and does lexical and syntactic analysis. Now the type system has been
 * implemented. Next step will be implementing a syntactic analyzer, this is a
 * work in progress.
 */
import java.io.*; //necessary imports
import java.util.*;

/**
 *
 * @author Konstantine
 */
public class Kmushegi_Lexer {

    public static List<String> tokens = new ArrayList<String>();
    public static List<String> lexemes = new ArrayList<String>();

    /**
     *
     */
    public static Map<String, idDataHolder> symbolTable = new HashMap<>();
    /**
     *
     */
    static int nextTokenIndex = 0;

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        parser(tokenize(readFile(getFileName()))); //asks user for file name
        //reads the file
        //tokenizes it and identifies tokens
    }

    public static String getFileName() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter File Name:");
        String input = sc.next(); //get user input
        System.out.println("**********************");
        return input;
    }

    //code for this method has been adapted from materials found on StackOverflow
    /**
     *
     * @param fileName
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String readFile(String fileName) throws FileNotFoundException, IOException {
        String fileToRead = "./files/" + fileName; //append filename to location
        //method that reads the text file line by line
        BufferedReader reader = new BufferedReader(new FileReader(fileToRead));
        String curLine = null; //current line string
        StringBuilder strBuilder = new StringBuilder();

        while ((curLine = reader.readLine()) != null) { //while there are more lines
            strBuilder.append(curLine); //append lines to StringBuilder
        }
        String input = strBuilder.toString(); //converting strBuilder to string 
        return input; //and return
    }

    public static List<String> tokenize(String inputFile) throws IOException {
        List<String> fullList = new ArrayList<String>();
        /*string tokenizer with specified delimiters.last peremeter is set to
         true so that delimiters are also taken as tokens
         */
        StringTokenizer st = new StringTokenizer(inputFile, "\\;\\{\\}\\=\\(\\)\\<\\>\\<=\\>=\\==\\+\\-\\*\\/\\,\\\t\\|\\&\\ ", true);
        while (st.hasMoreTokens()) { //while there are more tokens
            String lexeme = st.nextToken(); //get next token
            /* below there bunch of cases that check if
             tokens meet definitions
             */
            if (lexeme.equals("int") || lexeme.equals("char") || lexeme.equals("float") || lexeme.equals("bool")) {
                fullList.add("type");
                fullList.add(lexeme);
            } else if (lexeme.equals("main")) {
                fullList.add("main");
                fullList.add(lexeme);
            } else if (lexeme.equals("if")) {
                fullList.add("if");
                fullList.add(lexeme);
            } else if (lexeme.equals("while")) {
                fullList.add("while");
                fullList.add(lexeme);
            } else if (lexeme.equals("else")) {
                fullList.add("else");
                fullList.add(lexeme);
            } else if (lexeme.equals("=")) {
                fullList.add("assignOp");
                fullList.add(lexeme);
            } else if (lexeme.equals("true") || lexeme.equals("false")) {
                fullList.add("boolLiteral");
                fullList.add(lexeme);
            } else if (lexeme.equals("{")) {
                fullList.add("{");
                fullList.add(lexeme);
            } else if (lexeme.equals("}")) {
                fullList.add("}");
                fullList.add(lexeme);
            } else if (lexeme.equals("(")) {
                fullList.add("(");
                fullList.add(lexeme);
            } else if (lexeme.equals(")")) {
                fullList.add(")");
                fullList.add(lexeme);
            } else if (lexeme.equals("[")) {
                fullList.add("[");
                fullList.add(lexeme);
            } else if (lexeme.equals(",")) {
                fullList.add("comma");
                fullList.add(lexeme);
            } else if (lexeme.equals("]")) {
                fullList.add("]");
                fullList.add(lexeme);
            } else if (lexeme.equals("return")) {
                fullList.add("return");
                fullList.add(lexeme);
            } else if (lexeme.equals("print")) {
                fullList.add("print");
                fullList.add(lexeme);
            } else if (lexeme.equals("+") || lexeme.equals("-")) {
                fullList.add("addOp");
                fullList.add(lexeme);
            } else if (lexeme.equals("%") || lexeme.equals("*") || lexeme.equals("/")) {
                fullList.add("multOp");
                fullList.add(lexeme);
            } else if (lexeme.equals(";") || lexeme.equals(";")) {
                fullList.add(";");
                fullList.add(lexeme);
            } else if (lexeme.equals("==") || lexeme.equals("!=")) {
                fullList.add("equOp");
                fullList.add(lexeme);
            } else if (lexeme.equals(">") || lexeme.equals("<")
                    || lexeme.equals("<=") || lexeme.equals(">=")) {
                fullList.add("relOp");
                fullList.add(lexeme);
            } else if (Character.isAlphabetic(lexeme.charAt(0))) { //if first char is alphabetic
                if (lexeme.matches("[0-9A-Za-z]*")) {           //and following chars are alphanumeric then its id
                    fullList.add("id");
                    fullList.add(lexeme);
                }
            } else if (lexeme.matches("&") || lexeme.matches("|")) {
                String nextTok = st.nextToken();
                System.out.println(lexeme);
                System.out.println(nextTok);
                if (nextTok.equals(lexeme)) {
                    fullList.add("comparisonOp");
                    String fin = lexeme + nextTok;
                    System.out.println(fin);
                    fullList.add(fin);
                }
            } else if (lexeme.matches("[0-9]*")) {
                fullList.add("intLiteral");
                fullList.add(lexeme);
            } else if (lexeme.matches("[0-9]*[.][0-9]*")) { //integer followed by dot followede by integer = float
                fullList.add("floatLiteral");
                fullList.add(lexeme);
            } else if (lexeme.matches("['][A-Za-z][']")) { //if one letter surrounded by ' its a char
                fullList.add("charLiteral");
                fullList.add(lexeme);
            } else if (lexeme.charAt(0) == '/' && lexeme.charAt(1) == '/') {
                /* there is a limitation that comments need to be written
                 without spaces becasue if they are than separate comment words
                 are perceived as tokens. I tried using the while loop inside the 
                 else if statement so that if first two chars of a token are //
                 then I'd get next tokens and append them to the comment string
                 until i hit a token equal to \n ie new line but when testing
                 string tokenizer was not registering \n as a token. */
                //System.out.println("comment"+"\t\t"+lexeme);
                fullList.add("comment");
                fullList.add(lexeme);
            }
        }
        /*even though we will not need to read for a txt file, I am still writing
         tokens and lexemes to it just for observation purposes */
        writeToFile(fullList);
        return fullList;
    }

    //writes tokenizer output to the text file located in the output folder
    public static void writeToFile(List<String> tokenizerOutput) throws IOException {
        File oldFile = new File("./output/tokenizerOutput.txt"); //
        if (oldFile.exists()) { //delete the file if it already exists so we get 
            oldFile.delete(); //new output file every time
        }
        for (String tokenizerOutput1 : tokenizerOutput) {
            try (final BufferedWriter writer = new BufferedWriter(new FileWriter("./output/tokenizerOutput.txt", true))) {
                String line;
                line = tokenizerOutput1;
                writer.write(line);
                writer.newLine();
                writer.flush();
            }
        }
    }

    //separates the input into tokens and lexemes
    public static void parser(List<String> tokenizerOutput) {
        //for loops below separate tokenizer output into tokens and lexemes
        for (int i = 0; i < tokenizerOutput.size(); i += 2) { //items in even positions are tokens
            tokens.add(tokenizerOutput.get(i));
        }
        for (int i = 1; i < tokenizerOutput.size(); i += 2) { //in odd positions lexemes
            lexemes.add(tokenizerOutput.get(i));
        }
        checkSource(tokens, lexemes); //calling main checker function
    }

    /* if statements in the beginning are checking if the program starts correctly
     and then checks for declarations and other statements. in the end a check is made 
     to see if the program ends correctly (w/ }). if something is not satisfied 
     error method is called.
     */
    public static void checkSource(List<String> tokenStream, List<String> lexemeStream) {
        if (tokens.get(nextTokenIndex).equals("type")) {
            nextTokenIndex++;
            if (lexemes.get(nextTokenIndex).equals("main")) {
                nextTokenIndex++;
                if (lexemes.get(nextTokenIndex).equals("(")) {
                    nextTokenIndex++;
                    if (lexemes.get(nextTokenIndex).equals(")")) {
                        nextTokenIndex++;
                        if (lexemes.get(nextTokenIndex).equals("{")) {
                            nextTokenIndex++;
                            declarations(); //check declarations
                            statements(); //check other statements
                        } else {
                            error();
                        }
                        if (lexemes.get(nextTokenIndex).equals("}")) { //final check
                            System.out.println("Valid Expression!");
                            System.exit(1);
                        } else {
                            error();
                        }
                    } else {
                        error();
                    }
                } else {
                    error();
                }
            } else {
                error();
            }
        } else {
            error();
        }
    }

    public static void declarations() { //while token is type it means we're declaring so run declaration      
        while (nextTokenIndex < tokens.size() && tokens.get(nextTokenIndex).equals("type")) {
            if (symbolTable.containsKey(lexemes.get(nextTokenIndex + 1))) {
                System.out.println("No two variables can have the same name. Error @:" + lexemes.get(nextTokenIndex + 1));
                System.exit(1);
            } else {
                idDataHolder curHolder = new idDataHolder(lexemes.get(nextTokenIndex), null);
                symbolTable.put(lexemes.get(nextTokenIndex + 1), curHolder);
                declaration();
            }
        }
    }

    public static void declaration() { //check if declaration is correct, if anything is not satisfied error is called
        if (nextTokenIndex < tokens.size() && (tokens.get(nextTokenIndex)).equals("type")) {
            nextTokenIndex++;
            if (nextTokenIndex < tokens.size() && tokens.get(nextTokenIndex).equals("id")) {
                nextTokenIndex++;
                //if declaration ends with ; then it is correct 
                if (nextTokenIndex < tokens.size() && tokens.get(nextTokenIndex).equals(";")) {
                    nextTokenIndex++;
                } else {
                    error();
                }
            } else {
                error();
            }
        } else {
            error();
        }
    }

    public static void statements() { //check for statements
        while (nextTokenIndex < tokens.size()
                && (tokens.get(nextTokenIndex).equals("id")
                || tokens.get(nextTokenIndex).equals("print")
                || tokens.get(nextTokenIndex).equals("if")
                || tokens.get(nextTokenIndex).equals("while")
                || tokens.get(nextTokenIndex).equals("return"))) {
            statement(true);
        }
    }

    /*this method identifies what kind of statement we have and if it should be
     executed (ie printed out in the console) and calls appropriate method
     */
    public static void statement(boolean shouldExecute) {
        if (nextTokenIndex < tokens.size() && tokens.get(nextTokenIndex).equals("print")) {
            printStmt(shouldExecute);
        } else if (nextTokenIndex < tokens.size() && tokens.get(nextTokenIndex).equals("while")) {
            whileStmt(shouldExecute);
        } else if (nextTokenIndex < tokens.size() && tokens.get(nextTokenIndex).equals("if")) {
            ifStatement();
        } else if (nextTokenIndex < tokens.size() && tokens.get(nextTokenIndex).equals("return")) {
            returnStmt(shouldExecute);
        } else if (nextTokenIndex < tokens.size() && tokens.get(nextTokenIndex).equals("id")) {
            assignment(shouldExecute);
        }
    }

    // if(expression) statement [else statement] otherwise errors
    public static void ifStatement() {
        boolean ifIsTrue = false;
        if (nextTokenIndex < tokens.size() && "if".equals(tokens.get(nextTokenIndex))) {
            nextTokenIndex++; //checking syntax of if statement
            if (nextTokenIndex < tokens.size() && tokens.get(nextTokenIndex).equals("(")) {
                nextTokenIndex++;
                idDataHolder temp = expression(); //evaluate the expression inside ()
                if (nextTokenIndex < tokens.size() && lexemes.get(nextTokenIndex).equals(")")
                        && "true".equals(temp.value)) { //check if if stmt body should be executed
                    ifIsTrue = true;
                } else if (nextTokenIndex < tokens.size() && lexemes.get(nextTokenIndex).equals(")")
                        && "false".equals(temp.value)) {
                    ifIsTrue = false;
                }
                nextTokenIndex++;
                statement(ifIsTrue); //pass it into the statement
                if (nextTokenIndex < tokens.size() && lexemes.get(nextTokenIndex).equals("else")) {
                    nextTokenIndex++;
                    statement(!ifIsTrue); //if there is an else then execute else (only if condition was false
                }
            } else {
                error(); //syntax error detection
            }
        } else {
            error();
        }
    }

//print expression;
    public static void printStmt(boolean shouldExecute) {
        if (nextTokenIndex < tokens.size() && "print".equals(tokens.get(nextTokenIndex))) {
            nextTokenIndex++;
            if (nextTokenIndex < tokens.size() && "id".equals(tokens.get(nextTokenIndex))) {
                if (symbolTable.containsKey(lexemes.get(nextTokenIndex))) {
                    if (shouldExecute) {
                        System.out.println("Printing value of " + lexemes.get(nextTokenIndex) + ": " + symbolTable.get(lexemes.get(nextTokenIndex)).value);
                    }
                    nextTokenIndex++;
                } else {
                    System.out.println("Error: Variable " + lexemes.get(nextTokenIndex) + " not declared.");
                    System.exit(1);
                }
                if (lexemes.get(nextTokenIndex).equals(";")) {
                    nextTokenIndex++;
                } else {
                    error();
                }
            } else {
                idDataHolder temp = expression();
                if (shouldExecute) {
                    System.out.println("Printing the value of expr: " + temp.value);
                }
                nextTokenIndex++;
            }
        } else {
            error();
        }
    }

    public static void whileStmt(boolean shouldExecute) { //work in progress
        boolean keepLooping = false; //there should be a loop that keeps running
        while (keepLooping) {  //but didnt quite figure out where loop goes
            if (nextTokenIndex < tokens.size() && "while".equals(tokens.get(nextTokenIndex))) {
                nextTokenIndex++; //checking while loop syntax
                if (nextTokenIndex < tokens.size() && tokens.get(nextTokenIndex).equals("(")) {
                    nextTokenIndex++;
                    int i = nextTokenIndex;
                    String insideWhile = "";
                    while (!(")").equals(lexemes.get(i))) {
                        insideWhile = insideWhile + lexemes.get(i);
                        i++;
                    }
                    idDataHolder temp = expression(); //evaluate expression inside ()
                    if (nextTokenIndex < tokens.size() && lexemes.get(nextTokenIndex).equals(")")
                            && "true".equals(temp.value)) { //syntax and expr type check
                        keepLooping = true; //if true keepLooping set to true
                    } else if (nextTokenIndex < tokens.size() && lexemes.get(nextTokenIndex).equals(")")
                            && "false".equals(temp.value)) { //if not to false
                        keepLooping = false;
                        shouldExecute = false;
                    }
                    nextTokenIndex++;
                    System.out.println(shouldExecute);
                    statement(shouldExecute);
                    while (keepLooping) { //this is wrong but should be a way to go back to temp = expression
                        //nextTokenIndex++; //using something like a goto method
                        statement(shouldExecute);
                        //nextTokenIndex++;
                        break;
                    }
                } else {
                    error();
                }
            } else {
                error();
            }
        }
    }

    //return expression; 
    public static void returnStmt(boolean shouldExecute) {
        if (nextTokenIndex < tokens.size() && "return".equals(tokens.get(nextTokenIndex))) {
            nextTokenIndex++;
            if (nextTokenIndex < tokens.size() && "id".equals(tokens.get(nextTokenIndex))) {
                if (symbolTable.containsKey(lexemes.get(nextTokenIndex))) { //check if variable has been declated
                    if (shouldExecute) {
                        System.out.println("Returning value of " + lexemes.get(nextTokenIndex) + ": "
                                + symbolTable.get(lexemes.get(nextTokenIndex)).value);
                    }
                    nextTokenIndex++;
                } else {//and act accordingly
                    System.out.println("Error: Variable " + lexemes.get(nextTokenIndex) + " not declared.");
                    System.exit(1);
                }
                if (lexemes.get(nextTokenIndex).equals(";")) { //check for semicolon
                    nextTokenIndex++;//if its there move on
                } else {
                    error(); //else error
                }
            } else { //if not id
                idDataHolder temp = expression(); //evaluate the expression
                if (shouldExecute) {//and if should be executed print stuff
                    System.out.println("Returning value of " + temp.value);
                }
            }
        } else {
            error();
        }
    }

    //Assignment --> id assignOp Expression ;
    public static void assignment(boolean shouldExecute) {
        if (!symbolTable.containsKey(lexemes.get(nextTokenIndex))) { //check if variable has been declared
            System.out.println("Error: Variable " + lexemes.get(nextTokenIndex) + " not declared.");
            System.exit(1);
        }
        if (nextTokenIndex < tokens.size() && "id".equals(tokens.get(nextTokenIndex))) { //if id
            if (symbolTable.containsKey(lexemes.get(nextTokenIndex))) { //declaration check
                String idName = lexemes.get(nextTokenIndex); //save variable name thats on the left of assignment stmt
                String typeOfID = symbolTable.get(lexemes.get(nextTokenIndex)).type; //saving type of whats on the left
                nextTokenIndex++;
                if (nextTokenIndex < tokens.size() && "assignOp".equals(tokens.get(nextTokenIndex))) {
                    nextTokenIndex++;
                    idDataHolder expr = expression(); //gets whatevers on the rightside of assignment
                    if (typeOfID.equals("int") && expr.type.equals("float")) { //if int = float error
                        System.out.println("Error: Narrowing Conversion is not allowed!");
                        System.exit(1);
                    } //otherwise add value to the symboltable, and print he value
                    if (shouldExecute) {//check if stmt should actually be executed
                        if(expr.value != null) { //and that right side has actual value
                            symbolTable.get(idName).value = expr.value; //if yers print it else move on
                            //this is done for syntax checking purposes
                            System.out.println("The value of " + idName + " is: " + symbolTable.get(idName).value);
                        } else {
                            System.out.println("The expression on the right side of " + idName +" holds no value");
                            System.exit(1);
                        } 
                    }
                    //semicolon check at the end of assignment
                } else {
                    error();
                }
                if (nextTokenIndex < tokens.size() && tokens.get(nextTokenIndex).equals(";")) {
                    nextTokenIndex++; //check the assignment ends with ;
                } else {
                    error();
                }
            } else {
                System.out.println("Error: Variable " + lexemes.get(nextTokenIndex) + " not declared.");
                System.exit(1);
            }
        } else {
            error();
        }
    }

    //Expression --> conjunction || conjunction
    public static idDataHolder expression() { //same as for &&, need both sides to
        idDataHolder temp1 = conjunction(); //be boolean
        idDataHolder temp3 = new idDataHolder(null, null); //same purpose
        while (nextTokenIndex < tokens.size() && "||".equals(tokens.get(nextTokenIndex))) {
            String temp1type = temp1.type;
            nextTokenIndex++;
            idDataHolder temp2 = conjunction();
            String temp2type = temp2.type;
            if (temp1type.equals(temp2type) && temp2type.equals("bool")) { //check if both are bool
                boolean result = true;
                boolean t1 = Boolean.valueOf(temp1.type);
                boolean t2 = Boolean.valueOf(temp2.type);
                if (!t1 && !t2) { //if both are false
                    result = false; //entire expression is false
                } else {
                    result = true; //otherwise one of them is true, so expression is true
                }
                temp3.value = Boolean.toString(result);
                
               if (temp3.value != null){
                   temp3.type = "float";
               }
           
            } else { //types are not bool, then its a type mismatch
                System.out.println("Error: Types in Expression operation do not match");
                System.exit(1);
            }
        }
        if (temp3.value != null) { //same check
            return temp3;
        } else {
            return temp1;
        }
    }

    //conjunction --> equality && equality
    public static idDataHolder conjunction() { //this method is a little different
        idDataHolder temp1 = equality();
        idDataHolder temp3 = new idDataHolder(null, null);
        while (nextTokenIndex < tokens.size() && "comparisonOp".equals(tokens.get(nextTokenIndex))) {
            String temp1type = temp1.type;
            nextTokenIndex++;
            idDataHolder temp2 = equality();
            String temp2type = temp2.type;
            temp3.type = "bool"; //same
            if (temp1type.equals(temp2type) && temp2type.equals("bool")) { //we need lexemes on left and
                boolean result = true;
                boolean t1 = Boolean.valueOf(temp1.type); //right of && to be bools so we can check if they're
                boolean t2 = Boolean.valueOf(temp2.type); //true or not
                if (t1 == t2) { //if they are both true, overall expression is true
                    result = true;
                } else { //if not expression is false
                    result = false;
                }
                temp3.value = Boolean.toString(result); //here it is
            } else { //if both of them are not booleans, we have a type mismatch
                System.out.println("Error: Types in Conjunction operation do not match");
                System.exit(1);
            }
        }
        if (temp3.value != null) {
            return temp3;
        } else {
            return temp1;
        }
    }

    //equality --> relation equOp relation
    public static idDataHolder equality() { //this does the same as relation but
        idDataHolder temp1 = relation(); //different operations and added char comparison support
        idDataHolder temp3 = new idDataHolder(null, null);
        if (nextTokenIndex < tokens.size() && "equOp".equals(tokens.get(nextTokenIndex))) {
            String operation = lexemes.get(nextTokenIndex);
            String temp1type = temp1.type;
            nextTokenIndex++;
            idDataHolder temp2 = relation();
            String temp2type = temp2.type;
            temp3.type = "bool"; //serves the same purpose as in the method below
            if (temp1type.equals(temp2type) && temp2type.equals("int")) {
                boolean result = true;
                int t1 = Integer.parseInt((String) temp1.value);
                int t2 = Integer.parseInt((String) temp2.value);
                if (operation.equals("==")) {
                    if (t1 == t2) {
                        result = true;
                    } else {
                        result = false;
                    }
                }
                if (operation.equals("!=")) {
                    if (t1 != t2) {
                        result = true;
                    } else {
                        result = false;
                    }
                }
                temp3.value = Boolean.toString(result);
            } else if (temp1type.equals("float") || temp2type.equals("float")) {
                boolean result = true;
                float t1 = Float.parseFloat((String) temp1.value);
                float t2 = Float.parseFloat((String) temp2.value);
                if (operation.equals("==")) {
                    if (t1 == t2) {
                        result = true;
                    } else {
                        result = false;
                    }
                }
                if (operation.equals("!=")) {
                    if (t1 != t2) {
                        result = true;
                    } else {
                        result = false;
                    }
                }
                temp3.value = Boolean.toString(result);

            } else if (temp1type.equals(temp2type) && temp2type.equals("char")) {
                boolean result = true;
                char t1 = ((String) temp1.value).charAt(0);
                char t2 = ((String) temp1.value).charAt(0);
                if (operation.equals("==")) {
                    if (t1 == t2) {
                        result = true;
                    } else {
                        result = false;
                    }
                }
                if (operation.equals("!=")) {
                    if (t1 != t2) {
                        result = true;
                    } else {
                        result = false;
                    }
                }
                temp3.value = Boolean.toString(result);
                temp3.value = temp3.value.getClass().getName();
            } else {
                System.out.println("Error: Types in Equality operation do not match.");
                System.exit(1);
            }
        }
        if (temp3.value != null) {
            return temp3;
        } else {
            return temp1;
        }
    }

    //relation addition relOp addition
    public static idDataHolder relation() {
        idDataHolder temp1 = addition();
        idDataHolder temp3 = new idDataHolder(null, null);
        if (nextTokenIndex < tokens.size() && "relOp".equals(tokens.get(nextTokenIndex))) {
            String operation = lexemes.get(nextTokenIndex);
            String temp1type = temp1.type;
            nextTokenIndex++;
            idDataHolder temp2 = addition();
            String temp2type = temp2.type;
            temp3.type = "bool"; //returns the value of the evaluated expression, true or false
            if (temp1type.equals(temp2type) && temp2type.equals("int")) { //deciding what to typecast as
                boolean result = true;
                int t1 = Integer.parseInt((String) temp1.value);
                int t2 = Integer.parseInt((String) temp2.value);
                if (operation.equals(">")) { //deciding as to what operation we are looking at
                    if (t1 > t2) {
                        result = true;
                    } else {
                        result = false;
                    }
                }
                if (operation.equals("<")) {
                    if (t1 < (int) t2) {
                        result = true;
                    } else {
                        result = false;
                    }
                }
                if (operation.equals("<=")) {
                    if (t1 <= (int) t2) {
                        result = true;
                    } else {
                        result = false;
                    }
                }
                if (operation.equals(">=")) {
                    if (t1 >= t2) {
                        result = true;
                    } else {
                        result = false;
                    }
                }
                //have temp3
                temp3.value = Boolean.toString(result); //here it is
            } else if (temp1type.equals("float") || temp2type.equals("float")) { //allowing widening conversion
                boolean result = true;
                System.out.println(temp1.type);
                System.out.println(temp1.value);
                float t1 = Float.parseFloat((String) temp1.value);
                float t2 = Float.parseFloat((String) temp2.value);
                if (operation.equals(">")) { //checking operation
                    if (t1 > t2) {
                        result = true;
                    } else {
                        result = false;
                    }
                }
                if (operation.equals("<")) {
                    if (t1 < t2) {
                        result = true;
                    } else {
                        result = false;
                    }
                }
                if (operation.equals("<=")) {
                    if (t1 <= t2) {
                        result = true;
                    } else {
                        result = false;
                    }
                }
                if (operation.equals(">=")) {
                    if (t1 >= t2) {
                        result = true;
                    } else {
                        result = false;
                    }
                }
                temp3.value = result;
            } else { //if nothing is matched, theres an error.
                System.out.println("Error: Types in Relation operation do not match.");
                System.exit(1);
            }
        }
        if (temp3.value != null) { //if temp3 has been assigned value returned it
            return temp3;
        } else {
            return temp1; //else pass temp1 up
        }
    }

    //addition --> term addOp term
    @SuppressWarnings("ConvertToStringSwitch")
    public static idDataHolder addition() { //this method does the same as term, just operations
        idDataHolder temp1 = term(); //are + or - instead of multOps
        idDataHolder temp3 = new idDataHolder(null, null);
        while (nextTokenIndex < tokens.size() && "addOp".equals(tokens.get(nextTokenIndex))) {
            String operation = lexemes.get(nextTokenIndex);
            String temp1type = temp1.type;
            nextTokenIndex++;
            idDataHolder temp2 = term();
            String temp2type = temp2.type;
            if (temp1type.equals(temp2type) && temp2type.equals("int")) {
                int result = 0;
                int t1 = Integer.parseInt((String) temp1.value);
                int t2 = Integer.parseInt((String) temp2.value);
                if (operation.equals("+")) {
                    result = t1 + t2;
                } else if (operation.equals("-")) {
                    result = t1 - t2;
                }
                temp3.value = Integer.toString(result);
            } else if ((temp1type.equals("float") || temp1type.equals("int")) && temp2type.equals("float")) {
                float result = 0;
                float t1 = Float.parseFloat((String) temp1.value);
                float t2 = Float.parseFloat((String) temp2.value);
                if (operation.equals("+")) {
                    result = t1 + t2;
                } else if (operation.equals("-")) {
                    result = t1 - t2;
                }
                temp3.value = Float.toString(result);
            } else {
                System.out.println("Error: Types in + or - operation do not match.");
                System.exit(1);
            }
        }
        if (temp3.value != null) { //if temp3 has been assigned value returned it
            return temp3;
        } else {
            return temp1; //else pass temp1 up
        }
    }

    //term --> factor multOp factor
    @SuppressWarnings("ConvertToStringSwitch")
    public static idDataHolder term() {
        idDataHolder temp1 = factor(); //get the type value pair
        while (nextTokenIndex < tokens.size() && "multOp".equals(tokens.get(nextTokenIndex))) { //while there is more stuff
            String operation = lexemes.get(nextTokenIndex); //figure out what the operation is
            String temp1type = temp1.type; //obtain the type
            nextTokenIndex++;
            idDataHolder temp2 = factor(); //obtain whats after the operation
            String temp2type = temp2.type; //obtains its type too
            /*
             There are two cases, one when both operands are ints, so program
             is parsing them as ints, and executing operations.
             Another case is when one of them is equal to float, then we parse
             them as a float, because widening conversion is allowed.
             */
            if (temp1type.equals(temp2type) && temp2type.equals("int")) {
                int result = 0;
                int t1 = Integer.parseInt((String) temp1.value); //storing the parsed value
                int t2 = Integer.parseInt((String) temp2.value);
                if (operation.equals("*")) { //deciding which operation to execute
                    result = t1 * t2;
                } else if (operation.equals("/")) {
                    result = t1 / t2;
                } else if (operation.equals("%")) {
                    result = t1 % t2;
                }
                //converting the result value back to string
                temp1.value = Integer.toString(result);
            } else if ((temp1type.equals("float") || temp1type.equals("int")) && temp2type.equals("float")) {
                float result = 0;
                float t1 = Float.parseFloat((String) temp1.value);
                float t2 = Float.parseFloat((String) temp2.value);
                if (operation.equals("*")) {
                    result = t1 * t2;
                } else if (operation.equals("/")) {
                    result = t1 / t2;
                } else if (operation.equals("%")) {
                    result = t1 % t2;
                }
                //converting the result value back to string so that we are consistent
                //with the type that values are stored in the dataholder class and symboltable.
                temp1.value = Float.toString(result);
            } else { //if none of these is satisfied, ie if types are wrong then type mismatch error
                System.out.println("Error: Types in *, / or % operation do not match.");
                System.exit(1); //and exit   
            }
        }
        return temp1; //pass up the type/value pair up
    }

    //check if after * we have either id or some numeric value
    public static idDataHolder factor() {
        idDataHolder temp;
        if (nextTokenIndex < tokens.size() && "id".equals(tokens.get(nextTokenIndex))) {
            if (symbolTable.containsKey(lexemes.get(nextTokenIndex))) {
                nextTokenIndex++;
                return symbolTable.get(lexemes.get(nextTokenIndex - 1));
            } else {
                System.out.println("Error: Variable " + lexemes.get(nextTokenIndex) + " not declared.");
                System.exit(1);
                return null;
            }
        } else if (nextTokenIndex < tokens.size() && "intLiteral".equals(tokens.get(nextTokenIndex))) {
            temp = new idDataHolder("int", lexemes.get(nextTokenIndex));
            nextTokenIndex++; //if not id, we make a new dataholder object with the correspoding
            return temp; //type value pair and return it
        } else if (nextTokenIndex < tokens.size() && "boolLiteral".equals(tokens.get(nextTokenIndex))) {
            temp = new idDataHolder("bool", lexemes.get(nextTokenIndex));
            nextTokenIndex++;
            return temp;
        } else if (nextTokenIndex < tokens.size() && "floatLiteral".equals(tokens.get(nextTokenIndex))) {
            temp = new idDataHolder("float", lexemes.get(nextTokenIndex));
            nextTokenIndex++;
            return temp;
        } else if (nextTokenIndex < tokens.size() && "charLiteral".equals(tokens.get(nextTokenIndex))) {
            temp = new idDataHolder("char", lexemes.get(nextTokenIndex));
            nextTokenIndex++;
            return temp;
        } else if (nextTokenIndex < tokens.size() && "(".equals(tokens.get(nextTokenIndex))) { //checking for opening 
            nextTokenIndex++; //accounting for expression inside parenthesis
            temp = expression();
            if (nextTokenIndex < tokens.size() && ")".equals(tokens.get(nextTokenIndex))) { //checking closure
                nextTokenIndex++;
                return temp;
            } else {
                error();
                return null;
            }
        } else {
            error();
            return null;
        }
    }

    //error
    public static void error() {
        if (nextTokenIndex < tokens.size()) { //if didnt get to end
            System.out.println("Error: Invalid Expression. Error location <["
                    + tokens.get(nextTokenIndex) + "," + lexemes.get(nextTokenIndex) + "]>");
            nextTokenIndex++;
            System.exit(1);
        } else { //if got to end but expression still not valid
            System.out.println("Error: Incomplete Expression. Expecting More Terms");
            System.exit(1);
        }
    }
}
