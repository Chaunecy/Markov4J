/**
 * adjusted Probabilistic Context-Free Grammar.
 */
package algorithm;

import static pwdutils.Constants.operateTime;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pwdutils.Constants;
import pwdutils.DoublePairBuffer;
import pwdutils.MyNode;
import pwdutils.QuickSort;

public class PCFG {

	// The max length for the array hashmaps which stores the D2,S3.....grammar.
	int STRUCTMAXLEN = 40;
	int MAXLEN = STRUCTMAXLEN + 1;
	private static final double OVERSHOOT = 2.0;
	private static final double MULT = 8.0;

	/*
	 * In the following three variables, MyNode is only used for two parameters:
	 * baseStructure and p. Do not treat baseStruct as the original meaning. It
	 * is just the structure, maybe D2->33, 0.01.
	 */
	List<MyNode>[] digitList = new LinkedList[MAXLEN];
	List<MyNode>[] lowerList = new LinkedList[MAXLEN];
	List<MyNode>[] upperList = new LinkedList[MAXLEN];
	List<MyNode>[] symbolList = new LinkedList[MAXLEN];
	// default order will suffice?yl intended to suffice? why.
	List<MyNode> baseS = new LinkedList<MyNode>(); // base structure.

	// Generator
	public PCFG() {
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

	private void readToList(HashMap<String, Integer> xmap, List<MyNode> list,
			int totalNum) throws Exception {

		if (xmap != null) {
			int size = xmap.size();
			String[] stri = new String[size];
			int[] numb = new int[size];
			int c = 0; // temp counter.
			Iterator<Entry<String, Integer>> iter = xmap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, Integer> entry = iter.next();
				stri[c] = entry.getKey();
				numb[c++] = entry.getValue();
			}
			QuickSort.sort(stri, numb);
			for (int j = size - 1; j >= 0; j--) {
				list.add(new MyNode(stri[j], Math.log(numb[j] * 1.0 / totalNum)));
			}
		}
	}

	/**
	 * control 1 to raise threshold, -1 to lower, 0 to signal done.
	 * 此处的minLength只是限制最后的输出，不会影响概率模型里任何概率大小。
	 * 
	 * @throws Exception
	 */
	public void generate(File output, long targetNumber) throws Exception {

		double lo = 0.0, hi = 1.0;
		int control = -1;
		while (control != 0) {
			double mid = (lo + hi) / 2;
			if (control < 0)
				mid = lo + (hi - lo) / MULT;

			double threshold = Math.log(mid);
			control = tryIterate(output, targetNumber, threshold);

			if (control > 0)
				lo = mid;
			else if (control < 0) {
				if (lo < 1E-100 && prevGenerated * 5 > targetNumber) {
					double target = targetNumber * 1.6;
					double ratio = target / prevGenerated;
					double targetProb = mid / ratio;
					hi = MULT * targetProb;
				} else
					hi = mid;
			}
		}
	}

	private class PCFGTraversal {
		private final long maxAllowed;
		private final long targetNumber;
		private final double threshold;
		private final DoublePairBuffer buffer;
		private long generated;
		private boolean stoppedEarly;

		public PCFGTraversal(long targetNumber, double threshold_lo,
				DoublePairBuffer buffer) {
			this.threshold = threshold_lo;
			this.targetNumber = targetNumber;
			/*
			 * 每个distinct的口令数目远小于maxAllowed。
			 * 所以generated按照非distinct个数累加时，不用考虑加过头的问题。
			 */
			this.maxAllowed = Math.round(targetNumber * OVERSHOOT);
			this.buffer = buffer;
			this.generated = 0;// 从distinct的记数改成非distinct的
			this.stoppedEarly = false;
		}

		/**
		 * Guess using the generated grammar and the dictionary(trainingSet).
		 */
		public void guess() throws Exception {

			/*
			 * Initialize the values in the priority queue: priorityQ. This
			 * time, MyNode is fully used.
			 */
			Pattern pattern = Pattern.compile("(d+|s+|l+|u+)");
			Matcher matcher;
			double probability;
			int len;
			String structure, preTerm = "", str; // str is the child structure.
			MyNode tmp = null;
			for (MyNode base : baseS) {
				if (generated > maxAllowed) {
					stoppedEarly = true;
					break;
				}

				structure = base.getBaseStruct();
				matcher = pattern.matcher(structure);
				probability = base.getP();
				preTerm = "";

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
					probability += tmp.getP();
				}// end while (match.find())

				if (probability >= threshold) {
					MyNode entry = new MyNode();
					entry.setBaseStruct(structure);
					entry.setP(probability);
					entry.setPreTerminal(preTerm);
					
					if (buffer != null) {
						buffer.insert(preTerm, probability);
						int tmpcount = (int) (targetNumber * Math.exp(probability));
						int curcount = tmpcount > 0 ? tmpcount : 1;
						generated += curcount;
					}
					guessNext(entry, 0);
					
					
				} else {
					stoppedEarly = true;
				}
			}
		}

		/**
		 * Used by guess() poll the first entry of the priorityQ and insert
		 * other grammars into the priorityQ.
		 */
		private void guessNext(MyNode entry, int counter) throws Exception {
			if (generated > maxAllowed) {
				stoppedEarly = true;
				return;
			}

			double probability = entry.getP();
			String preTerm = entry.getPreTerminal();

			// todo 同一个guessNext(entry)来的structure是相同的，可以再简化
			String structure = entry.getBaseStruct();
			int len; // temp child grammar length.
			String str; // temp child grammar.
			Pattern pattern = Pattern.compile("(d+|l+|u+|s+)");
			Matcher matcher = pattern.matcher(structure);

			int pivot = -1;
			int index = 0;
			while (matcher.find()) {
				pivot++;
				str = matcher.group();
				len = str.length();

				if (pivot == counter) {
					List<MyNode>[] list = null;
					
					if (str.matches("l+"))
						list = lowerList;
					else if (str.matches("u+"))
						list = upperList;
					else if (str.matches("d+"))
						list = digitList;
					else if (str.matches("s+"))
						list = symbolList;

					MyNode newEntry, replaceNode;
					int listlen = list[len].size();
					//todo 直接和第一项相减更优，但为了和pcfgbased结果保持一致写成以下
					double oldProb  = list[len].get(0).getP();
					double newProb = 0;
					
					guessNext(entry, counter + 1);
					for (int i = 1; i < listlen; i++) {
						replaceNode = list[len].get(i);
						// for setting threshold
						newProb =  probability - oldProb+ replaceNode.getP();
						if (newProb >= threshold) {
							newEntry = new MyNode();
							newEntry.setBaseStruct(structure);
							newEntry.setP(newProb);
							newEntry.setPreTerminal(preTerm.substring(0, index)
									+ replaceNode.getBaseStruct()
									+ preTerm.substring(index + len));
							
							if (buffer != null) {
								buffer.insert(newEntry.getPreTerminal(), newProb);
								int tmpcount = (int) (targetNumber * Math.exp(newProb));
								int curcount = tmpcount > 0 ? tmpcount : 1;
								generated += curcount;
							}
							guessNext(newEntry, counter + 1);
						} else {
							stoppedEarly = true;
							return;
						}
						probability= newProb;
						oldProb = replaceNode.getP();
					}
					break;
				}
				index += len;
			}
		}
	}

	private long prevGenerated = 0;

	private int tryIterate(File output, long targetNumber, double threshold)
			throws Exception {
		File tmpDir = new File(Constants.workingDir, "tmp-buffer");
		DoublePairBuffer buffer = new DoublePairBuffer(tmpDir, (int) 1E6);
		PCFGTraversal pcfgt = new PCFGTraversal(targetNumber, threshold, buffer);

		if (Constants.debug)
			// System.out.printf("Trying threshold %.2f\n", threshold);
			System.out.println("Trying threshold " + Math.exp(threshold));

		long startTime = System.currentTimeMillis();
		pcfgt.guess();

		if (Constants.debug) {
			System.out.printf("%d passwords generated with threshold in %s\n",
					pcfgt.generated, operateTime(System.currentTimeMillis()
							- startTime));
		}

		prevGenerated = pcfgt.generated;

		if (prevGenerated > pcfgt.maxAllowed) {
			if (Constants.debug) {
				System.out.println("Too many password generated.");
			}
			return 1;
		}

		if (prevGenerated >= targetNumber || !pcfgt.stoppedEarly) {
			if (Constants.debug)
				System.out.println("Stopping");

			startTime = System.currentTimeMillis();

			buffer.flush();
			buffer.write(output);
			System.out.printf(" flush and write Stopped in %s\n",
					operateTime(System.currentTimeMillis() - startTime));

			return 0;
		}

		if (Constants.debug)
			System.out.println("Not enough passwords generated.");

		return -1;
	}

	/**
	 * train
	 * 
	 * @param trainingSet
	 * @throws Exception
	 */
	public void train(String trainingSet) throws Exception {

		/* get template by counting */
		// System.out.println("Generating structure Grammar....");
		genGrammar(trainingSet);

		/* get instantiation by counting */
		// System.out.println("Genrating lower, upper, digits and symbols instantiation ....");
		trainInstantiation(trainingSet);

	}

	/**
	 * Using the training file to get the BASE grammars. directly counting will
	 * not generate too large number of template, so no need to set threshold
	 */
	public void genGrammar(String trainingSet) throws Exception {
//		if (Constants.debug)
//			System.out.println("Generate base structures...");

		// store the structures and their occurrences
		HashMap<String, Integer> structuremap = new HashMap<String, Integer>();
		long totalTrainingCount = 0;// 训练集总共多少口令

		/*
		 * Write the first file: structures.txt which includes the structures
		 * with their probability in descending order.
		 */
		BufferedReader br = new BufferedReader(new FileReader(new File(
				trainingSet + ".txt"))); // Read the training file.

		String line = br.readLine();

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
					if (split.length == 2) {
						freq = Integer.parseInt(split[1]);
					} else {
						freq = 1;
					}
					totalTrainingCount += freq;
					structure = getStruct(passwd);
					Integer x = structuremap.get(structure);
					if (x == null)
						structuremap.put(structure, freq);
					else
						structuremap.put(structure, x + freq);
				}
			}

			line = br.readLine();
		}
		br.close();

		/** Read base structure(template) first. */
		Iterator<Entry<String, Integer>> iter = structuremap.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Integer> entry = iter.next();
			baseS.add(new MyNode(entry.getKey(), Math.log(entry.getValue()
					* 1.0 / totalTrainingCount)));
		}
//		if (Constants.debug)
//			System.out.println("Base structures have been generated.");
	}

	/**
	 * Generate the probabilities of lowers, uppers, digits and symbols.
	 */

	public void trainInstantiation(String trainingSet) throws Exception {

		BufferedReader br = new BufferedReader(new FileReader(new File(
				trainingSet + ".txt")));

		HashMap<String, Integer>[] dmap = new HashMap[MAXLEN];
		HashMap<String, Integer>[] smap = new HashMap[MAXLEN];
		HashMap<String, Integer>[] lmap = new HashMap[MAXLEN];
		HashMap<String, Integer>[] umap = new HashMap[MAXLEN];

		int[] totalDigit = new int[MAXLEN];
		int[] totalSymbol = new int[MAXLEN];
		int[] totalLower = new int[MAXLEN];
		int[] totalUpper = new int[MAXLEN];

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
					if (split.length == 2) {
						freq = Integer.parseInt(split[1]);
					} else {
						freq = 1;
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
							// put instantiation to number segment
							add2Map(seg2, dmap[seg2Len], freq);
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

		/**
		 * Read structures: digitList, lowerList, upperList and symbolList. sort
		 * and put these to node list
		 **/
		for (int i = 0; i < MAXLEN; i++) {
			// Read digit.
			if (dmap[i] != null) {
				digitList[i] = new LinkedList<MyNode>();
				readToList(dmap[i], digitList[i], totalDigit[i]);
			}

			// Read Symbol
			if (smap[i] != null) {
				symbolList[i] = new LinkedList<MyNode>();
				readToList(smap[i], symbolList[i], totalSymbol[i]);
			}

			// Read Lower
			if (lmap[i] != null) {
				lowerList[i] = new LinkedList<MyNode>();
				readToList(lmap[i], lowerList[i], totalLower[i]);
			}

			// Read Upper
			if (umap[i] != null) {
				upperList[i] = new LinkedList<MyNode>();
				readToList(umap[i], upperList[i], totalUpper[i]);
			}
		}
	}

	private static void add2Map(String str, HashMap<String, Integer> map,
			int freq) {
		Integer x = map.get(str);
		map.put(str, (x == null) ? freq : x + freq);
	}
}
