package net.year4000.servermenu;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.StringTokenizer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Commons {
    /** Split the string and limit the chars in a line */
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
