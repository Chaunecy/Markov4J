
package passwordAnalysis;

import java.util.regex.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Patterns {
	
	public static final int SINGLE = 0;//单一字符aaaa
	public static final int Pair = 1;//两种字符aabbb
	public static final int CONTINUOUS = 2;//顺序字符abcdef
	public static final int NCONTINUOUS = 3;//顺序字符叠加aabbccdd
	public static final int REPEAT = 4;//重复出现adgadgadg
	public static final int REVERSE = 5;//反转abcddcba
	

	public static void main(String[] args) throws Exception {
		/*for whole dataset*/
//		String[] fileSet = {"csdn"};
//		String[] fileSet = {"csdn","duduniu","rockyou","yahoo"};
//		for (String filename : fileSet){
//			filename+="_pwd_count";
//			System.out.println("\n not unique pattern of "+filename+"...");
//			countPwdPattern(filename);
//			System.out.println("\n unique pattern of "+filename+"...");
//			countUniquePwdPattern(filename);
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
					
					System.out.println("\n not unique pattern of "+filename+"...");
					countPwdPattern(filename);
					System.out.println("\n unique pattern of "+filename+"...");
					countUniquePwdPattern(filename);
				}
			}
		}
		
		/*for test*/
//		System.out.println("single? "+isSingle("aaaaaab"));//false
//		System.out.println("single? "+isSingle("xxx"));//false
//		System.out.println("single? "+isSingle("xxxx"));//true
//		System.out.println("single? "+isSingle("x"));//false
//		System.out.println("single? "+isSingle(""));//false
//		System.out.println("single? "+isSingle("66667777"));//false
//		System.out.println("pair? "+isPair("66667777"));
//		System.out.println("isContinuous? "+isContinuous("66667777"));
//		System.out.println("isContinuous? "+isContinuous("123465"));//false
//		System.out.println("isContinuous? "+isContinuous("123456"));//true
//		System.out.println("isNcontinuous? "+isNcontinuous("football"));//false
//		System.out.println("isNcontinuous? "+isNcontinuous("aabbccc"));//true
//		System.out.println("isNcontinuous? "+isNcontinuous("abcde"));//false
//		System.out.println("isIsRepeat? "+isRepeat("8642"));//false
//		System.out.println("isIsRepeat? "+isRepeat("86428642"));//true
//		System.out.println("isIsRepeat? "+isRepeat("864286428642"));//true
//		System.out.println("isIsRepeat? "+isRepeat("aaaa"));//false
		
	}

	public static void countUniquePwdPattern(String filename) throws Exception {
		int total =0;
		int[] patterns = new int[6];

		BufferedReader br = new BufferedReader(new FileReader(new File(filename
				+ ".txt")));

		String line;
		String passwd;
		String[] split;
		//for debug
//		int i1=0,i2=0,i3=0,i4=0,i5=0;
		while ((line = br.readLine()) != null) {
			if(line.length()>0){
				split = line.split("\t");
				passwd = split[0];
				if (passwd == null || passwd.length() == 0)
					continue;
				total++;
				if(isSingle(passwd)){
					patterns[SINGLE]++;
//					if(i1<5){
//						System.out.println("single: "+passwd);
//						i1++;
//					}
				}else if(isPair(passwd)){
					patterns[Pair]++;
//					if(i2<100){
//						System.out.println("pair: "+passwd);
//						i2++;
//					}
				}
				else if(isContinuous(passwd)){
					patterns[CONTINUOUS]++;
//					if(i3<400){
//						System.out.println("continuous: "+passwd);
//						i3++;
//					}
				}else if(isNcontinuous(passwd)){
					patterns[NCONTINUOUS]++;
//					if(i4<100){
//						System.out.println("nContinuous: "+passwd);
//						i4++;
//					}
				}else  if(isRepeat(passwd)){
					patterns[REPEAT]++;
//					if(i5<50){
//						System.out.println("repeat: "+passwd);
//						i5++;
//					}
				}else if(isReverse(passwd)){
					patterns[REVERSE]++;
				}
			}
		}
		br.close();

		System.out.println("Total\t" + total);
		System.out.println("SINGLE\t" + patterns[0]);
		System.out.println("PAIR\t" + patterns[1]);
		System.out.println("CONTINUOUS\t" + patterns[2]);
		System.out.println("NCONTINUOUS\t" + patterns[3]);
		System.out.println("REPEAT\t" + patterns[4]);
		System.out.println("REVERSE\t" + patterns[5]);
	}

	public static void countPwdPattern(String filename) throws Exception {
		int total =0;
		int[] patterns = new int[6];

		BufferedReader br = new BufferedReader(new FileReader(new File(filename
				+ ".txt")));

		String line;
		String passwd;
		String[] split;
		int count=0;
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
				if(isSingle(passwd)){
					patterns[SINGLE]+=count;
				}else  if(isPair(passwd)){
					patterns[Pair]+=count;
				}
				else  if(isContinuous(passwd)){
					patterns[CONTINUOUS]+=count;
				}else if(isNcontinuous(passwd)){
					patterns[NCONTINUOUS]+=count;
				}else  if(isRepeat(passwd)){
					patterns[REPEAT]+=count;
				}else if(isReverse(passwd)){
					patterns[REVERSE]+=count;
				}
			}
		}
		br.close();

		System.out.println("Total\t" + total);
		System.out.println("SINGLE\t" + patterns[0]);
		System.out.println("PAIR\t" + patterns[1]);
		System.out.println("CONTINUOUS\t" + patterns[2]);
		System.out.println("NCONTINUOUS\t" + patterns[3]);
		System.out.println("REPEAT\t" + patterns[4]);
		System.out.println("REVERSE\t" + patterns[5]);
		
	}
	
	/**
	 * 口令长度4-40的前提下一定正确
	 * @param passwd
	 * @return
	 */
	public static boolean isSingle(String passwd) {
		Pattern pattern = Pattern.compile("(.)\\1{3,}");
		return pattern.matcher(passwd).matches();
	}
	
	public static boolean isPair(String passwd) {
		if(isSingle(passwd)){
			return false;
		}//可省略，因为main里的逻辑以及处理了
		Pattern pattern = Pattern.compile("(.)\\1*(.)\\2*");
		return pattern.matcher(passwd).matches();
	}
	/**
	 * for example:123454321
	 * @param passwd
	 * @return
	 */
	public static boolean isReverse(String passwd) {
		int length = passwd.length();
		for(int i=0;i<length/2;i++){
			   if(passwd.charAt(i)!=passwd.charAt(length-i-1)){
			    return false;
			   }
		}
		return true;
	}

	public static boolean isRepeat(String passwd) {
		if(isSingle(passwd)){
			return false;
		}//可省略，因为main里的逻辑以及处理了
		Pattern pattern = Pattern.compile("(.{2,})\\1+");//两个及以上相同的
		return pattern.matcher(passwd).matches();
	}

	/**
	 * 不管每一个 小片段的长度是否相等
	 * @param passwd
	 * @return
	 */
	public static boolean isNcontinuous(String passwd) {
		if(isPair(passwd)){
			return false;
		}//可省略，因为main里的逻辑以及处理了
		Pattern pattern = Pattern.compile("(.)\\1*");
		Matcher mtc = pattern.matcher(passwd);
		mtc.find();
		String str = mtc.group();
		if(str.length()<2){
			return false;
		}
		char c = str.charAt(0);
		int interval=0;
		while(mtc.find()){
			str=mtc.group();
			if(str.length()<2){
				return false;
			}
			if(interval==0){
				interval = str.charAt(0)-c;
				c= str.charAt(0);
			}
			else{
				if(str.charAt(0)-c!=interval){
					return false;
				}
			}
		}
		return true;

	}

	/**
	 * 一直前提：口令长度大于等于4
	 * @param passwd
	 * @return
	 */
	public static boolean isContinuous(String passwd) {
		
		char prechar=passwd.charAt(1);
		int interval = prechar-passwd.charAt(0);
		if(interval==0){
			return false;
		}
		char curchar;
		int tmp=0;
        for (int i = 2; i < passwd.length(); i++) {
        	
            curchar = passwd.charAt(i);
            tmp  = curchar-prechar;
            if (tmp != interval) {
                return false;
            }
            prechar = curchar;
        }
        
		return true;
	}

	
}
