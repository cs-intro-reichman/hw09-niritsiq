import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;

    // The window length used in this model.
    int windowLength;

    // The random number generator used by this model.
    private Random randomGenerator;

    /**
     * Constructs a language model with the given window length and a given
     * seed value. Generating texts from this model multiple times with the
     * same seed value will produce the same random texts. Good for debugging.
     */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /**
     * Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production.
     */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
    public void train(String fileName) {
        // Your code goes here
        char ch;
        In input = new In(fileName);
        for (int i = 0; i < winLength; i++)
            windowBuf.append(input.readChar());
        String window = windowBuf.toString();
        while (!input.isEmpty()) {

            ch = input.readChar();
            List charProbs = CharDataMap.get(window);
            if (charProbs == null) {

                charProbs = new List();
                CharDataMap.put(window, charProbs);
            }
            charProbs.update(ch);
            window = window.substring(1) + ch;
        }
        for (List probs : CharDataMap.values())
            calculateProbabilities(probs);

    }

    // Computes and sets the probabilities (p and cp fields) of all the
    // characters in the given list. */
    public void calculateProbabilities(List probs) {
        // Your code goes here
        double p = 0;
        double cp = 0;
        ListIterator iterator = probs.listIterator(0);

        double numChars = 0;
        while (iterator.hasNext()) {

            numChars = numChars + iterator.current.cp.count;
            iterator.next();
        }
        iterator = probs.listIterator(0);
        while (iterator.hasNext()) {
            p = 0;
            p = (iterator.current.cp.count / numChars);

            iterator.current.cp.p = p;
            cp += p;
            iterator.current.cp.cp = cp;
            iterator.next();
        }
    }

    // Returns a random character from the given probabilities list.
    public char getRandomChar(List probs) {
        // Your code goes here
        int i = 0;
        double num = randomGenerator.nextDouble();
        while ((probs.listIterator(i).current.cp.cp < num))
            i++;
        return probs.get(i).chr;
    }

    /**
     * Generates a random text, based on the probabilities that were learned during
     * training.
     * 
     * @param initialText     - text to start with. If initialText's last substring
     *                        of size numberOfLetters
     *                        doesn't appear as a key in Map, we generate no text
     *                        and return only the initial text.
     * @param numberOfLetters - the size of text to generate
     * @return the generated text
     */
    public String generate(String initialText, int textLength) {
        // Your code goes here
        String st = "";
        if (initialText.length() < winLength)
            return initialText;

        String wL = initialText.substring(initialText.length() - winLength);
        st = wL;

        while (st.length() < initialText.length() + textLength) {
            List l1 = CharDataMap.get(wL);
            if (l1 == null)
                return st;
            char charRand = getRandomChar(l1);
            st += charRand;
            wL = st.substring(st.length() - winLength);
        }
        return st;
    }

    /** Returns a string representing the map of this language model. */
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (String key : CharDataMap.keySet()) {
            List keyProbs = CharDataMap.get(key);
            str.append(key + " : " + keyProbs + "\n");
        }
        return str.toString();
    }

    public static void main(String[] args) {
        // Your code goes here
        int winLength = Integer.parseInt(args[0]);
        String initialText = args[1];
        int r = Integer.parseInt(args[2]);
        Boolean check = args[3].equals("random");
        String file = args[4];

        LanguageModel model;
        if (check)
            model = new LanguageModel(winLength);
        else
            model = new LanguageModel(winLength, 20);
        model.train(file);

        System.out.println(model.generate(initialText, r));
    }
}
