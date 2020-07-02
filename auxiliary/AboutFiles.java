package auxiliary;

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

import pwdutils.Constants;

/**
 * 一些读写文件的常用方法
 * getByRank、counttotal、compareTwoFiles、mergeFiles、checkPwd
 * @author yys
 *
 */
public class AboutFiles {

	public static void main(String[] args) throws Exception {

//		 String dataSet = "csdn";
//		 String filename = "csdn_1tenth";
			
//		
//		compareTwoFiles("yahoo_1third-1_pcfg2","yahoo_1third-1_pcfg1","3");
//		checkPwd("yahoo");
		compareTwoFiles1("csdn_1sixth-1_markov","csdn_1sixth-1_markov6");
	}

	/**
	 * get top n lines of the filename
	 * @param filename
	 * @param top
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private static void getByRank(String filename,int top) throws IOException {
		System.out.println("get top "+top+" of " + filename);
		BufferedReader br = new BufferedReader(new FileReader(new File(filename
				+ ".txt")));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename+top+ ".txt")));
		int rank = 1;
		String tmp;
		while ((tmp = br.readLine()) != null) {
			if(tmp.length()>0){
				if (rank>top) {
					break;
				}
				bw.write(tmp);
				bw.newLine();
				rank++;
			}
		}
		br.close();
		bw.close();
	}
	
	/**
	 * check the validation of pwd in filename
	 * @param filename
	 * @throws IOException
	 */
	private static void checkPwd(String filename) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(new File(filename
				+ ".txt")));

		String tmp;
		
		while ((tmp = br.readLine()) != null) {
			if(tmp.length()>0){
				if(!Constants.checkPasswd(tmp)){
					System.out.println(tmp);
				}
			}
		}
		br.close();

	}
	
	//to check
	public static void compareTwoFiles1(String filename, String biggerFile)
			throws NumberFormatException, IOException {
		/* read testFile to guessmap */
		HashMap<String, String> realmap = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(new File(biggerFile
				+ ".txt")));
		String line = null;
		String passwd;
		String[] split;
		while ((line = br.readLine()) != null) {
			if (line.length() > 0) {
				split = line.split("\t");
				passwd = split[0];

				if (realmap.get(passwd) == null) {
					realmap.put(passwd,split[1] );
				}
			}
		}
		br.close();

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				filename + "_diffmore.txt")));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File(
				filename + "_diffless.txt")));
		br = new BufferedReader(new FileReader(new File(filename + ".txt")));

		while ((line = br.readLine()) != null) {
			if(line.length()>0){
			split = line.split("\t");
				String x = realmap.get(split[0]);
				if (x != null) {
					realmap.put(split[0], null);
				} else {
					bw.write(line);
					bw.newLine();
				}
			}
		}
		br.close();
		bw.close();
		
		/*输出hashmap剩余的*/
		Iterator<Entry<String, String>> iter = realmap.entrySet().iterator(); 
		while (iter.hasNext()) { 
			Map.Entry<String,String> entry = (Entry<String, String>) iter.next();
			if(entry.getValue()!=null){
				bw2.write(entry.getKey()+"\t"+entry.getValue());
				bw2.newLine();
			}
		}
		bw2.close();
	}
	
	public static void compareTwoFiles(String filename, String biggerFile,String pwdLen)
			throws NumberFormatException, IOException {
		/* read testFile to guessmap */
		HashMap<String, Integer> realmap = new HashMap<String, Integer>();
		BufferedReader br = new BufferedReader(new FileReader(new File(biggerFile
				+ ".txt")));
		String line = null;
		String passwd;
		String[] split;
		while ((line = br.readLine()) != null) {
			if (line.length() > 0) {
				split = line.split("\t");
				passwd = split[0];
				if(split[2].equals(pwdLen)){
					if (realmap.get(passwd) == null) {
						realmap.put(passwd,Integer.parseInt(split[2]) );
					}
				}
			}
		}
		br.close();

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				filename + "_diffmore.txt")));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File(
				filename + "_diffless.txt")));
		br = new BufferedReader(new FileReader(new File(filename + ".txt")));

		while ((line = br.readLine()) != null) {
			if(line.length()>0){
			split = line.split("\t");
			if(split[2].equals(pwdLen)){
				Integer x = realmap.get(split[0]);
				if (x != null) {
					realmap.put(split[0], x-Integer.parseInt(split[2]));
				} else {
					bw.write(line);
					bw.newLine();
				}
			}
			}
		}
		br.close();
		bw.close();
		
		/*输出hashmap剩余的*/
		Iterator<Entry<String, Integer>> iter = realmap.entrySet().iterator(); 
		while (iter.hasNext()) { 
			Map.Entry<String,Integer> entry = (Entry<String, Integer>) iter.next();
			if(entry.getValue()!=0){
				bw2.write(entry.getKey()+"\t"+entry.getValue());
				bw2.newLine();
			}
		}
		bw2.close();

	}
	/**
	 * simple merge "filenames" to the single file "filename"
	 * @param filenames
	 * @param filename
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private static void mergeFiles(String[] filenames, String filename)
			throws Exception {

		BufferedReader br = null;
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename
				+ ".txt")));

		for (int i = 0; i < filenames.length; i++) {
			br = new BufferedReader(new FileReader(new File(filenames[i]
					+ ".txt")));

			String line;
			while ((line = br.readLine()) != null) {
				if(line.length()>0){
					bw.write(line);
					bw.newLine();
				}
			}
			br.close();
		}
		bw.close();

	}

}
