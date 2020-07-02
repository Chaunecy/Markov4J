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

import pwdutils.QuickSort;

/**
 * 口令文件统计的常用方法
 * @author yys
 *
 */
public class PasswordCount {

	public static void main(String[] args) throws Exception {
	
//		/*for whole dataset*/
//		String[] fileSet = {"csdn","duduniu","rockyou","yahoo"};
//		for (String filename : fileSet){
//			passwdCount(filename);
//		}
		
		/*for sample*/
////		String dataSet = "csdn";
//		String dataSet = "duduniu";
////		String dataSet = "rockyou";
////		String dataSet = "yahoo";
////		int[] rounds = {3,6,10};
//		int round=1;
//		String[] filenames = {dataSet+"_1third",dataSet+"_1sixth",dataSet+"_1tenth"};
//		for(int i=0;i<filenames.length;i++){
////			round = rounds[i];
//			for(int r=1;r<=round;r++){
//				countTotal(filenames[i]+"-"+r+"_markov");
//				countTotal(filenames[i]+"-"+r+"_pcfg");
//				System.out.println();
//				passwdCount(filenames[i]+"-"+r);
//			}
//		}
		countTotal("csdn_1sixth-1_pcfg");
	}
	
	
	/**
	 * count passwds, and sort them in descending order
	 * output:passwd \t count
	 * 		  passwd \t prob 
	 * 
	 * @param filename.txt:passwd
	 * @throws IOException
	 */
	public static void passwdCount(String filename) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(new File(filename+".txt")));
		HashMap<String,Integer> hashmap = new HashMap<String,Integer>();
		String line;
		String passwd;
		Integer num;
		int totalNum = 0;
		while((line = br.readLine()) != null){
			if(line.length()>0){
				totalNum++;
				passwd = line;
				num = hashmap.get(passwd);
				if(num == null){
					hashmap.put(passwd, 1);
				}
				else{
					hashmap.put(passwd,num+1);
				}
			}
		}
		br.close();
		System.out.println("totalNum="+totalNum);
		
		/*输出hashmap的情况*/
		int len = hashmap.size();
		System.out.println("hashmap len = "+ len);
		int[] count = new int[len];
		String[] str = new String[len];
		int j = 0;
		Iterator<Entry<String, Integer>> iter = hashmap.entrySet().iterator(); 
		while (iter.hasNext()) { 
			Map.Entry<String,Integer> entry = (Entry<String, Integer>) iter.next();
			String key = entry.getKey()+""; 
			int val = entry.getValue();
			str[j] = key;
			count[j]=val;
			j++;
		} 	
		QuickSort.sort(str, count);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename+"_pwd_count.txt")));

		for (int i=len-1; i>=0; i--){
			bw.write(str[i]+"\t"+count[i]);
			bw.newLine();
		}
		bw.close();
	}

	//to check
	@SuppressWarnings("unused")
	private static void newOrder(String filename1, String filename2, String filename) throws IOException {
		
		/*read in sample file*/
		BufferedReader br = new BufferedReader(new FileReader(new File(filename2+".txt")));
		String tmp;
		String passwd;
		HashMap<String,String> hashmap= new HashMap<String,String>(); 
		while((tmp = br.readLine())!=null){
				passwd = tmp.split("\t")[0];
				if(hashmap.get(passwd)==null){
					hashmap.put(passwd, tmp.split("\t")[1]);
				}
				else{
					System.out.println("error count file1: "+ tmp);
				}
		}
		br.close();
		
		BufferedReader br2 = new BufferedReader(new FileReader(new File(filename1+".txt")));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename+".txt")));
		String tmp2;	

		while ((tmp2 = br2.readLine())!= null){
			passwd = tmp2.split("\t")[0];
			if(hashmap.get(passwd)!=null){
				bw.write(tmp2.split("\t")[0]+"\t"+hashmap.get(passwd));
				bw.newLine();
			}
			else{
				bw.write(tmp2.split("\t")[0]+"\t0");
				bw.newLine();
			}
		}
		br2.close();
		bw.close();
		
	}

	/**
	 * to sum up the count and check if the total number of right
	 * @param filename: a file:pwd\tcount
	 * @throws Exception
	 */
	private static void countTotal(String filename)
			throws Exception {

		System.out.println(filename+"...");
		BufferedReader br = new BufferedReader(new FileReader(new File(filename
				+ ".txt")));
		String line;
		String[] split;
		int totalNum=0;
		int totalUnique=0;
		while ((line = br.readLine()) != null) {
			if (line.length() > 0) {
				split = line.split("\t");
				if (split.length == 2) {
					totalNum+=Integer.parseInt(split[1]);
				}
				else if (split.length == 3) {
					totalNum+=Integer.parseInt(split[2]);
				}  
				else {
					System.out.println("Format error: length>3");
				}
				totalUnique++;
			} else {
				System.out.println("Format error: length=0");
			}
		}
		br.close();
		System.out.println("totalNum="+totalNum);
//		System.out.println("totalUnique="+totalUnique);
	}
}
