
package passwordAnalysis;

import java.util.regex.*;
import java.io.*;
import pwdutils.*;

public class PinyinWords {
	
	static CaseInsensitiveTrie PYTrie;
	static CaseInsensitiveTrie EWTrie;
	
	public static void main(String[] args) throws Exception{
		
		/*Build Pinyin Trie*/
		PYTrie = new CaseInsensitiveTrie();
		for (int i =0, len = IdentifyPinyin.PINYIN.length; i < len; i++){
			PYTrie.insert(IdentifyPinyin.PINYIN[i]);
		}
		EWTrie = new CaseInsensitiveTrie("Oxford.txt");
		
		/*for whole dataset*/
//		String[] fileSet = {"csdn","duduniu","rockyou","yahoo"};
//		for (String filename : fileSet){
//			filename+="_pwd_count";
//			System.out.println("\n not unique pinyin&word of "+filename+"...");
//			countPwdWord(filename);
//			System.out.println("\n unique pinyin&word of "+filename+"...");
//			countUniquePwdWord(filename);
//		}
		
		/*for sample and guess result*/
//		String dataSet = "csdn";
//		String dataSet = "duduniu";
//		String dataSet = "rockyou";
		String dataSet = "yahoo";
		int round =1;//循环几轮
//		int[] rounds = {3,6,10,};
		String[] filenames = {dataSet+"_1third",dataSet+"_1sixth",dataSet+"_1tenth"};//markov or pcfg
		
		String filename = "";
		for(int i=0;i<filenames.length;i++){
//			round = rounds[i];
			for(int r=1;r<=round;r++){
				/*for sample and guess result*/
				String[] toAnalysiss={"_pwd_count","_markov","_pcfg"};
				for(String toAnalysis:toAnalysiss){
					filename = filenames[i]+"-"+r+toAnalysis;
					
					System.out.println("\n not unique pinyin&word of "+filename+"...");
					countPwdWord(filename);
					System.out.println("\n unique pinyin&word of "+filename+"...");
					countUniquePwdWord(filename);
				}
			}
		}
	
	}
	
	private static void countPwdWord(String filename) throws Exception {
		int pinyinpure = 0;
		int wordpure=0;
		int bothpure=0;
		int pinyinelecit = 0;
		int wordelecit=0;
		int bothelecit=0;
		int total = 0;

		BufferedReader br = new BufferedReader(new FileReader(new File(filename
				+ ".txt")));

//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
//				"0pinyinwords.txt")));
		
		String line;
		String passwd;
		String[] split;
		int count=0;
		boolean[] isCompose = new boolean[3];
		while ((line = br.readLine()) != null) {
			if(line.length()>0){
				split = line.split("\t");
				passwd = split[0];
				if (passwd == null || passwd.length() == 0)
					continue;
				if(split.length==2){
					count=Integer.parseInt(split[1]);
				}
				else if(split.length==3){
					count=Integer.parseInt(split[2]);
				}
				total+=count;
				/*pure*/
				isCompose = pure(passwd);
				if(isCompose[0]){
					pinyinpure+=count;
				}
				if(isCompose[1]){
					wordpure+=count;
				}
				if (isCompose[0] && isCompose[1]){
					pinyinpure-=count;
					wordpure-=count;
					bothpure+=count;
//					bw.write(passwd);
//					bw.newLine();
				}
				
				/*elicit*/
				isCompose = elecit(passwd);
				if(isCompose[0]){
					pinyinelecit+=count;
				}
				if(isCompose[1]){
					wordelecit+=count;
				}
				if (isCompose[0] && isCompose[1]){
					pinyinelecit-=count;
					wordelecit-=count;
					bothelecit+=count;
//					System.out.println(passwd);
//					bw.write(passwd);
//					bw.newLine();
				}
			}
		}
		br.close();
//		bw.close();

		System.out.println("pinyinpure\t" + pinyinpure);
		System.out.println("wordpure\t" + wordpure);
		System.out.println("bothpure\t" + bothpure);
		System.out.println("pinyinelecit\t" + pinyinelecit);
		System.out.println("wordelecit\t" + wordelecit);
		System.out.println("bothelecit\t" + bothelecit);
		System.out.println("total\t" + total);
	}
	
	public static void countUniquePwdWord(String filename) throws Exception {
		int pinyinpure = 0;
		int wordpure=0;
		int bothpure=0;
		int pinyinelecit = 0;
		int wordelecit=0;
		int bothelecit=0;
		int total = 0;

		BufferedReader br = new BufferedReader(new FileReader(new File(filename
				+ ".txt")));

//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
//				"0pinyinwords.txt")));
		
		String line;
		String passwd;
		String[] split;
		boolean[] isCompose = new boolean[3];
		while ((line = br.readLine()) != null) {
			if(line.length()>0){
				split = line.split("\t");
				passwd = split[0];
				if (passwd == null || passwd.length() == 0)
					continue;
				total++;
				/*pure*/
				isCompose = pure(passwd);
				if(isCompose[0]){
					pinyinpure++;
				}
				if(isCompose[1]){
					wordpure++;
				}
				if (isCompose[0] && isCompose[1]){
					pinyinpure--;
					wordpure--;
					bothpure++;
//					bw.write(passwd);
//					bw.newLine();
				}
				
				/*elicit*/
				isCompose = elecit(passwd);
				if(isCompose[0]){
					pinyinelecit++;
				}
				if(isCompose[1]){
					wordelecit++;
				}
				if (isCompose[0] && isCompose[1]){
					pinyinelecit--;
					wordelecit--;
					bothelecit++;
//					System.out.println(passwd);
//					bw.write(passwd);
//					bw.newLine();
				}
			}
		}
		br.close();
//		bw.close();

		System.out.println("pinyinpure\t" + pinyinpure);
		System.out.println("wordpure\t" + wordpure);
		System.out.println("bothpure\t" + bothpure);
		System.out.println("pinyinelecit\t" + pinyinelecit);
		System.out.println("wordelecit\t" + wordelecit);
		System.out.println("bothelecit\t" + bothelecit);
		System.out.println("total\t" + total);
	}
	/**
	 * Extract Pinyin/Words from letter-only passwords.
	 * @param tableName
	 * @param PYTrie
	 * @param EWTrie
	 * @throws Exception
	 */
	public static boolean[] pure(String passwd){
		boolean[] isCompose = new boolean[2];//pinyin;word
		Pattern pattern = Pattern.compile("[a-zA-Z]+");
		Pattern patternSingle = Pattern.compile("(.)\\1*");
		if (!pattern.matcher(passwd).matches())
			return isCompose;
		if (patternSingle.matcher(passwd).matches())
			return isCompose;
		isCompose[0]= PYTrie.canCompose(passwd);
		isCompose[1]= EWTrie.canCompose(passwd);

		return isCompose;

	}
	
	public static boolean[] elecit(String passwd){
		boolean[] isCompose = new boolean[2];//pinyin;word
		Pattern pattern = Pattern.compile("[a-zA-Z]+");
		Pattern pattern2 = Pattern.compile("[^a-zA-Z]");
		Pattern patternSingle = Pattern.compile("(.)\\1*");
		Matcher matcher;
		if (pattern.matcher(passwd).matches())
			return isCompose;
		/* 对得到的口令进行处理 */
		matcher = pattern2.matcher(passwd);
		String tmp = matcher.replaceAll("").trim();
		if (patternSingle.matcher(tmp.toLowerCase()).matches())
			return isCompose;
		isCompose[0]= PYTrie.canCompose(tmp);
		isCompose[1]= EWTrie.canCompose(tmp);

		return isCompose;

	}
}
