package pwdutils;

import java.util.*;

public class DataSet {
    
    public final HashMap<String, FreqPair> passwordMap;
    public final ArrayList<FreqPair> passwordList;
    private boolean finalized;
    
    public DataSet (int initialCapacity) {
        passwordMap = new HashMap<String, FreqPair> (initialCapacity);
        passwordList = new ArrayList<FreqPair> ();
        finalized = false;
    }

    public DataSet () {
        this(1000);
    }
    
    public void add (String word, long freq) {
        if (word.length() < 4 || word.length() > 40)
            return;
        for (char c : word.toCharArray()) {
            if (Character.isISOControl(c) || c >= 128) // ascii printable check
                return;
        }
        finalized = false;
        FreqPair fp = passwordMap.get(word);
        if (fp == null) {
            fp = new FreqPair(word, freq);
            passwordList.add(fp);
            passwordMap.put(word, fp);
        } else {
            fp.freq += freq;
        }
    }

    public long getSize () {
        return passwordList.size();
    }

    public long getTotalSize () {
        long size = 0;
        for (FreqPair fp : passwordList) {
            size += fp.freq;
        }
        return size;
    }
    
    public void finalizeData () {
        if (!finalized) {
            finalized = true;
            Collections.sort(passwordList);
        }
    }
}
