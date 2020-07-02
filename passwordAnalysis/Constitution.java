/**
 * Constitution of the passwords.
 * Digit-only, lowercase-only, ....
 * yl done
 */
package passwordAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Pattern;

public class Constitution {

	static int DIGIT_ONLY = 0;
	static int LOWER_ONLY = 1;
	static int UPPER_ONLY = 2;
	static int SYM_ONLY = 3;

	static int DIGIT_LOWER = 4;
	static int DIGIT_UPPER = 5;
	static int DIGIT_SYM = 6;
	static int LOWER_UPPER = 7;
	static int LOWER_SYM = 8;
	static int UPPER_SYM = 9;

	static int DIGIT_LOWER_UPPER = 10;
	static int DIGIT_LOWER_SYM = 11;
	static int DIGIT_UPPER_SYM = 12;
	static int LOWER_UPPER_SYM = 13;

	static int ALL = 14;

	static int NONE = 15;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		/*for whole dataset*/
//		String[] fileSet = {"csdn","duduniu","rockyou","yahoo"};
//		for (String filename : fileSet){
//			filename+="_pwd_count";
//			System.out.println("\n"+filename+"not unique....");
//			show(calcTable(filename));
//			System.out.println("\n"+filename+" unique....");
//			show(uniqueCalcTable(filename));
//			
//			int[][] lenRanges = {{4,7},{8,10},{11,40}};
//			String filename2 = "";
//			for(int k=0;k<lenRanges.length;k++){
//				filename2 = filename +	"_len"+lenRanges[k][0]+"-"+lenRanges[k][1];
//				System.out.println("\n"+filename2+"not unique....");
//				show(calcTable(filename2));
//				System.out.println("\n"+filename2+" unique....");
//				show(uniqueCalcTable(filename2));
//			}
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
					
					/*constitution distribution*/
					System.out.println("\n"+filename+"not unique....");
					show(calcTable(filename));
					System.out.println("\n"+filename+" unique....");
					show(uniqueCalcTable(filename));
					
					/*constitution distribution for file with length range*/
					int[][] lenRanges = {{4,7},{8,10},{11,40}};
					for(int k=0;k<lenRanges.length;k++){
						filename = filenames[i]+"-"+r+toAnalysis+
							"_len"+lenRanges[k][0]+"-"+lenRanges[k][1];
				
						System.out.println("\n"+filename+"not unique....");
						show(calcTable(filename));
						System.out.println("\n"+filename+" unique....");
						show(uniqueCalcTable(filename));
					}
				}
			}
		}
	}

	/**
	 * show constitution
	 * @param consti
	 */
	public static void show(int[] consti) {
		int total = 0;
		for (int i = 0; i < 15; i++) {
			System.out.println(i + "\t" + consti[i]);
			total += consti[i];
		}
		System.out.println("Total: " + total);
		System.out.println();
		System.out.println("digit_only:\t" + consti[DIGIT_ONLY]);
		System.out
				.println("Letter_ONLY:\t"
						+ (consti[LOWER_ONLY] + consti[UPPER_ONLY] + consti[LOWER_UPPER]));
		System.out.println("LOWER_ONLY:\t" + consti[LOWER_ONLY]);
		System.out
				.println("digit+letter:\t"
						+ (consti[DIGIT_LOWER] + consti[DIGIT_UPPER] + consti[DIGIT_LOWER_UPPER]));
		System.out.println("digit+lower:\t" + consti[DIGIT_LOWER]);
		System.out
				.println("letter+symbol:\t"
						+ (consti[LOWER_SYM] + consti[UPPER_SYM] + consti[LOWER_UPPER_SYM]));
		System.out.println("lower+symbol:\t" + consti[LOWER_SYM]);
		System.out.println("symbo+digit:\t" + consti[DIGIT_SYM]);
		System.out
				.println("digit+letter+symbol:\t"
						+ (consti[DIGIT_LOWER_SYM] + consti[DIGIT_UPPER_SYM] + consti[ALL]));
		System.out.println("digit+lower+symbol:\t" + consti[DIGIT_LOWER_SYM]);
		System.out.println("mixed:\t"
				+ (consti[4] + consti[5] + consti[8] + consti[9] + consti[10]
						+ consti[11] + consti[12] + consti[13] + consti[14]));
	}

	/**
	 * count constitution
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static int[] calcTable(String filename) throws Exception {
		int[] result = new int[15];
		for (int i = 0; i < result.length; i++)
			result[i] = 0;

		BufferedReader br = new BufferedReader(new FileReader(new File(filename
				+ ".txt")));
		String line, passwd;
		/* Main logic */
		String[] split;
		while ((line = br.readLine()) != null) {
			if(line.length()>0){
				split = line.split("\t");
				passwd = split[0];
				if(split.length==1){
					result[constitution(passwd)]++;
				}
				else if(split.length==2){
					result[constitution(passwd)]+=Integer.parseInt(split[1]);
				}
				else if(split.length==3){
					result[constitution(passwd)]+=Integer.parseInt(split[2]);
				}
				else{
					System.out.println("error: length>3");
				}
			}
		}
		br.close();

		return result;
	}
	
	/**
	 * count constitution for unique passwords
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static int[] uniqueCalcTable(String filename) throws Exception {
		int[] result = new int[15];
		for (int i = 0; i < result.length; i++)
			result[i] = 0;

		BufferedReader br = new BufferedReader(new FileReader(new File(filename
				+ ".txt")));
		String line;
		/* Main logic */
		while ((line = br.readLine()) != null) {
			if(line.length()>0){
				result[constitution(line.split("\t")[0])]++;
			}
		}
		br.close();

		return result;
	}
	/**
	 * Return the type of one password.
	 * 
	 * @param passwd
	 * @return
	 */
	public static int constitution(String passwd) {
		if (passwd == null || passwd.length() == 0)
			return NONE;
		Pattern sym = Pattern.compile("[^0-9a-zA-Z]+");
		Pattern digit = Pattern.compile("[0-9]+");
		Pattern lower = Pattern.compile("[a-z]+");
		Pattern upper = Pattern.compile("[A-Z]+");

		if (passwd.matches("[0-9]+"))
			return DIGIT_ONLY;

		if (passwd.matches("[a-z]+"))
			return LOWER_ONLY;

		if (passwd.matches("[A-Z]+"))
			return UPPER_ONLY;

		if (passwd.matches("[^0-9a-zA-Z]+"))
			return SYM_ONLY;

		if (passwd.matches("[0-9a-z]+"))
			return DIGIT_LOWER;

		if (passwd.matches("[0-9A-Z]+"))
			return DIGIT_UPPER;

		if (passwd.matches("[a-zA-Z]+"))
			return LOWER_UPPER;

		if (passwd.matches("[0-9a-zA-Z]+"))
			return DIGIT_LOWER_UPPER;

		if (sym.matcher(passwd).find()) {
			if (digit.matcher(passwd).find()) {
				if (lower.matcher(passwd).find()) {
					if (upper.matcher(passwd).find())
						return ALL;

					return DIGIT_LOWER_SYM;
				}

				if (upper.matcher(passwd).find())
					return DIGIT_UPPER_SYM;

				return DIGIT_SYM;
			}

			if (lower.matcher(passwd).find()) {
				if (upper.matcher(passwd).find())
					return LOWER_UPPER_SYM;
				return LOWER_SYM;
			}

			if (upper.matcher(passwd).find()) {
				// if (digit.matcher(passwd).find())
				// return DIGIT_UPPER_SYM;
				return UPPER_SYM;
			}

		}
		return NONE;
	}
}
