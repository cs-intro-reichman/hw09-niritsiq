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
        char a;
        String window = "";
        In in = new In(fileName);
        for (int i = 1; i <= windowLength; i++) {
            if (!in.hasNextChar()) {
                return;
            }
            window += in.readChar();
        }
        while (!in.isEmpty()) {
            a = in.readChar();
            List prob = CharDataMap.get(window);
            if (prob == null) {
                prob = new List();
                CharDataMap.put(window, prob);
            }
            prob.update(a);
            window += a;
            window = window.substring(1, window.length()); // moving the current window 1 forward
        }

        for (String key : CharDataMap.keySet()) {
            List prob = CharDataMap.get(key);
            calculateProbabilities(prob);
        }
    }

    // Computes and sets the probabilities (p and cp fields) of all the
    // characters in the given list. */
    public void calculateProbabilities(List probs) {
        double num = 0.0;
        double current = 0.0;
        for (int i = 0; i < probs.getSize(); i++) {
            num += probs.get(i).count;
        }
        for (int i = 0; i < probs.getSize(); i++) {
            probs.get(i).p = probs.get(i).count / num;
            probs.get(i).cp = current + probs.get(i).p;
            current = probs.get(i).cp;
        }

    }

    // Returns a random character from the given probabilities list.
    public char getRandomChar(List probs) {
        // Your code goes here
        Double r = randomGenerator.nextDouble();
        for (int i = 0; i < probs.getSize(); i++) {
            if (r <= probs.get(i).cp) {
                return probs.get(i).chr;
            }
        }
        return 0;

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
        if (initialText.length() >= windowLength) {
            for (int i = 0; i < textLength; i++) {
                initialText += getRandomChar(
                        CharDataMap.get(initialText.substring(initialText.length() - windowLength)));
            }
        }
        return initialText;
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
