
package passwordAnalysis;

import java.io.*;

import static pwdutils.Constants.*;

/**
 * 长度相关的统计和处理
 * @author yys
 *
 */
public class LenDistribute {
	
	public static void main(String[] args) throws IOException {
		
		/*for whole dataset*/
//		String[] fileSet = {"csdn","duduniu","rockyou","yahoo"};
//		for (String filename : fileSet){
//			filename+="_pwd_count";
//			System.out.println(filename+" not unique....");
//			pwdLength(filename);
//			System.out.println(filename+" unqiue....");
//			uniquePwdLength(filename);
//			
//			/*get file by length range*/
//			int[][] lenRanges = {{4,7},{8,10},{11,40}};
//			for(int k=0;k<lenRanges.length;k++){
//				writeFileByLength(filename,lenRanges[k][0],lenRanges[k][1]);
//			}
//		}
		
		/*for sample or guess result*/
//		String dataSet = "csdn";
//		String dataSet = "duduniu";
//		String dataSet = "rockyou";
		String dataSet = "yahoo";
		
		int round =1;//循环几轮
//		int[] rounds = {3,6,10};
		String[] filenames = {dataSet+"_1third",dataSet+"_1sixth",dataSet+"_1tenth"};
		
		String filename = "";
		for(int i=0;i<filenames.length;i++){
//			round = rounds[i];
			for(int r=1;r<=round;r++){
				//for sample and guess result
				String[] toAnalysiss={"_pwd_count","_markov","_pcfg"};
				for(String toAnalysis:toAnalysiss){
					filename = filenames[i]+"-"+r+toAnalysis;
					//len distribution for file
					System.out.println(filename+" not unique....");
					pwdLength(filename);
					System.out.println(filename+" unqiue....");
					uniquePwdLength(filename);
					//get file by length range
					int[][] lenRanges = {{4,7},{8,10},{11,40}};
					for(int k=0;k<lenRanges.length;k++){
						writeFileByLength(filename,lenRanges[k][0],lenRanges[k][1]);
					}
				}
			}
		}	
	}
	/**
	 * wirte file by range [length1,length2]
	 * @param file
	 * @throws IOException
	 */
	public static void writeFileByLength(String filename,int length1,int length2) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(new File(filename+".txt")));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				filename + "_len"+length1+"-"+length2+".txt")));
		
		String line;
		int length=0;
		/* Main logic */
		while ((line = br.readLine()) != null){
			if(line.length()>0){
				length = line.split("\t")[0].length();
				if(length>=length1&&length<=length2){
					bw.write(line);
					bw.newLine();
				}
			}
		}
		br.close();
		bw.close();
	}
	
	/**
	 * wirte file by length
	 * @param file
	 * @throws IOException
	 */
	public static void writeFileByLength(String filename,int length) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(new File(filename+".txt")));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				filename + "_len"+length+".txt")));
		
		String line;
		String[] split = null;
		/* Main logic */
		while ((line = br.readLine()) != null){
			if(line.length()>0){
				split = line.split("\t");
				if(split[0].length()==length){
					bw.write(line);
					bw.newLine();
				}
			}
		}
		br.close();
		bw.close();
		
	}
	
	/**
	 * Analysis the length distribution for passwords
	 * @param filename
	 * @throws IOException
	 */
	public static void pwdLength(String filename) throws IOException{
		
		/* # of passwords of length is stored in lenDist[l]. * That's why LEN_LIMIT need to add 1. *  'Dist' is short for 'distribution'. */
		int[] lenDist = new int[LEN_LIMIT+1];// The # of passwords will absolutely not exceed MAX_INT.
		
		/* Initiate arrays */
		for (int i = 0, len = lenDist.length; i < len; i++){
			lenDist[i] = 0;
		}
		
		/* Main logic */
		BufferedReader br = new BufferedReader(new FileReader(new File(filename+".txt")));
		String line;
		String[] split = null;
		while ((line = br.readLine()) != null){
			if(line.length()>0){
				split = line.split("\t");
				if(split.length==1){//original file
					lenDist[split[0].length()]++;
				}
				else if(split.length==2){//password count file
					lenDist[split[0].length()]+=Integer.parseInt(split[1]);
				}
				else if(split.length==3){//guess result file
					lenDist[split[0].length()]+=Integer.parseInt(split[2]);
				}
				else{
					System.out.println("error: length>3");
				}
			}
		}
		br.close();
		
		/* Print result. */
		System.out.println(String.format("%10s\t%10s", "Length", "Count"));
		for (int i = 1, len = lenDist.length; i < len; i++){
			if (lenDist[i] != 0)
				System.out.println(String.format("%10d\t%,10d", i,  lenDist[i]));
		}
		
	}
	/**
	 * Analysis the length distribution for unique passwords
	 * @param filename:pwd\tcount
	 * @throws IOException
	 */
	public static void uniquePwdLength(String filename) throws IOException{
		
		/* # of passwords of length is stored in lenDist[l]. * That's why LEN_LIMIT need to add 1. *  'Dist' is short for 'distribution'. */
		int[] lenDist = new int[LEN_LIMIT+1];// The # of passwords will absolutely not exceed MAX_INT.
		
		/* Initiate arrays */
		for (int i = 0, len = lenDist.length; i < len; i++){
			lenDist[i] = 0;
		}
		
		/* Main logic */
		BufferedReader br = new BufferedReader(new FileReader(new File(filename+".txt")));
		String line;
		while ((line = br.readLine()) != null){
			if(line.length()>0){
				lenDist[line.split("\t")[0].length()]++;
			}
		}
		br.close();
		
		/* Print result. */
		System.out.println(String.format("%10s\t%10s", "Length", "Count"));
		for (int i = 1, len = lenDist.length; i < len; i++){
			if (lenDist[i] != 0)
				System.out.println(String.format("%10d\t%,10d", i,  lenDist[i]));
		}
		
	}
}
