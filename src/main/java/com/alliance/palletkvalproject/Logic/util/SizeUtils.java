package com.alliance.palletkvalproject.Logic.util;

public class SizeUtils {

    static String[] sizes = {"36x80", "34x80", "32x80", "30x80", "28x80", "26x80", "24x80", "22x80", "20x80", "18x80"};

    public static int getSizeColumnIndex(String size){
        for (int i = 0; i < sizes.length; i++) {
            if (sizes[i].equals(size)) {
                return i;
            }
        }
        return -1;
        // This method just finds the index of the column that its respective size needs to be at
    }
}
