package passwordAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * zipf分布
 * @author yys
 *
 */
public class Zipf {

	public static void main(String[] args) throws IOException {
		
		/*analysis in whole dataset*/
//		String[] fileSet = {"csdn","duduniu","rockyou","yahoo"};
//		for (String filename : fileSet){
//			filename+="_pwd_count";
//			/*order and frequency of dataset to log*/	
//			passwdCount2zipf(filename,3);
//			passwdCount2zipf(filename,5);
//			/*calculate zipf*/
//			zipf(filename+"zipf3");
//			zipf(filename+"zipf5");
//		}
		
//		String dataSet = "csdn";
//		String dataSet = "duduniu";
//		String dataSet = "rockyou";
		String dataSet = "yahoo";
		
//		int[] rounds = {3,6,10};
		int round=1;
		String filename="";
		/*analysis in sample*/
		String[] trainingSets = {dataSet+"_1third",dataSet+"_1sixth",dataSet+"_1tenth"};
		for(int i=0;i<trainingSets.length;i++){
//			round = rounds[i];
			for(int r=1;r<=round;r++){
				/*for sample and guess result*/
				String[] toAnalysiss={"_pwd_count","_markov","_pcfg"};
				for(String toAnalysis:toAnalysiss){
					filename = trainingSets[i]+"-"+r+toAnalysis;
					/*order and frequency of sample to log*/	
					passwdCount2zipf(filename,3);
					passwdCount2zipf(filename,5);
					/*calculate zipf*/
					zipf(filename+"zipf3");
					zipf(filename+"zipf5");
				}
			}	
		}
	}
	
	/**
	 * calculate zipf
	 * @param filename:pwd\tlog10(order)\tlog10(freq)
	 * @throws IOException
	 */
	public static void zipf(String filename) throws IOException{
		System.out.println("\nzipf of "+filename+"...");
		BufferedReader br = new BufferedReader(new FileReader(new File(filename+".txt")));

		String line;
		double totalRank=0;
		double totalRreq=0;
		int count=0;
		String[] split;
		while((line = br.readLine()) != null){
			if(line.length()>0){
				split = line.split("\t");
				totalRank+=Double.parseDouble(split[1]);
				totalRreq+=Double.parseDouble(split[2]);
				count++;
			}
		}
		br.close();
		
		double avgRank=totalRank/count;
		double avgFreq = totalRreq/count;
		
		double b =0.0;
		double logc = 0.0;
		double numerator=0.0;
		double denominator=0.0;
		double r2=0.0;
		br = new BufferedReader(new FileReader(new File(filename+".txt")));
		while((line = br.readLine()) != null){
			if(line.length()>0){
				split = line.split("\t");
				numerator+=((Double.parseDouble(split[1])-avgRank)*(Double.parseDouble(split[2])-avgFreq));
				denominator+=((Double.parseDouble(split[1])-avgRank)*(Double.parseDouble(split[1])-avgRank));
				count++;
			}
		}
		br.close();
		b = numerator/denominator;
		logc = avgFreq-b*avgRank;
		System.out.println("s = " + (-b));
		System.out.println("logc = "+ logc);
		
		/*calculate R^2=ESS/TSS*/
		br = new BufferedReader(new FileReader(new File(filename+".txt")));
		double ess=0.0;
		double tss=0.0;
		while((line = br.readLine()) != null){
			if(line.length()>0){
				split = line.split("\t");
				ess+=((logc+b*Double.parseDouble(split[1])-avgFreq)*(logc+b*Double.parseDouble(split[1])-avgFreq));
				tss+=((Double.parseDouble(split[2])-avgFreq)*(Double.parseDouble(split[2])-avgFreq));
			}
		}
		br.close();
		r2=ess/tss;
		System.out.println("R^2 = "+r2);
	}
	
	/**
	 * order and frequency to log with leastFreq limitation
	 * @param filename
	 * @param leastFreq
	 * @throws IOException
	 */
	public static void passwdCount2zipf(String filename,int leastFreq) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(filename+".txt")));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename+"zipf"+leastFreq+".txt")));

		String line;
		int rank=1;
		String[] split;
		int count=0;
		while((line = br.readLine()) != null){
			if(line.length()>0){
				split = line.split("\t");
				count = Integer.parseInt(split[split.length-1]); 
				if(count<=leastFreq){
					break;
				}
				bw.write(split[0]+"\t"+Math.log10(rank)+"\t"+Math.log10(count));
				bw.newLine();
				rank++;
			}
		}
		br.close();
		bw.close();
	}

}
