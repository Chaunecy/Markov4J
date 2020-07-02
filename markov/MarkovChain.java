package markov;

import java.util.*;

import static pwdutils.Constants.*;

/**
 * @author cw
 */
public class MarkovChain {

    public final String state;
    public Map<Character, Long> transitions;
    public Map<Character, Double> logTransitions;
    public long totalTransition;
    public double smoothingProb;

    public MarkovChain(String state) {
        this.state = state;
        transitions = new HashMap<>(2, 0.75F);
    }

    public void addTransition(char transition, long freq) {
        totalTransition += freq;
        Long prev = transitions.get(transition);
        transitions.put(transition, freq + (prev == null ? 0 : prev));
    }

    public void generateLogTransitions(double smoothing, int totalNumChars) {
        double total = totalTransition + smoothing * totalNumChars;
        logTransitions = new HashMap<>(transitions.size() + 1);
        for (Map.Entry<Character, Long> entry : transitions.entrySet()) {
            logTransitions.put(entry.getKey(), Math.log((entry.getValue() + (ALLCHARSET.contains(entry.getKey()) ? smoothing : 0.0)) / total));
        }
        transitions = null;

        if (smoothing > EPSILON) {
            smoothingProb = Math.log(smoothing / total);
        }
    }

    public long getSize() {
        return 48 + state.length() * 2 + Math.max(60, Math.round(26 * 1.33 * (logTransitions == null ? transitions.size() : logTransitions.size())));
    }
}
