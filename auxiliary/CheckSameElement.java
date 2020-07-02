package auxiliary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class CheckSameElement {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		String filename="rockyou_1sixth-1_markov";
		HashSet<String> hset = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(new File(filename+".txt")));
		String line = null;
		String passwd;
		String[] split;
		while ((line = br.readLine()) != null) {
			if (line.length() > 0) {
				split = line.split("\t");
				passwd = split[0];

				if (!hset.contains(passwd)) {
					hset.add(passwd);
				}
				else{
					System.out.println(line);
				}
			}
		}
		br.close();
	}
}
