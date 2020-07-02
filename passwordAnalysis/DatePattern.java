
package passwordAnalysis;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatePattern {
	
	static List<String> fplist = new ArrayList<String>();
	public static void main(String[] args) throws Exception {
		String[] falsepositive={"111111", "123123", "111000", "112233","100200",
				"111222", "121212", "520520", "110110", "123000",
				"101010", "111333","110120", "102030", "110119",
				"121314", "521125", "120120", "010203", "122333",
				"121121", "101101", "131211", "100100", "321123", 
				"110112", "112211", "111112","520521", "110111"};
		for(String fps:falsepositive){
			fplist.add(fps);
		}
		/*for whole dataset*/
//		String[] fileSet = {"csdn","duduniu","rockyou","yahoo"};
//		for (String filename : fileSet){
//			filename+="_pwd_count";
//			System.out.println("\n not unique 6 or 8 digits date of "+filename+"...");
//			countPwdDate(filename);
//			System.out.println("\n unique 6 or 8 digits date of "+filename+"...");
//			countUniquePwdDate(filename);
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
					
					System.out.println("\n not unique 6 or 8digits date of "+filename+"...");
					countPwdDate(filename);
					System.out.println("\n unique 6 or 8 digits date of "+filename+"...");
					countUniquePwdDate(filename);
					
				}
			}
		}

	}
	
	private static void countUniquePwdDate(String filename) throws Exception {
		int date6Num=0;
		int date8Num=0;
		int sepdate6Num=0;
		int sepdate8Num=0;
		int dateNum=0;
		int totaldateNum=0;
		int total = 0;
		
		BufferedReader br = new BufferedReader(new FileReader(new File(filename
				+ ".txt")));

		String line;
		String passwd;
		String[] split;
		boolean checkhasDate6;
		boolean checkhasDate8;
		boolean checkhassepDate6;
		boolean checkhassepDate8;
		while ((line = br.readLine()) != null) {
			if(line.length()>0){
				split = line.split("\t");
				passwd = split[0];
				if (passwd == null || passwd.length() == 0)
					continue;
				total++;
				checkhasDate6 = hasDate(passwd,6);
				checkhasDate8 = hasDate(passwd,8);
				checkhassepDate6 = hasSepDate(passwd,6);
				checkhassepDate8 = hasSepDate(passwd,8);
				if(checkhasDate6){
					date6Num++;
				}
				if(checkhasDate8){
					date8Num++;
				}
				if(checkhassepDate6){
					sepdate6Num++;
				}
				if(checkhassepDate8){
					sepdate8Num++;
				}
				
				if(checkhasDate6||checkhasDate8){
					dateNum++;
				}
				if(checkhasDate6||checkhasDate8||checkhassepDate6||checkhassepDate8){
					totaldateNum++;
				}
			}
		}
		br.close();
		System.out.println("Total\t" + total);
		System.out.println("date6Num\t" + date6Num);
		System.out.println("date8Num\t" + date8Num);
		System.out.println("sepdate6Num\t" + sepdate6Num);
		System.out.println("sepdate8Num\t" + sepdate8Num);
		System.out.println("dateNum\t" + dateNum);
		System.out.println("totaldateNum\t" + totaldateNum);
	}
	
	private static void countPwdDate(String filename) throws Exception {
		int date6Num=0;
		int date8Num=0;
		int sepdate6Num=0;
		int sepdate8Num=0;
		int dateNum=0;//no sep
		int totaldateNum=0;
		int total = 0;

		BufferedReader br = new BufferedReader(new FileReader(new File(filename
				+ ".txt")));

		String line;
		String passwd;
		int count=0;
		String[] split;
		boolean checkhasDate6;
		boolean checkhasDate8;
		boolean checkhassepDate6;
		boolean checkhassepDate8;
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
				checkhasDate6 = hasDate(passwd,6);
				checkhasDate8 = hasDate(passwd,8);
				checkhassepDate6 = hasSepDate(passwd,6);
				checkhassepDate8 = hasSepDate(passwd,8);
				if(checkhasDate6){
					date6Num+=count;
				}
				if(checkhasDate8){
					date8Num+=count;
				}
				if(checkhassepDate6){
					sepdate6Num+=count;
				}
				if(checkhassepDate8){
					sepdate8Num+=count;
				}
				if(checkhasDate6||checkhasDate8){
					dateNum+=count;
				}
				if(checkhasDate6||checkhasDate8||checkhassepDate6||checkhassepDate8){
					totaldateNum+=count;
				}
			}
		}
		br.close();
		System.out.println("Total\t" + total);
		System.out.println("date6Num\t" + date6Num);
		System.out.println("date8Num\t" + date8Num);
		System.out.println("sepdate6Num\t" + sepdate6Num);
		System.out.println("sepdate8Num\t" + sepdate8Num);
		System.out.println("dateNum\t" + dateNum);
		System.out.println("totaldateNum\t" + totaldateNum);
	}

	public static boolean hasDate(String passwd, int dateLength){
		/*get password with length 6 or 8*/
		Pattern pureDigit = Pattern.compile("\\d+");
		//not necessary but may decrease the time of using regex
		String regex = String.format("\\d{%d}", dateLength);
		Pattern exactPattern = Pattern.compile(regex);
		Matcher pureMatcher,exactMatcher;
		pureMatcher = pureDigit.matcher(passwd);

		String digits;
		while (pureMatcher.find()){
			digits = pureMatcher.group();
			exactMatcher = exactPattern.matcher(digits);
			if (exactMatcher.matches()){
				if(isDate(digits,dateLength,false)){
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean hasSepDate(String passwd, int dateLength) {
		Pattern pureDigit = Pattern.compile("\\d+[-./]\\d{2}[-./]\\d+");
//		String regex8 = "(\\d{4}[-./]\\d{2}[-./]\\d{2})|(\\d{2}[-./]\\d{2}[-./]\\d{4})";
		Matcher pureMatcher;
		String tmp = passwd;
		pureMatcher = pureDigit.matcher(tmp);

		String digits;
		while (pureMatcher.find()){
			digits = pureMatcher.group();
			if(isDate(digits,dateLength,true)){
				if(!tmp.equals(passwd)){
					System.out.println("tmp="+tmp);
					System.out.println("passwd="+passwd);
				}
				return true;
			}
			int index= tmp.indexOf(digits);
			if(digits.contains("-")){
				index+=digits.indexOf("-");
			}else if(digits.contains(".")){
				index+=digits.indexOf(".");
			}else if(digits.contains("/")){
				index+=digits.indexOf("/");
			}
			tmp = tmp.substring(index);
			pureMatcher = pureDigit.matcher(tmp);
		}
		return false;
	}
	/**
	 * 已经是满足长度的N位字符串了。判断具体数字是否满足日期格式
	 * @param passwd
	 * @param dateLength
	 * @return
	 * @throws Exception
	 */
	private static boolean isDate(String date, int dateLength,boolean issep){
		/*for 8 digit date*/
		Pattern YYYYMMDD = Pattern.compile("(19|20)\\d\\d((0[13578]|1[02])([0-2][0-9]|30|31)|(04|06|09|11)([0-2][0-9]|30)|02[0-2][0-9])");
		Pattern MMDDYYYY = Pattern.compile("((0[13578]|1[02])([0-2][0-9]|30|31)|(04|06|09|11)([0-2][0-9]|30)|02[0-2][0-9])(19|20)\\d\\d");
		Pattern DDMMYYYY = Pattern.compile("(([0-2][0-9]|30|31)(0[13578]|1[02])|([0-2][0-9]|30)(04|06|09|11)|[0-2][0-9]02)(19|20)\\d\\d");
		
		Pattern YYYYMMDDsep = Pattern.compile("(19|20)\\d\\d[-./]((0[13578]|1[02])[-./]([0-2][0-9]|30|31)|(04|06|09|11)[-./]([0-2][0-9]|30)|02[-./][0-2][0-9])");
		Pattern MMDDYYYYsep = Pattern.compile("((0[13578]|1[02])[-./]([0-2][0-9]|30|31)|(04|06|09|11)[-./]([0-2][0-9]|30)|02[-./][0-2][0-9])[-./](19|20)\\d\\d");
		Pattern DDMMYYYYsep = Pattern.compile("(([0-2][0-9]|30|31)[-./](0[13578]|1[02])|([0-2][0-9]|30)[-./](04|06|09|11)|[0-2][0-9][-./]02)[-./](19|20)\\d\\d");
		
		/*for 6 digit daet*/
		Pattern YYMMDD = Pattern.compile("\\d\\d((0[13578]|1[02])([0-2][0-9]|30|31)|(04|06|09|11)([0-2][0-9]|30)|02[0-2][0-9])");
		Pattern DDMMYY = Pattern.compile("(([0-2][0-9]|30|31)(0[13578]|1[02])|([0-2][0-9]|30)(04|06|09|11)|[0-2][0-9]02)\\d\\d");
		Pattern MMDDYY = Pattern.compile("((0[13578]|1[02])([0-2][0-9]|30|31)|(04|06|09|11)([0-2][0-9]|30)|02[0-2][0-9])\\d\\d");
		
		Pattern YYMMDDsep = Pattern.compile("\\d\\d[-./]((0[13578]|1[02])[-./]([0-2][0-9]|30|31)|(04|06|09|11)[-./]([0-2][0-9]|30)|02[-./][0-2][0-9])");
		Pattern DDMMYYsep = Pattern.compile("(([0-2][0-9]|30|31)[-./](0[13578]|1[02])|([0-2][0-9]|30)[-./](04|06|09|11)|[0-2][0-9][-./]02)[-./]\\d\\d");
		Pattern MMDDYYsep = Pattern.compile("((0[13578]|1[02])[-./]([0-2][0-9]|30|31)|(04|06|09|11)[-./]([0-2][0-9]|30)|02[-./][0-2][0-9])[-./]\\d\\d");
		
		if (dateLength==6){
			//排除一些明显不是的
			String checkString = date;
			Pattern patternelecit = Pattern.compile("[^0-9]");
			Matcher matcher = patternelecit.matcher(checkString);
			String tmp = matcher.replaceAll("").trim();
			if(fplist.contains(tmp)){
//				System.out.println("date:  "+date+" fplist: "+tmp);
				return false;
			}
			if((!issep)&&(YYMMDD.matcher(date).matches() || MMDDYY.matcher(date).matches() || DDMMYY.matcher(date).matches())){
				return true;
			}
			else if(issep&&(YYMMDDsep.matcher(date).matches() || MMDDYYsep.matcher(date).matches() || DDMMYYsep.matcher(date).matches())){
				return true;
			}
		}else if (dateLength==8){
			if((!issep)&&(YYYYMMDD.matcher(date).matches() || MMDDYYYY.matcher(date).matches() || DDMMYYYY.matcher(date).matches())){
				return true;
			}
			else if(issep&&(YYYYMMDDsep.matcher(date).matches() || MMDDYYYYsep.matcher(date).matches() || DDMMYYYYsep.matcher(date).matches())){
				return true;
			}
		}
		
		return false;	
	}
}
