package auxiliary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * 将一个文件放入两个文件
 * **/
public class SplitFile {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		String filename = "csdn_1sixth-1_pcfg";
		BufferedReader br = new BufferedReader(new FileReader(new File(filename+".txt")));
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File(filename+"_1.txt")));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File(filename+"_2.txt")));
		BufferedWriter bw3 = new BufferedWriter(new FileWriter(new File(filename+"_3.txt")));
		String trainLine;
		int count = 0;
		
		while(((trainLine = br.readLine()) != null) && (trainLine.length() > 0)){
				count++;
				if(count<5000){
					bw1.write(trainLine);
					bw1.newLine();
				}
				else if(count == 5000){
					bw1.write(trainLine);
					bw1.newLine();
					bw1.close();
					//break;
				}
				else if(count < 100000){
					bw2.write(trainLine);
					bw2.newLine();
				}
				else if(count == 100000){
					bw2.write(trainLine);
					bw2.newLine();
					bw2.close();
					break;
				}
//				else{
//					bw3.write(trainLine);
//					bw3.newLine();
//				}
		}
		br.close();
		if(count<5000){
			bw1.close();
		}
		else if(count<100000){
			bw2.close();
			
		}
		bw3.close();
	}

}
