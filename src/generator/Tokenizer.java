package generator;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    enum TType {
        WORD, SEPARATOR;
    }


    public final String SEPARATORS = ".,?!-:;";

    class Token {
        String str;

        public Token(String s) {
            str = s;
        }

        public TType type() {
            return TType.WORD;
        }

        public int hashCode() {
            return str.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Token)) return false;
            Token t = (Token) o;
            return str.equals(t.str);
        }

        @Override
        public String toString() {
            return str;
        }
    }

    class Separator extends Token {

        public Separator(String s) {
            super(s);
        }

        @Override
        public TType type() {
            return TType.SEPARATOR;
        }
    }

    public List<Token> tokenize(String txt) {

        String word = "";

        List<Token> result = new ArrayList<Token>();

        for (char c : txt.toCharArray()) {
            if (Character.isLetter(c) || Character.isDigit(c)
                            || "()'`".contains(c + "")) {
                word += c;
                continue;
            } else if (word != "") {
                result.add(new Token(word));
                word = "";
            }

            if (SEPARATORS.contains(c + "")) {
                result.add(new Separator(c + ""));
            }
        }
        return result;
    }
}
