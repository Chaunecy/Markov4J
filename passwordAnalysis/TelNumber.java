package passwordAnalysis;

import java.util.regex.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class TelNumber {
//	static int  countprint=0;
	public static void main(String[] args) throws Exception {
		/*for whole dataset*/
//		String[] fileSet = {"csdn","duduniu","rockyou","yahoo"};
//		for (String filename : fileSet){
//			filename+="_pwd_count";
//			System.out.println("\n not unique tel of "+filename+"...");
//			countPwdTel(filename);
//			System.out.println("\n unique tel of "+filename+"...");
//			countUniquePwdTel(filename);
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
					
					System.out.println("\n not unique tel of "+filename+"...");
					countPwdTel(filename);
					System.out.println("\n unique tel of "+filename+"...");
					countUniquePwdTel(filename);
				}
			}
		}

	}

	private static void countUniquePwdTel(String filename) throws Exception {
		int tel = 0;
		int total = 0;

		BufferedReader br = new BufferedReader(new FileReader(new File(filename
				+ ".txt")));

		String line;
		String passwd;
		String[] split;
		while ((line = br.readLine()) != null) {
			if(line.length()>0){
				split = line.split("\t");
				passwd = split[0];
				if (passwd == null || passwd.length() == 0)
					continue;
				total++;
				if(isTel(passwd)){
					tel++;
				}
			}
		}
		br.close();

		System.out.println("Total\t" + total);
		System.out.println("Tel\t" + tel);
	}
	
	private static void countPwdTel(String filename) throws Exception {
		int tel = 0;
		int total = 0;

		BufferedReader br = new BufferedReader(new FileReader(new File(filename
				+ ".txt")));

		String line;
		String passwd;
		int count=0;
		String[] split;
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
				if(isTel(passwd)){
					tel+=count;
				}
			}
		}

		br.close();

		System.out.println("Total\t" + total);
		System.out.println("Tel\t" + tel);
	}
	
	private static boolean isTel(String str){
		Pattern pattern = Pattern.compile("1(30|31|32|33|34|35|36|37|38|39|47|50|51|52|53|55|56|57|58|59|80|82|85|86|87|88|89)[0-9]{8}");
		Pattern pureDigit = Pattern.compile("\\d+");
		String regex = String.format("\\d{%d}", 11);
		Pattern exactPattern = Pattern.compile(regex);
		Matcher pureMatcher,exactMatcher;
		
		pureMatcher = pureDigit.matcher(str);
		String digits;
		
		while (pureMatcher.find()){
			digits = pureMatcher.group();
			exactMatcher = exactPattern.matcher(digits);
			if (exactMatcher.matches()){
				if(pattern.matcher(digits).matches()){
//					if(countprint<100&&!pureDigit.matcher(str).matches()){
//						System.out.println(str);
//						countprint++;
//					}
					return true;
				}
			}
		}
		return false;
	}
}
