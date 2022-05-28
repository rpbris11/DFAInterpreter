package Project1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * This class reads the needed information to construct a deterministic finite automata from a provided .txt file,
 * and, storing it appropriately, makes the necessary comparisons to validate whether specific strings (provided
 * in another .txt file) are accepted or rejected. This information is then written to an output file.
 *
 * Ryan Brisbane, COSC 485 DFA Project
 * Last updated: May 17, 2022 7:34 pm
 */
public class DFA {

    List<Integer> states;
    List<Character> alphabet;
    int start;
    List<Integer> finalStates;
    List<List<Object>> transitionFunction;

    public DFA(List<Integer> states, List<Character> alphabet,
               int start, List<Integer> finalStates, List<List<Object>> transitionFunction){

        this.states = states;
        this.alphabet = alphabet;
        this.start = start;
        this.finalStates = finalStates;
        this.transitionFunction = transitionFunction;
    }

    /**
     * this is the big method! the one that matters! it derives the final state of the provided string and
     * returns whether the string is accepted by the machine or not.
     *
     * The list of lists that is the transition function is used to determine what the next state the string reaches
     * will be and this is tracked with every iteration.
     * @param test
     * @return boolean - true if string is in an accepted final state, false otherwise
     */
    public boolean testString(String test){
        int currentState = start;
        for(int i = 0; i < test.length(); i++){
            for(int j = 0; j < transitionFunction.size(); j++){
                int tempState = ((Number)transitionFunction.get(j).get(0)).intValue();
                char input = ((Character)transitionFunction.get(j).get(1)).charValue();
                if(tempState == currentState && input == test.charAt(i)){
                    currentState = ((Number)transitionFunction.get(j).get(2)).intValue();
                    break;
                }
            }
        }
        if(finalStates.contains(currentState)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * This method reads and stores the test strings provided in the other .txt file for validation
     * @return list of strings for testing
     */
    public static List<String> readStringFile(){
        List<String> testStrings = new ArrayList<>();
        try {
            File f = new File("COSC485_P1_StringsDFA.txt");
            Scanner read = new Scanner(f);
            while(read.hasNextLine()){
                testStrings.add(read.nextLine());
            }
        } catch(IOException e){
            System.out.println("Error in reading test string file.");
        }
        return testStrings;
    }

    /**
     * This method creates (or replaces) the output file with the results from the testing of each string
     * @param testStrings
     */
    public void validationOutput(List<String> testStrings){
        try {
            File output = new File("COSC485_P1_AnswersDFA.txt");
            FileWriter write = new FileWriter("COSC485_P1_AnswersDFA.txt");

            for(int i = 0; i < testStrings.size(); i++){
                boolean test = testString(testStrings.get(i));
                if(test){
                    write.write(testStrings.get(i) + " is accepted.\n");
                }
                if(!test){
                    write.write(testStrings.get(i) + " is rejected.\n");
                }
            }
            write.close();
        }catch(IOException e){
            System.out.println("Error in writing to output file.");
        }
    }

    /**
     * Method that reads the provided DFA .txt file, storing each line in an arraylist for
     * further breakdown (in subsequent methods)
     *
     * ONE NOTE ABOUT THIS: all the subsequent methods assume that all the necessary data (states, alphabet, starting
     * state, final states, and transition functions ALL START ON THE SAME LINE EACH TIME. They do in my
     * different input files but if they don't for you, this won't work properly.
     *
     * @return list of strings, one line of the file per string
     */
    public static List<String> readDFAFile(){
        List<String> dfaLines = new ArrayList<>();
        try {
            File f = new File("COSC485_P1_DFA.txt");
            Scanner read = new Scanner(f);
            while(read.hasNextLine()){
                dfaLines.add(read.nextLine());
            }
        }catch (IOException e){
            System.out.println("Error in reading DFA file.");
        }
        return dfaLines;
    }

    /**
     * This method reads the txt file and determines (and stores) what states the machine can reach/are accepted
     * @param dfaLines
     * @return integer list of possible states
     */
    public static List<Integer> parseStates(List<String> dfaLines){
        String statesList = dfaLines.get(5).substring(dfaLines.get(5).indexOf("{"),
                dfaLines.get(5).indexOf("}")+1);
        List<Integer> states = new ArrayList<>();
        int count = 0;
        for(int i = 0; i < statesList.length(); i++){
            if(statesList.charAt(i) == 'q'){
                count++;
                states.add(count);
            }
        }
        return states;
    }

    /**
     * A method that reads the txt file and stores the accepted characters for this language
     * @param dfaLines
     * @return a list of characters (a, b, c, etc)
     */
    public static List<Character> parseAlphabet(List<String> dfaLines){
        String alphabetList = dfaLines.get(7).substring(dfaLines.get(7).indexOf("{"),
                dfaLines.get(7).indexOf("}")+1);
        List<Character> alphabet = new ArrayList<>();
        for(int i = 0; i < alphabetList.length(); i++){
            if(alphabetList.charAt(i) != (' ') && alphabetList.charAt(i) != ','
                    && alphabetList.charAt(i) != '{' && alphabetList.charAt(i) != '}'){
                alphabet.add(alphabetList.charAt(i));
            }
        }
        return alphabet;
    }

    /**
     * A method that reads the starting state from the provided txt file. Used later
     * @param dfaLines
     * @return an integer, representing the state the DFA starts at (usually 0)
     */
    public static int parseStartingState(List<String> dfaLines){
        char startingState = dfaLines.get(9).charAt(dfaLines.get(9).indexOf("q")+1);
        return Character.getNumericValue(startingState);
    }

    /**
     * Method that reads the provided list of final states and stores it for use
     * @param dfaLines
     * @return a list of integers representing states that the machine accepts, used for validating a string
     */
    public static List<Integer> parseFinalStates(List<String> dfaLines){
        String finalStateList = dfaLines.get(11).substring(dfaLines.get(11).indexOf("{"),
                dfaLines.get(11).indexOf("}")+1);
        List<Integer> finalStates = new ArrayList<>();
        for(int i = 0; i < finalStateList.length(); i++){
            if(finalStateList.charAt(i) == 'q'){
                finalStates.add(Character.getNumericValue(finalStateList.charAt(i+1)));
            }
        }
        return finalStates;
    }

    /**
     * Method that takes the transition function provided in the txt file and stores it appropriately for later use
     * @param dfaLines
     * @return A list of sublists containing the entry states, the direction (letter), and the exit state
     */
    public static List<List<Object>> parseTransitionFunction(List<String> dfaLines){
        String transFunction = "{";
        for(int i = 14; i < dfaLines.size()-1; i++){
            transFunction += dfaLines.get(i).substring(dfaLines.get(i).indexOf('('),
                    dfaLines.get(i).indexOf(')')+1);
            transFunction += ',';
        }
        List<List<Object>> transitionFunction = new ArrayList<>();
        for(int i = 0; i < transFunction.length()-1; i+=14){
            List<Object> temp = new ArrayList<>();
            temp.add(Character.getNumericValue(transFunction.charAt(4+i)));
            temp.add(transFunction.charAt(7+i));
            temp.add(Character.getNumericValue(transFunction.charAt(11+i)));
            transitionFunction.add(temp);
        }
        return transitionFunction;
    }


    /**
     * Main method, runs each of the above methods to construct the DFA and then tests the provided strings
     * for acceptance or rejection
     * @param args
     */
    public static void main(String[] args){
        //reading through all the info and storing appropriately
        List<String> dfaLines = readDFAFile();
        List<Integer> states = parseStates(dfaLines);
        List<Character> alphabet = parseAlphabet(dfaLines);
        int startingState = parseStartingState(dfaLines);
        List<Integer> finalStates = parseFinalStates(dfaLines);
        List<List<Object>> transitionFunction = parseTransitionFunction(dfaLines);

        //DFA object to call the string validation
        DFA automata = new DFA(states, alphabet, startingState,
                finalStates, transitionFunction);

        List<String> testStrings = readStringFile();
        automata.validationOutput(testStrings);
        //if we reach this point, no exceptions were thrown and the file was successfully created
        System.out.println("Output file created. Exiting..");
    }
}