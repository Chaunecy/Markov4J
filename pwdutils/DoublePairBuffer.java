package pwdutils;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * Writes password to disk in descending order of probability, using a
 * filesystem buffer.
 *
 * @author cw
 */

public class DoublePairBuffer {

    private static final int DEFAULT_BUF_SIZE = 16777215;
    private static final String EXT = ".tmp";

    private final int bufferSize;
    private final File bufferDir;
    private final DoublePair[] buffer;
    private int numBuffers;
    private int curBufferSize;

    public DoublePairBuffer(File bufferDir) {
        this(bufferDir, DEFAULT_BUF_SIZE);
    }

    public DoublePairBuffer(File bufferDir, int bufferSize) {
        this.bufferSize = bufferSize;
        this.bufferDir = bufferDir;
        this.bufferDir.mkdirs();
        for (File f : this.bufferDir.listFiles()) {
            f.delete();
        }
        this.buffer = new DoublePair[this.bufferSize];
        for (int i = 0; i < this.buffer.length; i++) {
            buffer[i] = new DoublePair(null, 0.0);
        }
        this.numBuffers = 0;
        this.curBufferSize = 0;
    }

    public void insert(String word, double prob) {
        DoublePair dp = buffer[curBufferSize++];
        dp.word = word;
        dp.prob = prob;
        if (curBufferSize >= bufferSize) {
            flush();
        }
    }

    /**
     * Merge buffered files with a merge sort.	---lzg
     * Need to modify
     *
     * @param outFile
     */
    public void write(File outFile) throws IOException {

        int len = numBuffers;
        int count = 1;
        File[] files = new File[len];
        for (int i = 0; i < len; i++) {
            files[i] = getBufferFile(i);
        }
        //两两合并到files[0]
        while (files.length > 1) {
            files = mergetsv(files, count + "");
            count++;
        }

        if (outFile.exists()) {
            Files.delete(outFile.toPath());
        }
        //renameTo的目标文件如果存在会发生错误，所以需要先删除
        if (!files[0].renameTo(outFile)) {
            throw new IOException("cannot generate guesses");
        }
    }

    /**
     * (Repeat) merge the files
     *
     * @return file names after merge
     */
    private File[] mergetsv(File[] files, String addString) throws IOException {
        int size = files.length;
        if (size == 0) {
            return files;
        }

        if (size == 1) {
            return files;
        }

        int num = size / 2;
        int i;
        for (i = 0; i < num; i++) {
            mergeTwoTsv(files[2 * i], files[2 * i + 1], new File(bufferDir, i + EXT + addString));
        }

        if (size % 2 == 1) {
            File renameFile = new File(bufferDir, i + EXT + addString);
            if (renameFile.exists()) {
                Files.delete(renameFile.toPath());
            }
            if (!files[size - 1].renameTo(renameFile)) {
                throw new IOException("failed to rename file");
            }
            i++;
        }

        File[] retFile = new File[i];
        for (int j = 0; j < i; j++) {
            retFile[j] = new File(bufferDir, j + EXT + addString);
        }

        return retFile;
    }

    private void mergeTwoTsv(File f1, File f2, File targetFile) {
        try {
            BufferedReader br1 = new BufferedReader(new FileReader(f1));
            BufferedReader br2 = new BufferedReader(new FileReader(f2));
            BufferedWriter bw = new BufferedWriter(new FileWriter(targetFile));
            String line1 = br1.readLine();
            String line2 = br2.readLine();

            while ((line1 != null) && (line2 != null)) {
                if (line1.length() == 0) {
                    line1 = br1.readLine();
                    continue;
                }

                if (line2.length() == 0) {
                    line2 = br2.readLine();
                    continue;
                }

                double d1 = Double.parseDouble(line1.split("\t")[1]);
                double d2 = Double.parseDouble(line2.split("\t")[1]);

                if (d1 > d2) {
                    bw.write(line1);
                    bw.newLine();
                    line1 = br1.readLine();
                } else {
                    bw.write(line2);
                    bw.newLine();
                    line2 = br2.readLine();
                }
            }

            while (line1 != null) {
                if (line1.length() == 0) {
                    line1 = br1.readLine();
                    continue;
                }
                bw.write(line1);
                bw.newLine();
                line1 = br1.readLine();
            }

            while (line2 != null) {
                if (line2.length() == 0) {
                    line2 = br2.readLine();
                    continue;
                }
                bw.write(line2);
                bw.newLine();
                line2 = br2.readLine();
            }

            bw.close();
            br1.close();
            br2.close();
//
            f1.delete();
            f2.delete();
        } catch (Exception ignored) {
        }
    }

    public File getBufferFile(int id) {
        return new File(bufferDir, id + EXT);
    }

    /**
     * 不清楚为什么不能直接输出Math.exp(buffer[i].prob)
     * 这里将Pwd\tlog(prob)写入文件。后面单独用方法写成pwd\tprob\count形式。
     */
    public void flush() {
        /*flush多次无影响，因为curBufferSize=0之后，下一次这里直接返回*/
        if (curBufferSize < 1) {
            return;
        }
        Arrays.sort(buffer, 0, curBufferSize);
        try (BufferedWriter writer = new BufferedWriter((new FileWriter(getBufferFile(numBuffers))))) {
            for (int i = 0; i < curBufferSize; i++) {
                //2016.7.2因为这里设置了0.8f所以如果exp之后，很多小概率会只显示0.应该把printf改成println
                writer.write(buffer[i].word + "\t" + buffer[i].prob);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        curBufferSize = 0;
        numBuffers++;
    }

    /**
     * change markov/pcfg guess result(pwd\tprob(log)) to pwd\tprob\count
     * and make the sum of count equal to targetNum
     *
     * @throws IOException
     */
    private static void unique2total(File file, File outFile) throws IOException {


        BufferedReader br = new BufferedReader(new FileReader(file));
        BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
        String line;
        String[] split;
        while ((line = br.readLine()) != null) {
            if (line.length() > 0) {
                split = line.split("\t");
                if (split.length == 2) {
                    bw.write(split[0] + "\n");
                } else {
                    System.out.println("Format error: length!=2");
                }
            } else {
                System.out.println("Format error: length=0");
            }
        }
        br.close();
        bw.close();
        file.delete();
    }
}
