package auxiliary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * 为交叉验证取随机等分的口令
 * @author yys
 *
 */
public class GetRandomPwd {
	
	//随机的第index份
	public static int index = 1;

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		
		int N = 3;//口令文件分成N份
//		int totalNum = 6420426;
//		String dataSet = "csdn";
//		int totalNum = 15049451;
//		String dataSet = "duduniu";
//		int totalNum = 32575770;
//		String dataSet = "rockyou";
		int totalNum = 442287;
		String dataSet = "yahoo";
		
		int count = totalNum/N;//每一份的大小
		/**randomly split the dataSet into N files with equal size*/
		String Nname="";
		switch (N){
			case 3:
				Nname = "_1third";
				break;
			case 6:
				Nname = "_1sixth";
				break;
			case 10:
				Nname = "_1tenth";
				break;
		}
		
		/*get the first file form the dataSet*/
		int[] arr = generateRandomNums(totalNum, count);
		randomPwdFromTxt(dataSet,dataSet+Nname,arr);
		
		/*get the next N-1 files*/
		int leftNum = totalNum;
		for(index=2;index<=N;index++){
			leftNum-=count;
			arr = generateRandomNums(leftNum, count);
			randomPwdFromTxt(dataSet+Nname+"-"+(index-1)+"left",dataSet+Nname,arr);
		} 
		
	}

	
	/**
	 * get passwd from filename.txt according to the index arr
	 * write to fileOut :passwd
	 * 
	 * @param filename
	 * @param fileOut
	 * @param arr, an array of random indexes for passwords
	 * @throws IOException
	 */
	private static void randomPwdFromTxt(String filename, String fileOut, int[]arr) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(new File(filename+".txt")));
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File(fileOut+"-"+index+".txt")));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File(fileOut+"-"+index+"left.txt")));
		int count = 0;
		int indexOfArr=0;
		String line;
		int len = arr.length;
		while((line = br.readLine()) != null){
			if(line.length()>0){
				if(indexOfArr < len && count == arr[indexOfArr]){
					bw1.write(line);
					bw1.newLine();
					indexOfArr++;
				}
				else{
					bw2.write(line);
					bw2.newLine();
				}
				count++;
			}
		}
		br.close();
		bw1.close();
		bw2.close();
	}
	
	
	/**
	 * randomly generate count unique numbers from [1,totalNums]
	 * and sort them
	 * 
	 * @param totalNum
	 * @param count
	 * @return array of these random numbers, value range[0,totalNum)
	 */
	public static int[] generateRandomNums(int totalNum, int count){
		
		boolean[] hasRandom = new boolean[totalNum];
		
		int[] arr = new int[count];
		int random = 0;
		boolean flag = true;
		for(int i=0;i<count;i++){
			flag = true;
			do{
				random = (int)(Math.random()*totalNum);//[0,1)
				if(hasRandom[random]==false){
					arr[i] = random;
					hasRandom[random] = true;
					flag = false;
				}
			}while(flag);
		}
		Arrays.sort(arr);
 		
		return arr;
	}
	
}
