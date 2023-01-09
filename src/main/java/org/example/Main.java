package org.example;

import java.sql.*;
import java.util.Scanner;
public class Main {
//Simple function to remove spaces in math formula line
//-----------------------------------------------------
    public static String TrimLine(String LineInput){
        return LineInput.replace(" ","");
    }
//-----------------------------------------------------
//Function to validate line appearance as a valid math expression
//-----------------------------------------------------
    public static boolean ValidateLine(String LineInput){
        Boolean Validation = true;

        char CurrentItem = LineInput.charAt(0);
        char NextItem;
        char ThirdItem;
//math line can't start from these characters
        if (CurrentItem == '*' || CurrentItem =='/' || CurrentItem == '+' || CurrentItem =='.') Validation = false;

        int CountBracket = 0;
        int CountDots = 0;
//one by one cycle that validate characters appear (please note that currently letters and multiplication by parentheses without multiplication character are forbidden, but can be added )
        for (int i = 0; i<LineInput.length();i++){

            CurrentItem =  LineInput.charAt(i);

            switch (CurrentItem){
                case '*':
                case '/':
                case '+':
                case '-': {
                    if (i+1 == LineInput.length()) {
                        //System.out.println("last char");
                        Validation = false;
//math line can't end with these characters
                    } else {
                        NextItem = LineInput.charAt(i + 1);
                        if (NextItem == '*' || NextItem == '+' || NextItem == '/' || NextItem == '.' ||  NextItem == ')') {
                            //System.out.println("two math char");
                            Validation = false;
//there can't be two mathematical operations in a raw
                        } else if (NextItem == '-' && LineInput.length() > i + 2) {
                            ThirdItem = LineInput.charAt(i + 2);
                            if (ThirdItem == '*' || ThirdItem == '/' || ThirdItem == '+' || ThirdItem == '-' || ThirdItem == '.') {
                                //System.out.println("three math chars");
                                Validation = false;
//Separate validation for '-' character
                            }
                        }
                    }
                    CountDots=0;
                }
                    break;
                case '(':
                    CountBracket++;

                    if (i+1<LineInput.length()) {
                        NextItem = LineInput.charAt(i + 1);
                        if (NextItem == '*' || NextItem == '+' || NextItem == '/' || NextItem == '.'|| NextItem == ')') {
                            //System.out.println("math char afet bracket");
                            Validation = false;
                        } else if (NextItem == '-' && LineInput.length() > i + 2) {
                            ThirdItem = LineInput.charAt(i + 2);
                            if (ThirdItem == '*' || ThirdItem == '/' || ThirdItem == '+' || ThirdItem == '-' || ThirdItem == '.') {
                                //System.out.println("two math chars after bracket");
                                Validation = false;
                            }
                        }
                    }
//Validate that open Bracket can't contain math operator after it
                    if (i>0){
                        NextItem = LineInput.charAt(i - 1);
                        if (NextItem == '*' || NextItem == '+' || NextItem == '/' || NextItem == '-' || NextItem == '(') ;else {
                            //System.out.println("no math char before bracket");
                            Validation = false;
                        }
                    }
//Validate that open Bracket should contain math operator before it
                    break;
                case ')':{
                    CountBracket--;

                    if (CountBracket < 0 ) Validation = false;
//Check that there can't close bracket before open bracket
                    if (i+1<LineInput.length()){
                        NextItem = LineInput.charAt(i + 1);
                        if (NextItem == '*' || NextItem == '+' || NextItem == '/' || NextItem == '-' || NextItem == ')') ;else {
                            //System.out.println("no math char afet bracket");
                            Validation = false;
                        }
//Validate that close Bracket should have math operator after it
                    }
                }
                    break;
                case '.':
                    CountDots++;
                    if (i+1==LineInput.length()){
                        //System.out.println("dot can't be last character");
                        Validation = false;
                    }
//Validate that there can be only one dot in one number
                    break;
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '0':
                    break;
            //validation that all other characters are forbidden
                default:
                    Validation = false;
                    break;
            }
            if (CountDots > 1) {
            //validation that number has only one dot
                Validation = false;
            }
        }
        if (CountBracket != 0 ) {
            //validation that all bracket were closed
            Validation = false;
        }
        return Validation;
    }
//-----------------------------------------------------
//update line make all subtraction operations as addition with negative number and replace double minus as one plus
//-----------------------------------------------------
    public static String NormalizeString (String LineInput){
        String LineOutput=LineInput;
        char CurrentItem;
        char PreviousItem;

        for (int i=1;i<LineOutput.length();i++){
            CurrentItem = LineOutput.charAt(i);
            if (CurrentItem == '-') {
                PreviousItem = LineOutput.charAt(i - 1);
                if (PreviousItem >= '0' && PreviousItem <= '9')
                    LineOutput=LineOutput.substring(0,i)+"+"+LineOutput.substring(i,LineOutput.length());
                if (PreviousItem == '-')
                    LineOutput=LineOutput.substring(0,i-1)+"+"+LineOutput.substring(i+1,LineOutput.length());
            }
        }

        return LineOutput;
    }
//-----------------------------------------------------
//Find full number that located before Position number in the string
//-----------------------------------------------------
    public static String FindLeft (String LineInput, int Position){
        int i = Position-1;
        boolean NotCompletedNumber = true;
        char CurrentItem;
        String output = "";

        CurrentItem = LineInput.charAt(i);
        while (NotCompletedNumber){
            output = CurrentItem + output;

            i--;
            if (i>=0) {
                CurrentItem = LineInput.charAt(i);

                if (CurrentItem == '+' || CurrentItem == '*' || CurrentItem == '/') NotCompletedNumber = false;
            } else NotCompletedNumber = false;

        }
        return output;
    }
//-----------------------------------------------------
//Find full number that located after Position number in the string
//-----------------------------------------------------
    public static String FindRight (String LineInput, int Position){
        int i = Position+1;
        boolean NotCompletedNumber = true;
        char CurrentItem;
        String output = "";

        CurrentItem = LineInput.charAt(i);

        while (NotCompletedNumber){
            output = output+CurrentItem;

            i++;
            if (i< LineInput.length()) {
                CurrentItem = LineInput.charAt(i);

                if (CurrentItem == '+' || CurrentItem == '*' || CurrentItem == '/') NotCompletedNumber = false;
            } else NotCompletedNumber = false;
        }
        return output;
    }
//Function to replace part of original line with new value
    public static String MergeLines(String OriginalLine,String UpdateLine, int PosStart, int PosEnd){
        String WorkLine="";
        if ((PosStart > 0) && (PosEnd < OriginalLine.length()))
                WorkLine = OriginalLine.substring(0, PosStart) + UpdateLine + OriginalLine.substring(PosEnd);
            else if ((PosStart == 0) && (PosEnd < OriginalLine.length()))
                WorkLine = UpdateLine + OriginalLine.substring(PosEnd);
            else if ((PosStart > 0) && (PosEnd == OriginalLine.length()))
                WorkLine = OriginalLine.substring(0, PosStart) + UpdateLine;
            else if ((PosStart == 0) && (PosEnd == OriginalLine.length()))
                WorkLine = UpdateLine;

        return WorkLine;
    }
//Function that return is there available mathematical functions to execute
    public static boolean MathAvailable (String InputLine){
        boolean result;

        result = (InputLine.indexOf('*')>0||InputLine.indexOf('/')>0||InputLine.indexOf('+')>0);
        return result;
    }
//-----------------------------------------------------
//Function that calculate simple mathematic actions as +,* or / , minus update to plus with NormalizeString function
//-----------------------------------------------------
    public static String DoMath(String LineInput,int position){
        double item1 = Double.parseDouble(FindLeft(LineInput,position));
        double item2 = Double.parseDouble(FindRight(LineInput,position));
        double processed;
        String result;
        switch (LineInput.charAt(position)){
            case '*':{
                processed = item1*item2;
                break;
            }
            case '/':{
                processed = item1/item2;
                break;
            }
            case '+':{
                processed = item1+item2;
                break;
            }
            default:{
                processed = 0;
                System.out.println("Invalid math");
                break;
            }
        }
        result = String.valueOf(processed);
        return result;
    }
//-----------------------------------------------------
//simple function that calculate values, please note about recursion
//-----------------------------------------------------
    public static String CalulateString (String LineInput){
        String WorkLine = LineInput;
        String SubLine;
        String MathResults;

        char CurrentItem;
        int CountBracket = 0;
        int MathPosition;
        int ForwardBracketPosition;
        int BackBracketPosition;
        int i;

//Find brackets start and end positions to process segment separately by recursion
        while (WorkLine.indexOf('(')>=0){

            ForwardBracketPosition = WorkLine.indexOf('(');
            BackBracketPosition = 0;

            CountBracket = 1;
            i = ForwardBracketPosition+1;

                while (CountBracket > 0) {
                    CurrentItem = WorkLine.charAt(i);

                    if (CurrentItem == '(') CountBracket++;
                    else if (CurrentItem == ')') {
                        CountBracket--;
                        if (CountBracket == 0) BackBracketPosition = i;

                    }
                    i++;
                }
 //           System.out.println(WorkLine);
 //           System.out.println(ForwardBraketPosition+1);
 //           System.out.println(BackBracketPosition-1);
                SubLine = CalulateString(WorkLine.substring(ForwardBracketPosition+1,BackBracketPosition));
 //           System.out.println(SubLine + "TEST");
                WorkLine = MergeLines(WorkLine,SubLine,ForwardBracketPosition,BackBracketPosition+1);
//each bracket execute as separate function call, function call itself to calculate all brackets values in mathematical order
//when bracket value was calculated it put to line formula, to continue execution as a single number
        }
//Perform Calculation when there is no brackets, execution performs by priority, each mathematical action put result to the line while actions are available
        WorkLine = NormalizeString(WorkLine);
 //       System.out.println(WorkLine);
        while (MathAvailable(WorkLine)){

            MathPosition = WorkLine.indexOf('*');
            if ((WorkLine.indexOf('/')>0&&MathPosition>WorkLine.indexOf('/'))||MathPosition<0) MathPosition = WorkLine.indexOf('/');
            if (WorkLine.indexOf('+')>0&&MathPosition<0) MathPosition = WorkLine.indexOf('+');
            MathResults = DoMath(WorkLine,MathPosition);

            WorkLine = MergeLines(WorkLine,MathResults,MathPosition-FindLeft(WorkLine,MathPosition).length(),MathPosition+1+FindRight(WorkLine,MathPosition).length());
            WorkLine = NormalizeString(WorkLine);

  //          System.out.println(WorkLine);
        }
    return WorkLine;
    }
    public static void main(String[] args) {
        Scanner Input = new Scanner(System.in);
        Scanner Input2 =  new Scanner(System.in);
        boolean NotExit=true;
        boolean Updateloop;
        String MathLine="";
        String Result;
        String condition="";
        int action;
        float filter;

        try {
            Connection sqlite = DriverManager.getConnection("jdbc:sqlite:C:\\databases\\math.db");
            Statement statement = sqlite.createStatement();
            ResultSet SQLout;
            statement.execute("CREATE TABLE IF NOT EXISTS mathematics "+
                             "(_id INTEGER PRIMARY KEY, expression TEXT, result FLOAT)");


            while (NotExit) {
                System.out.println("Hello user, please select an action: \n 1) Enter new record \n 2) View/Update Already exists record \n 3) Exit application");
                action = Input.nextInt();
                //  System.out.println(action);

                if (action == 1) {
                    System.out.println("Please enter mathematical expression");

                    MathLine = Input2.nextLine();
//    MathLine = "-5+2+3*2+(1.5*1.5456)-16-16+10*(5+3-8/2*(10+5*2))+(12.5)";
//    MathLine = "-5/-1.26*-100.50026-5";
//    MathLine = "(10+10*-5/-1+3*5/5)*2";\
//    MathLine = "490429/23*3232-(684/65417*548+57)";
                    MathLine = TrimLine(MathLine);
                    if (ValidateLine(MathLine)) {
                        System.out.println("Math line is valid, answer is:");
                        Result = CalulateString(MathLine);
                        System.out.println(CalulateString(MathLine));
                        statement.execute("INSERT INTO mathematics (expression, result) " +
                                            "VALUES ('"+MathLine+"',"+Result+")");
                        System.out.println("Database was updated with this statement");

                    } else
                        System.out.println("Please use other math line, there is an issue in arguments");
                } else if (action == 2) {
                    Updateloop = true;
                    while (Updateloop) {
                        System.out.println("Do you want to: \n 1) view all records \n 2) view records filtered by result" +
                                " \n 3) update record \n 4) exit to previous menu");
                        action = Input.nextInt();
                        switch (action) {
                            case (1): {
                                statement.execute("SELECT * FROM mathematics");
                                SQLout = statement.getResultSet();
                                while(SQLout.next()){
                                    System.out.println("Record " + SQLout.getInt("_id")+
                                            "\n"+ SQLout.getString("expression") + " = " +
                                            SQLout.getString("result"));
                                }
                                break;
                            }
                            case (2): {
                                System.out.println("Please apply filter type, and enter number \n 1) Filter Less than (<).. X \n 2) Filter bigger than (>) .. X" +
                                        " \n 3) Filter same to (=).. X");

                                action = Input.nextInt();
                                filter = Input.nextFloat();
                                switch (action){
                                    case(1):{
                                        condition = "<";
                                        break;
                                    }
                                    case(2):{
                                        condition = ">";
                                        break;
                                    }
                                    case(3):{
                                        condition = "=";
                                        break;
                                    }
                                }

                                if (action == 1 || action == 2 || action ==3){
                                    statement.execute("SELECT * FROM mathematics WHERE result "+condition+" "+ filter);
                                    SQLout = statement.getResultSet();
                                    while(SQLout.next()){
                                        System.out.println("Record " + SQLout.getInt("_id")+
                                                "\n"+ SQLout.getString("expression") + " = " +
                                                SQLout.getString("result"));
                                    }
                                } else System.out.println("Invalid argument");
                                break;
                            }
                            case (3): {
                                System.out.println("Please enter record ID to update:");
                                action = Input.nextInt();

                                System.out.println("Please enter updated mathematical expression");
                                MathLine = Input2.nextLine();

                                MathLine = TrimLine(MathLine);
                                if (ValidateLine(MathLine)) {
                                    System.out.println("Math line is valid, answer is:");
                                    Result = CalulateString(MathLine);
                                    System.out.println(CalulateString(MathLine));
                                    statement.execute("UPDATE mathematics SET " +
                                            "expression = '"+MathLine+"',result = "+Result+
                                            " WHERE _id = " + action);
                                    System.out.println("Database was updated with updated statement");

                                } else
                                    System.out.println("Invalid logic appear in math line, update was not performed");

                                break;
                            }
                            case (4): {
                                Updateloop = false;
                                break;
                            }
                            default:{
                                System.out.println("Invalid argument please enter numbers 1..4");
                                break;
                            }

                        }
                    }

                } else if (action == 3) NotExit = false;
            }

            statement.close();
            sqlite.close();
        } catch (SQLException e) {
            System.out.println("Issue appear: "+ e.getMessage());
        }


    }
}