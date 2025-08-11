package com.alliance.palletkvalproject.Logic.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class PalletSheetMethods {

    //Searches the CSV file for the MO then assigns the values to an array of Strings
    //It uses commas to determine the values and values is reassigned every loop so it does not continuously add to the var
    public static String[] findMoInFile(String MO){

        String filePath = "Orders.csv";

        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skips the header with the MO,LineNumber... etc

            while((line = br.readLine()) != null){

                String[] values = line.split(",");
                if(values[0].trim().equalsIgnoreCase(MO.trim())){
                    return values;

                }
            }

        }catch(IOException e){

            System.out.println("Order file not found or unreadable: " + filePath + e.getMessage());
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
