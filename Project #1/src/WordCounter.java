import java.util.Comparator;

import java.util.Iterator;
import components.map.Map;
import components.map.Map1L;
import components.set.Set;
import components.set.Set1L;
import components.queue.Queue;
import components.queue.Queue1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Program that counts word occurences in txt file and html file.
 *
 * @author Heeji Seol
 */
public final class WordCounter {

    /**
     * Default constructor--private to prevent instantiation.
     */
    private WordCounter() {
    }

   /**
    * Creates the html page/file that stores the table of words
    * and the number of occurences.
    * @param out
    the output file that is written to with {@code SimpleWriter}
    @param wordMap
    the {@code Map} containing all the words as keys and
    the occurences as the value associated.
    @param sortOrder
    the {@code Queue} containing the {@code wordMap} keys
    @param fileName
    the text input file
    @clears {@code key}, {@code wordMap} @ensures <pre>
    {@code HTML code table elements = entries </pre>
     */
    public static void tableFormat(String fileName, Queue<String> sortOrder,
            Map<String, Integer> wordMap, SimpleWriter out) {
        assert wordMap != null : "Violation of: wordMap is not null";
        assert sortOrder != null : "Violation of: terms is not null";
        assert out.isOpen() : "Violation of: out is open";

     // Prints out opening tags
        out.println("<?xml version='1.0' encoding='ISO-8859-1' ? >");
        out.println("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Strict//EN' "
                + " 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd'>");
        out.println("<html xmlns='http://www.w3.org/1999/xhtml'>");
        out.println("<head>");
        out.println("<meta http-equiv='Content-Type'"
                + " content='text/html; charset=ISO-8859-1' />");
        out.println("</head>");
        out.println("<body background=\"http://boomoy.files.wordpress.com/"
                + "2011/09/white-snow-background-1.jpeg\">");
        out.println("<h2>Word Counter</h2>");
        out.println("<h3>Filename: " + fileName + "</h3>");

        //Title
        out.println("<title>Word Counter</title>");

        // Tabling the words with counts in occurences
        int j = 0;
        out.println("<table border = '1'>"); out.println("<tr>");
        out.println("<th>Word</th>");
        out.println("<th>Word Count</th>");
        out.println("</tr>");
        int length = sortOrder.length();
        while (j < length) {
                    String termName = sortOrder.dequeue();
                    out.println("<tr>");
                    out.println("<td>" + termName + "</td>");
                    out.println("<td>" + wordMap.value(termName) + "</td>");
                    out.println("</tr>");
                    j++;
        }
        out.println("</table>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Generates words (Strings) & word counts (integers) in its occurences
     * through input textfile {@code file} into the given {@code Map}.
     * words do not have particular order (@code Set}
     * @param inputFile
     *          the input text file {@code SimpleReader}
     * @words
     *          {@code Map} contains the words and its occurences
     * @param wordMap
     *          {@code Map}
     * @updates {@code words}
     * @inputFile's name
     *          {@code Map}'s Key(words) & Value(occurences)
     */
    public static void generateWords(SimpleReader inputFile,
            Map<String, Integer> wordMap) {
        assert inputFile.isOpen() : "Violation of: InputFile is open";
        assert wordMap != null : "Violation of: wordMap is not null";

     // Delete the whole elements in wordMap
        wordMap.clear();

        int strPosition = 0;
        //separators characters defined
        Set<Character> separatorChr = new Set1L<Character>();
        final String separators = "\t,.-~: ";
        generateSeparators(separators, separatorChr);

        // lines of inputFile through the loop (iteration)
        while (!inputFile.atEOS()) {
            strPosition = 0;
            String line = inputFile.nextLine();
            while (strPosition < line.length()) {
                String next = nextWordOrSeparator(line, strPosition, separatorChr);
                // if next is word
                if (!separatorChr.contains(next.charAt(0))) {
                    // get word if Map doesn't have it, count it up
                    if (!wordMap.hasKey(next)) {
                        wordMap.add(next, 1);
                        }
                    // if Map has it, get the value and count it up with its occurences
                    else {
                        int wordCount = 0;
                        wordCount = wordMap.value(next);
                        wordCount++;
                        wordMap.replaceValue(next, wordCount);
                    }
                }
                strPosition += next.length();
            }
        }
    }


    /**
     * Sorts {@code q} according to the ordering provided by the {@code compare}
     * method from {@code order}.
     * @param wordMap
     *            the word map
     * @requires [the relation computed by order.compare is a total preorder]
     * @ensures q = [#q ordered by the relation computed by order.compare]
     * @return sort of order from alphabetical sort from the Map
     */
    public static Queue<String> alphabeticalSort(Map<String, Integer> wordMap) {
        Queue<String> sortOrder = new Queue1L<>();

        Iterator<Map.Pair<String, Integer>> words = wordMap.iterator();
        while (words.hasNext()) {
            String word = words.next().key();
            sortOrder.enqueue(word);
        }

        return sortOrder;
    }

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     * @param str
     *            the given {@code String}
     * @param charSet
     *            the {@code Set} to be replaced
     * @replaces charSet
     * @ensures charSet = entries(str)
     */
    private static void generateSeparators(String str, Set<Character> charSet) {
        assert str != null : "Violation of: str is open";
        assert charSet != null : "Violation of: charSet is not null";

        int count = 0;
        char piece = 'a';

        charSet.clear();

        while (count < str.length()) {
            if (!charSet.contains(str.charAt(count))) {
                piece = str.charAt(count);
                charSet.add(piece);
            }
            count++;
        }
    }
    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        int count = 0;
        char returnedPiece = 'a';
        String returned = "";
        // if separators contains index of "position"
        if (separators.contains(text.charAt(position))) {
            // loop for getting length from position to text length
            while (count < text.substring(position, text.length()).length()) {
                returnedPiece = text.charAt(position + count);
                // if separators contains index of "position + count"
                if (separators.contains(text.charAt(position + count))) {
                    returned += returnedPiece;
                    count++;
                    }
                // if separators does not contain index of "position + count"
                else {
                    count = text.substring(position, text.length()).length();
                }
            }
            count = 0;
            }
        // if separators does not contains index of "position"
        else {
         // loop for getting length from position to text length
            while (count < text.substring(position, text.length()).length()) {
                returnedPiece = text.charAt(position + count);
             // if separators does not contains index of "position + count"
                if (!separators.contains(text.charAt(position + count))) {
                    returned += returnedPiece;
                    count++;
                    }
             // if separators contains index of "position + count"
                else {
                    count = text.substring(position, text.length()).length();
                }
            }
            count = 0;
        }
        return returned;
    }

    /**
     ** Compares Strings regardless of case using a string comparator.
     */
    public static class StringCom implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }
    }

    /**
     * Return a string representation.
     * @return a string representation of the Comparator
     */
    private static class StringLT implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            // Compare the words insensitively
            return (o1.toLowerCase()).compareTo(o2.toLowerCase());
        }

    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        Queue<String> sortOrder;
        Comparator<String> cs = new StringLT();

        Map<String, Integer> words = new Map1L<String, Integer>();

        // Ask for user to input file
        out.println("Please Enter the input file: ");
        String title = in.nextLine();
        SimpleReader inputFile = new SimpleReader1L(title);

        // Ask for user to output file
        out.println("Please Enter the output file: ");
        String outputFile = in.nextLine();

        generateWords(inputFile, words);
        sortOrder = alphabeticalSort(words);
        sortOrder.sort(cs);

        SimpleWriter index = new SimpleWriter1L(outputFile + "/index.html");
        tableFormat(title, sortOrder, words, index);

        index.close();
        in.close();
        out.close();
        inputFile.close();
    }

}