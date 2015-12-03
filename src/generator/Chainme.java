/**
 * Date: 12/1/15
 */
package generator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Chainme {
    static String help = "Usage:\nChainme\tf=<corpus-file-name> n=<ngram-size> i=<iterations>" +
                                     "\n\thelp";

    public static void main(String[] args) throws Exception {

        if (args.length < 3) {
            if ((args.length == 0) || (args.length == 1 && args[0].equals("help"))) {
                sayln(help);
            } else {
                sayln("Invalid arguments. " + help);
            }
            return;
        }

        String[] arg = args[0].split("=");
        String fileName = ".";
        if (arg.length == 2) fileName = arg[1].trim();
        else {
            sayln("Invalid argument: " + args[0] + "\n" + help);
            return;
        }

        arg = args[1].split("=");
        int N = 3;
        if (arg.length == 2) N = Integer.parseInt(arg[1].trim());

        arg = args[2].split("=");
        int iterations = 10000;
        if (arg.length == 2) iterations = Integer.parseInt(arg[1].trim());

        sayln(fileName);
        File out = new File(fileName.split("\\.")[0] + "_out.txt");

        try {
            if (!out.exists()) out.createNewFile();
        }
        catch (IOException e) {
            sayln("Failed to create output file; writing to console");
            out = null;
        }

        PrintWriter pw = out == null ? null : new PrintWriter(out);

        Generator gen = new Generator(N, iterations, fileName);

        String text = gen.generateText();

        if (pw != null) {
            pw.write(text);
            pw.close();
        }
    }

    static void say(Object o) {
        System.out.print(o);
    }

    static void sayln(Object o) {
        System.out.println(o);
    }
}
