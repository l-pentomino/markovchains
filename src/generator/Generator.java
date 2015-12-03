package generator;
import generator.Tokenizer.*;

import static generator.Chainme.sayln;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Generator {

    /**
     * Helper class to represent the "base" (root) of the ngram
     */
    class NgramBase {

        Token[] base;

        /**
         * Instantiates a new ngram-base of (n-1) tokens where n is a size
         * of the ngram
         * @param tokens - words or separators constituting a base
         */
        NgramBase(Token...tokens) {
            base = tokens;
        }

        /**
         * Creates a new base by discarding its leftmost token and adding the
         * newly sampled one
         * @param nextToken - the newly sampled token
         * @return new ngram base including the last token
         */
        NgramBase next(Token nextToken) {
            Token[] oldBase = base;
            base = new Token[oldBase.length];
            for (int i = 0; i < base.length - 1; i++) {
                base[i] = oldBase[i+1];
            }
            base[base.length - 1] = nextToken;
            return new NgramBase(base);
        }

        /**
         * Checks if the ngram base belongs in the beginning of the
         * sentence (starts with a capital letter)
         * (only relevant when starting a new iteration)
         * @return
         */
        boolean isStartSentence() {
            char start = base[0].str.charAt(0);
            return (start >= 'A' && start <= 'Z');
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public String toString() {
            String result = "";
            for (Token t : base) {
                result += t.type() == TType.SEPARATOR ? t.str : " " + t.str;
            }
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof NgramBase)) return false;
            NgramBase b = (NgramBase) o;
            for (int i = 0; i < base.length; i++) {
                if (!base[i].equals(b.base[i])) return false;
            }
            return true;
        }
    }

    int ngramN;
    String source;
    FrequencyHash<NgramBase, Token> frequency;

    int iterations;


    /**
     * Creates a new instance of the Generator
     * @param n - size of the ngram
     * @param iter - number of iterations
     * @param fileName - name and path to the corpus text file
     */
    public Generator(final int n, final int iter, final String fileName) {
        ngramN = n;
        iterations = iter;
        frequency = new FrequencyHash<NgramBase, Token>();

        Scanner sc = null;
        try {
            sc = new Scanner(new File(fileName));
        } catch (FileNotFoundException e) {
            sayln("File not found");
        } catch (IOException e) {
            sayln("Could not open file " + fileName);
        }
        if (sc == null) {
            sayln("FATAL: Could not locate corpus file. Terminating");
            System.exit(1);
        }

        while (sc.hasNextLine()) {
            source += " " + sc.nextLine().trim();
        }
    }

    /**
     * Tokenize text and collect word frequencies
     */
    void collect() {
        Tokenizer t = new Tokenizer();
        List<Token> allTokens = t.tokenize(source);
        for (int i = 0; i < allTokens.size() - ngramN; i++) {
            Token[] baseTokens = new Token[ngramN - 1];

            int j = i;
            for (int k = 0; j < i + ngramN - 1; j++, k++) {
                baseTokens[k] = allTokens.get(j);
            }

            NgramBase base = new NgramBase(baseTokens);

            Token next = allTokens.get(j);
            frequency.add(base, next);
        }
    }

    /**
     * Generate Markov chains, beginning with one of the sentence starters
     * @return generated text
     */
    public String generateText() {

        collect();

        String result = "";
        NgramBase start = null;

        while (start == null) {
            Random random = new Random();
            int r = random.nextInt(frequency.total) + 1;
            int total = 0;
            for (NgramBase base : frequency.all()) {
                total += frequency.getKey(base).total;
                if (total >= r && base.isStartSentence()){
                    start = base;
                    break;
                }
            }
        }

        result += start;

        NgramBase nextBase = start;

        for (int i = 0; i < iterations; i++) {
            // add the next sampled token to the text
            Token nextToken = frequency.sample(nextBase);
            result += nextToken.type() == TType.SEPARATOR ? nextToken.str
                                  : " " + nextToken.str;

            //paragraphs
            if (nextToken.str.equals(".") && result.length() % 50 == 0) {
                result += "\n\n";
            }

            // generate a new base from the last (n-1) elements of the old base
            // and the sampled token
            nextBase = nextBase.next(nextToken);
        }
        return result;
    }
}