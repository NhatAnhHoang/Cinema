package com.example.cinema.util;

public class StringUtil {

    /**
     * Check format of email
     *
     * @param target is a email need checking
     */
    public static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * Check field not empty and 6 word
     *
     * @param input text input
     */
    public static boolean isGoodField(String input) {
        if (input == null || input.isEmpty() || input.length() < 6)
            return false;
        return true;
    }

    /**
     * Check field not empty
     *
     * @param input text input
     */
    public static boolean isEmpty(String input) {
        if (input == null || input.isEmpty() || ("").equals(input.trim()))
            return true;
        return false;
    }

    public static boolean isQuantity(String input) {
        float quantity = Float.parseFloat(input);
        if (quantity > 0)
            return true;
        return false;
    }
}
