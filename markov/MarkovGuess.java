
package markov;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import pwdutils.DataSet;

/**
 * @author cw
 */
public class MarkovGuess {

    public static void main(String[] args) throws IOException {
        int minLen = 4;
        MCEndSymbol mc = new MCEndSymbol(3);
        String csdnSrc = "/home/cw/Codes/Python/PwdTools/corpora/src/csdn-src.txt";
        String csdnTar = "./hello.txt";
        DataSet train = loadDataSet(new File(csdnSrc));
        mc.train(train);
        mc.generate(new File(csdnTar), 10000, minLen);
    }

    public static DataSet loadDataSet(File trainFile) throws IOException {
        DataSet dataSet;
        try (BufferedReader in = new BufferedReader(new FileReader(trainFile))) {
            dataSet = new DataSet();
            String line;

            while ((line = in.readLine()) != null) {
                if (line.length() > 0) {
                    int tabIndex = line.lastIndexOf('\t');
                    if (tabIndex < 0) {
                        dataSet.add(line, 1);
                    } else {
                        dataSet.add(line.substring(0, tabIndex),
                                Long.parseLong(line.substring(tabIndex + 1)));
                    }
                }
            }
        }
        dataSet.finalizeData();
        return dataSet;
    }
}
