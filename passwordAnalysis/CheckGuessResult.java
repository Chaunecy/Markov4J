package passwordAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

/**
 * analysis index1 guess right rate
 * @author yys
 *
 */
public class CheckGuessResult {
	
	public static void main(String[] args) throws Exception {
		
//		String dataSet = "csdn";
//		int targetNumber =  6420426;
		
//		String dataSet = "duduniu";
//		int targetNumber = 15049451;
		
//		String dataSet = "rockyou";
//		int targetNumber = 32575770;
		
		String dataSet = "yahoo";
		int targetNumber = 442287;
		
		
		String testFile = dataSet;
		/*check markov result*/
//		int[] rounds = {3,6,10};
		int round=1;
		String[] guessfiles = {dataSet+"_1third",dataSet+"_1sixth",dataSet+"_1tenth"};
		for(int i=0;i<guessfiles.length;i++){
//			round = rounds[i];
			for(int r=1;r<=round;r++){
				checkGuessResult(guessfiles[i]+"-"+r+"_markov", testFile);
			}
		}
		/*check pcfg result*/
		for(int i=0;i<guessfiles.length;i++){
//			round = rounds[i];
			for(int r=1;r<=round;r++){
				/*check*/
				checkGuessResult(guessfiles[i]+"-"+r+"_pcfg", testFile);
			}
		}
	}
	
	/**
	 * guesssfile中多少口令落入testFile中
	 * @param guessfile:pwd\tprob\tcount
	 * @param testFile：非unique的pwd
	 * @throws Exception
	 */
	private static void checkGuessResult(String guessfile, String testFile) throws Exception {
		System.out.println("check "+guessfile+".....");
		/*read testFile to guessmap*/
		HashMap<String, Integer> guessmap = new HashMap<String, Integer>();
		BufferedReader br = new BufferedReader(new FileReader(new File(testFile+".txt")));

		String line = null;
		String passwd;
		Integer x;
		while ((line= br.readLine()) != null) {
			if (line.length() > 0) {
				passwd = line;
				x = guessmap.get(passwd);
				if ( x == null){
					guessmap.put(passwd, 1);
				}
			}
		}
		br.close();
		
		br = new BufferedReader(new FileReader(new File(guessfile
				+ ".txt")));
		int count=0;
		int rightguessTotal = 0, rightguessUnique = 0;
		while ((line=br.readLine()) != null) {
			if (line.length() > 0) {
				passwd = line.split("\t")[0];
				count = Integer.parseInt(line.split("\t")[2]);
				x = guessmap.get(passwd);
				if (x != null) {
					rightguessUnique++;
					rightguessTotal += count;
				}
			}
		}
		br.close();		

		System.out.println("rightGuessUnique\t"+rightguessUnique);
		System.out.println("rightguessTotal\t"+rightguessTotal);
		
	}
}
