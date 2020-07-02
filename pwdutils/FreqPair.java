package pwdutils;

public class FreqPair implements Comparable<FreqPair> {

    public String word;
    public long freq;

    public FreqPair (String word, long freq) {
        this.word = word;
        this.freq = freq;
    }

    public FreqPair (String word) {
        this(word, 0);
    }

    public int compareTo (FreqPair other) {
        if (freq > other.freq)
            return -1;
        if (freq < other.freq)
            return 1;
        return word.compareTo(other.word);
    }
}
