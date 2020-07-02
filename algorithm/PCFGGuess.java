/**
 * adjusted Probabilistic Context-Free Grammar.
 * similar to MarkovGuess
 */
package algorithm;

import java.io.*;

import pwdutils.Constants;

public class PCFGGuess {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		/*对pcfg,训练时已经check长度范围，
		 * 因此structure也限定了猜测的口令长度不会超出这个范围。
		 * 如果需要对猜测结果做进一步要求，可以在最终写入guessResult之前做限制.
		 */
//		int minLen = 4;
		
//		String[] dataSets = {"csdn"};
//		int[] targetNumbers = {6420426};
		
//		String dataSet = "duduniu";
//		int targetNumber = 15049451;
		
//		String[] dataSets = {"rockyou"};
//		int[] targetNumbers = {32575770};
		
		String[] dataSets = {"yahoo"};
		long[] targetNumbers = {442287};
		
		
//		String[] dataSets = {"csdn","duduniu","rockyou","yahoo"};
//		long[] targetNumbers = {6420426,15049451,32575770,442287};
		for(int datasetIndex=0,len = dataSets.length;datasetIndex<len;datasetIndex++){//start 4 website simulate
			
		String dataSet = dataSets[datasetIndex];
		long targetNumber = targetNumbers[datasetIndex];
		
//		int[] rounds = {3,6,10};
		int round=1;
		String[] trainingSets = {dataSet+"_1third",dataSet+"_1sixth",dataSet+"_1tenth"};
		for(int i=0;i<trainingSets.length;i++){
//		for(int i=0;i<1;i++){
//			round = rounds[i];
			for(int r=1;r<=round;r++){
				PCFG pcfg = new PCFG();
				pcfg.STRUCTMAXLEN = Constants.LEN_LIMIT;//40上限已知，为了方便方便初始化
				
				/* train 相关概率为prob*/
				long startTime = System.currentTimeMillis();
				pcfg.train(trainingSets[i]+"-"+r);
					System.out.println(trainingSets[i]+"-"+r+ " train stopped in time: "
							+ Constants.operateTime(System.currentTimeMillis() - startTime));
		
				/* generate 相关概率为log(prob)*/
				startTime = System.currentTimeMillis();
				pcfg.generate(new File(trainingSets[i]+"-"+r+"_pcfg.txt"),targetNumber);
				System.out.println(trainingSets[i]+"-"+r+" pcfg guess Stopped in time: "+ Constants.operateTime(System.currentTimeMillis() - startTime));
			}
		}
		
		}//end 4 website simulate
	}
}
