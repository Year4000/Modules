package net.year4000.hubitems.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.StringTokenizer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Common {
    public static double manaConverter(float mana) {
        return (double) mana * .10;
    }


    public static String[] loreDescription(String string) {
        return splitIntoLine(string, 30);
    }

    public static String[] splitIntoLine(String input, int maxCharInLine){

        StringTokenizer tok = new StringTokenizer(input, " ");
        StringBuilder output = new StringBuilder(input.length());
        int lineLen = 0;
        while (tok.hasMoreTokens()) {
            String word = tok.nextToken();

            while(word.length() > maxCharInLine){
                output.append(word.substring(0, maxCharInLine-lineLen) + "\n");
                word = word.substring(maxCharInLine-lineLen);
                lineLen = 0;
            }

            if (lineLen + word.length() > maxCharInLine) {
                output.append("\n");
                lineLen = 0;
            }
            output.append(word + " ");

            lineLen += word.length() + 1;
        }
        // output.split();
        // return output.toString();
        return output.toString().split("\n");
    }

}
