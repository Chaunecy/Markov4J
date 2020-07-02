/**
 * Find out the dominant structure.
 * Count how many passwords are in the dominant structure.
 * yl done
 * almost same as PCFGBased.java genGrammar
 */
package passwordAnalysis;
import java.util.*;
import java.util.Map.Entry;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import pwdutils.*;

//to check
public class StructDistribute {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
//		String[] datasets = {"csdn_1third","csdn_1sixth","csdn_1sixthnew","csdn_1tenth"};
		String[] datasets = {"csdn_1tenth"};
		for(String filename:datasets){
//			filename+="_pcfgGuessCSDN";
//			countStruct(filename);
			countStruct("csdn_1tenth");
		}
		
		/**
		 * for csdn.txt
		 * map size =52762
		 * top ten:
		 * dddddddd	1381362
		 * ddddddddd	718366
		 * llllllll	298533
		 * ddddddddddd	274161
		 * dddddddddd	240505
		 * llldddddd	172225
		 * lllllllll	160305
		 * lldddddd	128159
		 * llllllllll	123895
		 */
	}
	/**
	 * @param filename
	 * @throws Exception
	 */
	public static void countStruct(String filename) throws Exception{
		String passwd, line;
		String struct;
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		
		BufferedReader br = new BufferedReader(new FileReader(new File(filename+".txt")));
		String[] split;
		while ((line = br.readLine()) != null){
			if(line.length()>0){
				split=line.split("\t");
				passwd=split[0];
				struct = structure(passwd);
				if (struct != null){
					Integer x = map.get(struct);
					if (x == null)
						x = 0;
					if(split.length==1){
						map.put(struct, x+1);
					}
					else{
						map.put(struct, x+Integer.parseInt(split[1]));
					}
				}
			}
		}
		br.close();
		
		int size = map.size();
		System.out.println("Map size:" + size);
		String[] str = new String[size];
		int[] num = new int[size];
		
		Iterator<Entry<String, Integer>> iter = map.entrySet().iterator();
		int k = 0;
		while (iter.hasNext()){
			Map.Entry<String, Integer> entry = (Entry<String, Integer>) iter.next();
			str[k] = entry.getKey();
			num[k++] = entry.getValue();
		}
		System.out.println("------- Sorting -------");
		QuickSort.sort(str, num);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				filename + "_countStructure.txt")));
		for (k = size - 1; k >= 0; k--){
			bw.write(str[k]+"\t"+num[k]);
			bw.newLine();
		}
		bw.close();
	}
	
	
	/*show passwd in "u,l,d,s"*/
	public static String structure(String passwd){
		String result = "";
		
		if (passwd == null || passwd.length() == 0)
			return null;
		
		char[] characters = passwd.toCharArray();
		
		for (int i = 0, len = characters.length; i < len; i++){
			if (characters[i] >= 'A' && characters[i] <= 'Z')
				result += 'u';
			else if (characters[i] >= 'a' && characters[i] <= 'z')
				result += 'l';
			else if (characters[i] >= '0' && characters[i] <= '9')
				result += 'd';
			else 
				result += 's';
		}
		
		return result;
	}
}
