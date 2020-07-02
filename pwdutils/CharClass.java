/**
 * There are five character classes: d for digit, e for end, u for uppercase letters, l for lowercase letters an d for symbols.
 */
package pwdutils;

import static pwdutils.Constants.*;

import java.util.ArrayList;
import java.util.HashSet;

public class CharClass {

    private static final CharClass[] reprMap = new CharClass[65536];
    private static final CharClass[] charMap = new CharClass[65536];		// 65536 is equal to 2^16.
    private static final ArrayList<CharClass> classes = new ArrayList<CharClass>();
    public static final char [] specials;

    static {
        for (char c : ALLCHARS) {
            if (Character.isDigit(c))
                addToCharClass(c, 'd');
            else if (c == ENDCHAR)
                addToCharClass(c, 'e');
            else if (Character.isUpperCase(c))
                addToCharClass(c, 'u');
            else if (Character.isLowerCase(c))
                addToCharClass(c, 'l');
            else
                addToCharClass(c, 's');
        }
        specials = new char[reprMap['s'].chars.size()];
        int i = 0;
        for (char c : reprMap['s'].chars)
            specials[i ++] = c;
    }

    public final char repr;
    public final HashSet<Character> chars;

    private CharClass (char repr) {
        this.repr = repr;
        this.chars = new HashSet<Character>(200, 0.1F);
    }

    public static CharClass getCharClass (char c) {
        CharClass charClass = charMap[c];
        if (charClass == null)
            return addToCharClass(c, 's');
        return charClass;
    }

    public static CharClass getCharClassFromRepr (char c) {
        return reprMap[c];
    }

    public static ArrayList<CharClass> getCharClasses () {
        return classes;
    }

    public static CharClass addToCharClass (char c, char charClass) {
        if (reprMap[charClass] == null) {
            reprMap[charClass] = new CharClass(charClass);
            classes.add(reprMap[charClass]);
        }

        reprMap[charClass].chars.add(c);
        charMap[c] = reprMap[charClass];
        return reprMap[charClass];
    }

    public static String getCharClassRepr (String str) {
        char [] ret = new char[str.length()];
        for (int i = 0; i < ret.length; i ++)
            ret[i] = getCharClass(str.charAt(i)).repr;
        return new String(ret);
    }
}
