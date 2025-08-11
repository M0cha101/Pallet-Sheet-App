package com.alliance.palletkvalproject.Logic.util;

import java.util.Arrays;
import java.util.Set;

public class SizeUtils {

    public static final String[] sizes = {
            "36x80", "34x80", "32x80", "30x80",
            "28x80", "26x80", "24x80", "22x80",
            "20x80", "18x80"
    };

    // A set of all the sizes that are valid, used in setUpQuantityEnter to check if the size exists
    private static final Set<String> VALID_SIZES = Set.of(sizes);

    //Checks if a given size is in the list of valid sizes
    public static boolean isValidSize(String size) {
        return VALID_SIZES.contains(size);
    }

    //Returns the column index for a size, throws if not found
    public static int getSizeColumnIndex(String size) {
        for (int i = 0; i < sizes.length; i++) {
            if (sizes[i].equals(size)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Invalid size: " + size);
    }
}
