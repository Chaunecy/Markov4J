/**
 * Guessing using Probabilistic Context-Free Grammar.
 */
package algorithm;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pwdutils.Constants;
import pwdutils.MyNode;
import pwdutils.QuickSort;

public class PCFGBased {

	File trainFile;
	Queue<MyNode> priorityQ;// 存放的都是具体的password(即terminal)
	HashMap<String, Integer> guessmap = new HashMap<String, Integer>();
	Comparator<MyNode> Order;
	long targetNumber = 0;
	//The max length for the array hashmaps which stores the D2,S3.....grammar.
	
	int STRUCTMAXLEN = 40;
	int MAXLEN = STRUCTMAXLEN+1;

	// Generator
	public PCFGBased() {
		initiatePriorityQ();
	}

	public PCFGBased(File trainFile, File dictionary) {
		this.trainFile = trainFile;
		initiatePriorityQ();
	}

	public Queue<MyNode> getPriorityQ() {
		return priorityQ;
	}

	// Set the Comparator of the priority queue to satisfy my need: Order the
	// grammar by probability in descending order.
	private void initiatePriorityQ() {
		Order = new Comparator<MyNode>() {
			public int compare(MyNode o1, MyNode o2) {
				double numbera = o1.getP();
				double numberb = o2.getP();
				if (numberb > numbera) {
					return 1;
				} else if (numberb < numbera) {
					return -1;
				} else {
					return 0;
				}
			}
		};

		priorityQ = new PriorityQueue<MyNode>(10000, Order);
	}

	/**
	 * If not exsit, create one. if exist, empty it.
	 */
	private void checkDir(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (File x : files) {
					x.delete();
				}
			} else {
				file.delete();
				file.mkdir();
			}
		} else {
			file.mkdir();
		}
	}

	private static String getStruct(String str) {
		char[] cset = str.toCharArray();
		String result = "";

		for (int i = 0, len = cset.length; i < len; i++) {
			if (cset[i] >= 'a' && cset[i] <= 'z')
				result += "l";
			else if (cset[i] >= 'A' && cset[i] <= 'Z')
				result += "u";
			else if (cset[i] >= '0' && cset[i] <= '9')
				result += "d";
			else
				result += "s";
		}

		return result;
	}

	public void setTrainFile(File training) {
		this.trainFile = training;
	}

	/**
	 * Guess using the generated grammar and the dictionary.
	 */
	@SuppressWarnings("unchecked")
	public void guess(File templateFile, String trainingSet, double threshold) throws Exception {

		/*
		 * In the following three variables, MyNode is only used for two
		 * parameters: baseStructure and p. Do not treat baseStruct as the
		 * original meaning. It is just the structure, maybe D2->33, 0.01.
		 */
		List<MyNode>[] digitList = new ArrayList[MAXLEN];
		List<MyNode>[] lowerList = new ArrayList[MAXLEN];
		List<MyNode>[] upperList = new ArrayList[MAXLEN];
		List<MyNode>[] symbolList = new ArrayList[MAXLEN];
		// default order will suffice?yl intended to suffice? why.
		List<MyNode> baseS = new ArrayList<MyNode>(); // base structure.

		// Read base structure(template) first.
		BufferedReader br = new BufferedReader(new FileReader(templateFile));

		String line = br.readLine();
		String[] split;
		while (line != null) {
			if (line.length() > 0) {
				split = line.split("\t");
				if (split.length != 2) {
					System.out.println("[WRONG]: " + line);
					line = br.readLine();
					continue;
				}
				baseS.add(new MyNode(split[0], Double.parseDouble(split[1])));
			}
			line = br.readLine();
		}
		br.close();

		/**
		 * Read other structures: digitList, lowerList, upperList and
		 * symbolList.
		 **/
		String digitPath = trainingSet + "_digit/";
		String symbolPath = trainingSet + "_symbol/";
		String lowerPath = trainingSet + "_lower/";
		String upperPath = trainingSet + "_upper/";

		File file;
		for (int i = 0; i < MAXLEN; i++) {
			// Read digit.
			file = new File(digitPath + i + ".txt");
			if (file.exists()) {
				digitList[i] = new ArrayList<MyNode>();
				readToList(file, digitList[i]);
			}

			// Read Symbol
			file = new File(symbolPath + i + ".txt");
			if (file.exists()) {
				symbolList[i] = new ArrayList<MyNode>();
				readToList(file, symbolList[i]);
			}

			// Read Lower
			file = new File(lowerPath + i + ".txt");
			if (file.exists()) {
				lowerList[i] = new ArrayList<MyNode>();
				readToList(file, lowerList[i]);
			}

			// Read Upper
			file = new File(upperPath + i + ".txt");
			if (file.exists()) {
				upperList[i] = new ArrayList<MyNode>();
				readToList(file, upperList[i]);
			}
		}

		/*
		 * Initialize the values in the priority queue: priorityQ. This time,
		 * MyNode is fully used.
		 */
		Pattern pattern = Pattern.compile("(d+|s+|l+|u+)");
		Matcher matcher;
		double probability;
		int len;
		String structure, preTerm = "", str; // str is the child structure.
		MyNode tmp = null;
		for (MyNode base : baseS) {
			preTerm = "";
			// A new node ready to insert into the priorityQ.
			MyNode entry = new MyNode();
			structure = base.getBaseStruct();
			entry.setBaseStruct(structure);
			probability = base.getP();

			matcher = pattern.matcher(structure);

			while (matcher.find()) {
				str = matcher.group();
				len = str.length();
				if (str.matches("l+")) {
					// 0 is the entry with the biggest probability.
					tmp = lowerList[len].get(0);
				} else if (str.matches("u+")) {
					tmp = upperList[len].get(0);
				} else if (str.matches("d+")) {
					tmp = digitList[len].get(0);
				} else if (str.matches("s+")) {
					tmp = symbolList[len].get(0);
				} else {
					System.out
							.println("template file error: contain str out of{u,l,s,d}");
				}
				preTerm += tmp.getBaseStruct();
				probability *= tmp.getP();
			}// end while (match.find())

			entry.setP(probability);
			entry.setPreTerminal(preTerm);
			entry.setPivot(0);

			priorityQ.add(entry);
		}

		 /* guess out password from high probability to low probability*/
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				trainingSet + "_pcfg.txt")));
		String guessResult;
		long guesscount = 0;
		int tmpcount=0;
		int curcount=0;
		while (true) {
			guessResult = guessNext(lowerList, upperList, digitList,
					symbolList, threshold);
			/*calculate if simulated set has reached targetNumber*/
			tmpcount = (int) (targetNumber*Double.parseDouble(guessResult.split("\t")[1]));
			curcount = tmpcount>0?tmpcount:1;
			guesscount += curcount;
			if(guesscount>=targetNumber){
				bw.write(guessResult+"\t"+(curcount-(guesscount-targetNumber)));
				bw.newLine();
				break;
			}
			else{
				bw.write(guessResult+"\t"+curcount);
				bw.newLine();
			}
		}
		bw.close();
	}

	/**
	 * Used by guess() poll the first entry of the priorityQ and insert other
	 * grammars into the priorityQ.
	 */
	private String guessNext(List<MyNode>[] lowerList,
			List<MyNode>[] upperList, List<MyNode>[] digitList,
			List<MyNode>[] symbolList, double threshold) throws Exception {
		MyNode entry = priorityQ.poll();

		if (entry == null) {
			System.out.println("[OUT OF PATTERN!]!");
			System.exit(0);
		}

		int pivot = entry.getPivot();
		double probability = entry.getP();
		String structure = entry.getBaseStruct();
		int counter = -1; // Replace if counter >= pivot
		String preTerm = entry.getPreTerminal();
		int index = 0; // show where the replacement is
		int len; // temp child grammar length.
		String str; // temp child grammar.
		Pattern pattern = Pattern.compile("(d+|l+|u+|s+)");
		Matcher matcher = pattern.matcher(structure);
		List<MyNode>[] list = null;

		MyNode newEntry, replaceNode, lastNode;
		int[] posC;

		/** Insert into the priority queue **/
		while (matcher.find()) {
			counter++;
			str = matcher.group();
			len = str.length();
			// System.out.println("[test]" + str + " " +counter);

			if (counter >= pivot) {
				if (str.matches("l+"))
					list = lowerList;
				else if (str.matches("u+"))
					list = upperList;
				else if (str.matches("d+"))
					list = digitList;
				else if (str.matches("s+"))
					list = symbolList;

				// todo! 大量的节点都具有相同的概率，如何合并?使得直接这里就插入所有等同大小的节点
				if (list != null) {
					posC = new int[41];// yl change 100 to 41
					newEntry = new MyNode();
					newEntry.setBaseStruct(structure);
					newEntry.setPivot(counter);
					arrayCopy(entry.posCounter, posC);
					if (posC[counter] + 1 < list[len].size()) {
						lastNode = list[len].get(posC[counter]);

						replaceNode = list[len].get(posC[counter] + 1);

						newEntry.setPreTerminal(preTerm.substring(0, index)
								+ replaceNode.getBaseStruct()
								+ preTerm.substring(index + len));

						// for setting threshold
						double newProb = probability / lastNode.getP()
								* replaceNode.getP();
						if (newProb >= threshold) {
							newEntry.setP(newProb);
							posC[counter]++;
							newEntry.posCounter = posC;
							priorityQ.add(newEntry);
						}
					}
				}
			}

			index += len;
		}

		return preTerm + "\t" + probability;

	}

	private static void arrayCopy(int[] src, int[] dest) {
		for (int i = 0; i < src.length; i++)
			dest[i] = src[i];
	}

	private static void readToList(File file, List<MyNode> list)
			throws Exception {
		BufferedReader br;
		String line;
		String[] split;

		if (file.exists()) {
			br = new BufferedReader(new FileReader(file));
			line = br.readLine();
			while (line != null) {
				if (line.length() > 0) {
					split = line.split("\t");
					if (split.length != 2) {
						System.out.println("[WRONG]: " + line);
						line = br.readLine();
						continue;
					}

					list.add(new MyNode(split[0], Double.parseDouble(split[1])));
				}
				line = br.readLine();
			}
			br.close();
		}
	}

	/**
	 * Generate the probabilities of lowers, uppers, digits and symbols.
	 */

	@SuppressWarnings("unchecked")
	public void trainInstantiation(String trainingSet) throws Exception {

		BufferedReader br = new BufferedReader(new FileReader(new File(
				trainingSet + ".txt")));
		String digitPath = trainingSet + "_digit/";
		String symbolPath = trainingSet + "_symbol/";
		String lowerPath = trainingSet + "_lower/";
		String upperPath = trainingSet + "_upper/";
		checkDir(new File(digitPath));
		checkDir(new File(symbolPath));
		checkDir(new File(lowerPath));
		checkDir(new File(upperPath));
		
		int[] totalDigit = new int[MAXLEN];
		int[] totalSymbol = new int[MAXLEN];
		int[] totalLower = new int[MAXLEN];
		int[] totalUpper = new int[MAXLEN];

		HashMap<String, Integer>[] dmap = new HashMap[MAXLEN];
		HashMap<String, Integer>[] smap = new HashMap[MAXLEN];
		HashMap<String, Integer>[] lmap = new HashMap[MAXLEN];
		HashMap<String, Integer>[] umap = new HashMap[MAXLEN];

		String line, passwd;
		Pattern sep = Pattern
				.compile("([0-9]+)|([a-z]+)|([A-Z]+)|([^a-zA-Z0-9]+)");
		Matcher mat;

		int freq = 1;// 格式为pwd\tcount或pwd
		String[] split;
		while ((line = br.readLine()) != null) {
			if (line.length() > 0) {
				split = line.split("\t");
				passwd = split[0];
				// check the input passwd
				if (Constants.checkPasswd(passwd)) {
					if(split.length==2){
						freq = Integer.parseInt(split[1]);
					}
					else{
						freq =1;
					}
					/* Map the segment to instantiation */
					mat = sep.matcher(passwd);
					String seg2;
					int seg2Len = 0;
					while (mat.find()) {
						seg2 = mat.group();
						if (seg2.matches("[0-9]+")) {
							seg2Len = seg2.length();
							if (dmap[seg2Len] == null)
								dmap[seg2Len] = new HashMap<String, Integer>();
							add2Map(seg2, dmap[seg2Len], freq);// put
																// instantiation
																// to number
																// segment
							totalDigit[seg2Len] += freq;
						} else if (seg2.matches("[a-z]+")) {
							seg2Len = seg2.length();
							if (lmap[seg2Len] == null)
								lmap[seg2Len] = new HashMap<String, Integer>();
							add2Map(seg2, lmap[seg2Len], freq);
							totalLower[seg2Len] += freq;
						} else if (seg2.matches("[A-Z]+")) {
							seg2Len = seg2.length();
							if (umap[seg2Len] == null)
								umap[seg2Len] = new HashMap<String, Integer>();
							add2Map(seg2, umap[seg2Len], freq);
							totalUpper[seg2Len] += freq;
						} else {
							seg2Len = seg2.length();
							if (smap[seg2Len] == null)
								smap[seg2Len] = new HashMap<String, Integer>();
							add2Map(seg2, smap[seg2Len], freq);
							totalSymbol[seg2Len] += freq;
						}
					}
				} else {
					System.out
							.println("it is impossible to exist in the file: "
									+ passwd);
				}
			}
		}
		br.close();

		/* write dmap, lmap, umap, smap to files. */
		// todo:make length variable
		for (int i = 1; i <= STRUCTMAXLEN; i++) {
			BufferedWriter digitbw, symbolbw, lowerbw, upperbw;
			if (dmap[i] != null) {
				digitbw = new BufferedWriter(new FileWriter(new File(digitPath
						+ i + ".txt")));
				int size = dmap[i].size();
				String[] stri = new String[size];
				int[] numb = new int[size];

				int c = 0; // temp counter.
				Iterator<Entry<String, Integer>> iter = dmap[i].entrySet()
						.iterator();
				while (iter.hasNext()) {
					Map.Entry<String, Integer> entry = iter
							.next();
					stri[c] = entry.getKey();
					numb[c++] = entry.getValue();
				}

				// Sort&write
				QuickSort.sort(stri, numb);
				for (int j = size - 1; j >= 0; j--) {
					digitbw.write(stri[j] + "\t"
							+ (numb[j] * 1.0 / totalDigit[i]) + "\n");
				}
				digitbw.close();
			}

			if (smap[i] != null) {
				symbolbw = new BufferedWriter(new FileWriter(new File(
						symbolPath + i + ".txt")));
				int size = smap[i].size();
				String[] stri = new String[size];
				int[] numb = new int[size];

				int c = 0; // temp counter.
				Iterator<Entry<String, Integer>> iter = smap[i].entrySet()
						.iterator();
				while (iter.hasNext()) {
					Map.Entry<String, Integer> entry = iter
							.next();
					stri[c] = entry.getKey();
					numb[c++] = entry.getValue();
				}

				// Sort&write
				QuickSort.sort(stri, numb);
				for (int j = size - 1; j >= 0; j--) {
					symbolbw.write(stri[j] + "\t"
							+ (numb[j] * 1.0 / totalSymbol[i]) + "\n");
				}
				symbolbw.close();
			}
			if (lmap[i] != null) {
				lowerbw = new BufferedWriter(new FileWriter(new File(lowerPath
						+ i + ".txt")));
				int size = lmap[i].size();
				String[] stri = new String[size];
				int[] numb = new int[size];

				int c = 0; // temp counter.
				Iterator<Entry<String, Integer>> iter = lmap[i].entrySet()
						.iterator();
				while (iter.hasNext()) {
					Map.Entry<String, Integer> entry = iter
							.next();
					stri[c] = entry.getKey();
					numb[c++] = entry.getValue();
				}

				// Sort&write
				QuickSort.sort(stri, numb);
				for (int j = size - 1; j >= 0; j--) {
					lowerbw.write(stri[j] + "\t"
							+ (numb[j] * 1.0 / totalLower[i]) + "\n");
				}
				lowerbw.close();
			}
			if (umap[i] != null) {
				upperbw = new BufferedWriter(new FileWriter(new File(upperPath
						+ i + ".txt")));
				int size = umap[i].size();
				String[] stri = new String[size];
				int[] numb = new int[size];

				int c = 0; // temp counter.
				Iterator<Entry<String, Integer>> iter = umap[i].entrySet()
						.iterator();
				while (iter.hasNext()) {
					Map.Entry<String, Integer> entry = iter
							.next();
					stri[c] = entry.getKey();
					numb[c++] = entry.getValue();
				}

				// Sort&write
				QuickSort.sort(stri, numb);
				for (int j = size - 1; j >= 0; j--) {
					upperbw.write(stri[j] + "\t"
							+ (numb[j] * 1.0 / totalUpper[i]) + "\n");
				}
				upperbw.close();
			}
		}
	}

	/**
	 * Using the training file to get the BASE grammars. directly counting will
	 * not generate too large number of template, so no need to set threshold
	 */
	public void genGrammar(String trainingSet) throws Exception {
		if(Constants.debug)
			System.out.println("Generate base structures...");
		/*
		 * Write the first file: structures.txt which includes the structures
		 * with their probability in descending order.
		 */
		BufferedReader br = new BufferedReader(new FileReader(new File(
				trainingSet + ".txt"))); // Read the training file.
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				trainingSet + "_structures.txt")));
		String line = br.readLine();
		// store the structures and their occurrences
		HashMap<String, Integer> hashmap = new HashMap<String, Integer>();

		int count = 0; // Count how many passwords in training file.
		String passwd;
		// store the structure of a training password temporarily
		String structure;
		int freq = 1;
		String[] split;
		while (line != null) {
			if (line.length() > 0) {
				split = line.split("\t");
				passwd = split[0];
				// check the format and length of input passwd from trainingSet 
				if (Constants.checkPasswd(passwd)) {
					if(split.length==2){
						freq = Integer.parseInt(split[1]);
					}
					else{
						freq =1;
					}
					count += freq;
					structure = getStruct(passwd);
					Integer x = hashmap.get(structure);
					if (x == null)
						hashmap.put(structure, freq);
					else
						hashmap.put(structure, x + freq);
				}
			}

			line = br.readLine();
		}
		br.close();

		/*
		 * The occurrences of structure str[i] is stored in num[i]. It is
		 * convenient to SORT the structures using array.
		 */
		int size = hashmap.size();
		int[] num = new int[size]; // the occurrences of one structure
		String[] str = new String[size]; // the structure.

		int c = 0; // temp counter.

		Iterator<Entry<String, Integer>> iter = hashmap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Integer> entry = iter
					.next();
			str[c] = entry.getKey();
			num[c++] = entry.getValue();
		}

		QuickSort.sort(str, num); // Sort them in ASCENDING order.

		for (int i = size - 1; i >= 0; i--) { // the reverse order because we
												// need DESCENDING order.
			bw.write(str[i] + "\t" + (num[i] * 1.0 / count));
//			bw.write(str[i] + "\t" + num[i]);
			bw.newLine();
		}
		bw.close();
		
		if(Constants.debug)
			System.out.println("Base structures have been generated.");
	}

	public void train(String trainingSet) throws Exception {
		
		/*get template by counting*/
//		System.out.println("Generating structure Grammar....");
		genGrammar(trainingSet);

		/*get instantiation by counting*/
//		System.out.println("Genrating lower, upper, digits and symbols instantiation ....");
		trainInstantiation(trainingSet);

	}

	private static void add2Map(String str, HashMap<String, Integer> map,
			int freq) {
		Integer x = map.get(str);
		map.put(str, (x == null) ? freq : x + freq);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		/*对pcfg,训练时已经check长度范围，
		 * 因此structure也限定了猜测的口令长度不会超出这个范围。
		 * 如果需要对猜测结果做进一步要求，可以在最终写入guessResult之前做限制.
		 */
//		int minLen = 4;
		
//		double threshold = 1E-11;
//		double threshold = 1E-11;
		double threshold = 0;
		
		String dataSet = "csdn";
		long targetNumber = 6420426;
		
//		String dataSet = "duduniu";
//		long targetNumber = 15049451;
		
//		String dataSet = "rockyou";
//		long targetNumber = 32575770;
		
//		String dataSet = "yahoo";
//		long targetNumber = 442287;
		
//		int[] rounds = {3,6,10};
		int round=1;
		String[] trainingSets = {dataSet+"_1tenth",dataSet+"_1third",dataSet+"_1sixth"};
//		for(int i=0;i<trainingSets.length;i++){
		for(int i=0;i<1;i++){
//			round = rounds[i];
			for(int r=1;r<=round;r++){
				PCFGBased pcfgBased = new PCFGBased();
				pcfgBased.targetNumber = targetNumber*10;
				pcfgBased.STRUCTMAXLEN = Constants.LEN_LIMIT;//40上限已知，为了方便方便初始化
				
				/* train */
				long startTime = System.currentTimeMillis();
				pcfgBased.train(trainingSets[i]+"-"+r);
					System.out.println(trainingSets[i]+"-"+r+ " train stopped in time: "
							+ Constants.operateTime(System.currentTimeMillis() - startTime));
		
				/* generate */
				File templateFile = new File(trainingSets[i]+"-"+r + "_structures.txt");
				startTime = System.currentTimeMillis();
				pcfgBased.guess(templateFile, trainingSets[i]+"-"+r, threshold);
				System.out.println(trainingSets[i]+"-"+r+" pcfg guess Stopped in time: "+ Constants.operateTime(System.currentTimeMillis() - startTime));
			}
		}
	}
}
