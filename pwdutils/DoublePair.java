/**
 * Implements a word pair with log-probability.
 */

package pwdutils;

public class DoublePair implements Comparable<DoublePair> {

    public String word;
    public double prob;

    public DoublePair (String word, double prob) {
        this.word = word;
        this.prob = prob;
    }

    /**
     * Sorting DoublePair will get a descending order array.
     */
    public int compareTo (DoublePair other) {
        if (prob > other.prob)
            return -1;
        if (prob < other.prob)
            return 1;
        return word.compareTo(other.word);
    }
}
