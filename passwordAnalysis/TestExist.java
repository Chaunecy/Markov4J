package passwordAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class TestExist {

	public static void main(String[] args) throws NumberFormatException,
			IOException {
		
		compareWith("csdn_1sixth_markovGuessCSDN_equal2","csdn_1sixth_markovGuessCSDN_equal");
	}

	/**
	 * filename:file to test real:the real file output:
	 * filename_more:passwd\tmodelcount\trealcount
	 * filename_less:passwd\tmodelcount\trealcount
	 * filename_equal:passwd\tmodelcount\trealcount
	 */
	public static void compareWith(String filename, String real)
			throws NumberFormatException, IOException {
		/* read testFile to guessmap */
		HashMap<String, Integer> realmap = new HashMap<String, Integer>();
		BufferedReader br = new BufferedReader(new FileReader(new File(real
				+ ".txt")));
		String line = null;
		String passwd;
		String[] split;
		Integer count=0;
		while ((line = br.readLine()) != null) {
			if (line.length() > 0) {
				split = line.split("\t");
				passwd = split[0];

				if (split.length == 1) {
					count =1;
				}else{
					count  = Integer.parseInt(split[1]);
				}
				if (realmap.get(passwd) == null) {
					realmap.put(passwd, count);
				} else {
					realmap.put(passwd, realmap.get(passwd) + count);
				}
			}
		}
		br.close();

		BufferedWriter bwMore = new BufferedWriter(new FileWriter(new File(
				filename + "_more.txt")));
		BufferedWriter bwEqual = new BufferedWriter(new FileWriter(new File(
				filename + "_equal.txt")));
		BufferedWriter bwLess = new BufferedWriter(new FileWriter(new File(
				filename + "_less.txt")));

		br = new BufferedReader(new FileReader(new File(filename + ".txt")));
		Integer curNum = 0;
		while ((line = br.readLine()) != null) {
			split = line.split("\t");
			if (split.length >= 2) {
				curNum = Integer.parseInt(split[1]);
				Integer x = realmap.get(split[0]);
				if (x != null) {
					if (curNum.compareTo(x)>0) {
						bwMore.write(line + "\t" + x);
						bwMore.newLine();
					} else if (curNum.equals(x)) {
						bwEqual.write(line + "\t" + x);
						bwEqual.newLine();
					} else if (curNum.compareTo(x)<0) {
						bwLess.write(line + "\t" + x);
						bwLess.newLine();
					}
					
				} else {
					bwMore.write(line + "\t0");
					bwMore.newLine();
				}
				realmap.put(split[0], null);
			} else {
				System.out.println("Format error: length!=2");
			}
		}
		br.close();
		bwMore.close();
		bwEqual.close();
		
		/*输出hashmap剩余的*/
		Iterator<Entry<String, Integer>> iter = realmap.entrySet().iterator(); 
		while (iter.hasNext()) { 
			Map.Entry<String,Integer> entry = (Entry<String, Integer>) iter.next();
			if(entry.getValue()!=null){
				bwLess.write(entry.getKey()+"\t0\t"+entry.getValue());
				bwLess.newLine();
			}
		}
		bwLess.close();
	}
}
