package markov;

import java.util.*;

import static pwdutils.Constants.*;

import pwdutils.*;

import java.io.*;

/**
 * @author cw
 */
public class MCEndSymbol {

    private static final double OVERSHOOT = 2.0;
    private static final double MULT = 8.0;

    private final int order;
    private HashMap<String, MarkovChain> chains;

    public MCEndSymbol(int order) {
        this.order = order;
    }

    public void train(DataSet trainSet) {        // trainSet should contain tab separated values.
        chains = new HashMap<>();
        /* train data. */
        for (FreqPair fp : trainSet.passwordList) {
            String password = fp.word + ENDCHAR;
            long freq = fp.freq;

            for (int i = 0, len = password.length(); i < len; i++) {
                String chainState = password.substring(Math.max(0, i - order), i);
                MarkovChain chain = chains.get(chainState);
                if (chain == null) {
                    chain = new MarkovChain(chainState);
                    chains.put(chainState, chain);
                }
                chain.addTransition(password.charAt(i), freq);
            }
        }

        /* Generate log transitions. */
        for (MarkovChain chain : chains.values()) {
            chain.generateLogTransitions(0.0, ALLCHARS.length);
        }

        /* Debug mode. */
        if (Constants.debug) {
            System.out.println("chain size: " + chains.size());
        }

    }

    /**
     * control 1 to raise threshold, -1 to lower, 0 to signal done.
     * 此处的minLength只是限制最后的输出，不会影响概率模型里任何概率大小。
     */
    public void generate(File output, long targetNumber, int minLength) {
        double lo = 0.0;
        double hi = 1.0;
        int control = -1;
        while (control != 0) {
            double mid = (lo + hi) / 2;
            if (control < 0) {
                mid = lo + (hi - lo) / MULT;
            }

            double threshold = Math.log(mid);
            control = tryIterate(output, targetNumber, threshold, minLength);

            if (control > 0) {
                lo = mid;
            } else if (control < 0) {
                if (lo < 1E-100 && prevGenerated * 5 > targetNumber) {
                    double target = targetNumber * 1.6;
                    double ratio = target / prevGenerated;
                    double targetProb = mid / ratio;
                    hi = MULT * targetProb;
                } else {
                    hi = mid;
                }
            }
        }
    }

    /**
     * 虽然有4-40的长度限制，但是只是限制输出，不影响单个口令的概率
     */
    private class MarkovTraversal {
        //2016.5.21 STACK_LIM的最后一项为ENDCHAR,总char数为maxLen+1是允许的，所以STACK_LIM=maxLen+1
        private final int stackLim = LEN_LIMIT + 1;
        private final long maxAllowed;
        private final long targetNumber;
        private final double threshold;
        private final char[] stack;
        private final DoublePairBuffer buffer;
        private long generated;
        private boolean stoppedEarly;

        public MarkovTraversal(long targetNumber, double thresholdLo, DoublePairBuffer buffer) {
            this.threshold = thresholdLo;
            this.targetNumber = targetNumber;
            /*每个distinct的口令数目远小于maxAllowed。
             * 所以generated按照非distinct个数累加时，不用考虑加过头的问题。*/
            this.maxAllowed = Math.round(targetNumber * OVERSHOOT);
            this.stack = new char[stackLim];
            this.buffer = buffer;
            this.generated = 0;//从distinct的记数改成非distinct的
            this.stoppedEarly = false;
        }

        //stack中已经有了len个元素，现在考察index为len的情况
        private void recurse(int len, double curProb, int minLength) {
            if (generated > maxAllowed || curProb < threshold) {
                stoppedEarly = true;
                return;
            }

            /*en=STACK_LIM是可以存在的，len=STACK_LIM时不return,
             * 若到下面stack[len - 1] == ENDCHAR，刚好就是ENDCHAR在占满STACK最后一位的情况；
             * 若到下面不是stack[len - 1] == ENDCHAR，则在下一个循环里return就好了
             */
            if (len >= stackLim) {
                return;
            }

            if (len > 0 && stack[len - 1] == ENDCHAR) {
                if (len > minLength) {
                    if (buffer != null) {
                        buffer.insert(new String(stack, 0, len - 1), curProb);
                    }
//					generated++;//for unique
                    generated += 1;
                }
                return;
            }
            /* Not generated a end string, continue to fill the stack. */
            int from = Math.max(0, len - order);
            MarkovChain chain = chains.get(new String(stack, from, len - from));        // String(char[] value, int offset, int count)
            if (chain == null) {
                return;
            }

            for (Map.Entry<Character, Double> transition : chain.logTransitions.entrySet()) {
                stack[len] = transition.getKey();
                recurse(len + 1, curProb + transition.getValue(), minLength);
            }
        }
    }

    private long prevGenerated = 0;

    private int tryIterate(File output, long targetNumber, double threshold, int minLength) {
        File tmpDir = new File(Constants.workingDir, "tmp-buffer");
        DoublePairBuffer buffer = new DoublePairBuffer(tmpDir, (int) 1E6);
        MarkovTraversal mct = new MarkovTraversal(targetNumber, threshold, buffer);

        if (Constants.debug) {
            System.out.println("Trying threshold " + threshold);
        }

        long startTime = System.currentTimeMillis();
        mct.recurse(0, 0, minLength);

        if (Constants.debug) {
            System.out.printf("%d passwords generated with threshold in %s\n", mct.generated, operateTime(System.currentTimeMillis() - startTime));
        }

        prevGenerated = mct.generated;

        if (mct.generated > mct.maxAllowed) {
            if (Constants.debug) {
                System.out.println("Too many password generated.");
            }
            return 1;
        }

        if (mct.generated >= targetNumber || !mct.stoppedEarly) {
            if (Constants.debug) {
                System.out.println("Stopping");
            }

            startTime = System.currentTimeMillis();

            buffer.flush();
            try {

                buffer.write(output);
            } catch (IOException e) {
                System.exit(-1);
            }
            System.out.printf(" flush and write Stopped in %s\n", operateTime(System.currentTimeMillis() - startTime));

            return 0;
        }

        if (Constants.debug) {
            System.out.println("Not enough passwords generated.");
        }

        return -1;
    }
}
