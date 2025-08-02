package com.alliance.palletkvalproject.Logic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class palletSheetMethods {

    //Prompts the user for the MO they would like to search the system for then returns it
    public static String promptMO() {
        String MO;
        Scanner kb = new Scanner(System.in);

        while (true) {

            //TODO: add input sanitization and enforce typing 
            int lengthOfMO = 7;
            System.out.println("Enter the MO for the order: ");
            MO = kb.nextLine();

            if (MO.length() != lengthOfMO) {
                System.out.println("The MO must be " + lengthOfMO + " digits."); // MO was bad length
            } else {
                break; // MO was good length
            }
        }
        return MO;
    }


    //Searches the CSV file for the MO then assigns the values to an array of Strings
    //It uses commas to determine the values and values is reassigned every loop so it does not continuously add to the var
    public static String[] findMoInFile(String MO){

        String filePath = "Orders.csv";//TODO: change to use a more filepath safe method? Also error handling for if file does not exist

        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skips the header with the MO,LineNumber... etc

            while((line = br.readLine()) != null){

                String[] values = line.split(",");
                if(values[0].equals(MO)){
                    return values;

                }
            }

        }catch(IOException e){

            System.out.println("Error reading file: " + e.getMessage());
        }

        return null;
    }

    //Checks if there is any empty field in a line item
    public static boolean anyFieldEmpty(String[] values) {
        for (String value : values) {
            if (value == null || value.trim().isEmpty()) return true;
        }
        return false;
    }
}
