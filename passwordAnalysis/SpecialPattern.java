
package passwordAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import pwdutils.CaseInsensitiveTrie;
import pwdutils.IdentifyPinyin;

public class SpecialPattern {
	
	public static final int Pattern = 0;//特殊结构模式
	public static final int KeyBoard = 1;//键盘模式
	public static final int Date = 2;//日期
	public static final int PinyinWord = 3;//拼音或单词
	
	static KeyBoardPattern kbp;
	
	public static void main(String[] args) throws Exception{
		kbp = new KeyBoardPattern();
		/*Build Pinyin Trie*/
		PinyinWords.PYTrie = new CaseInsensitiveTrie();
		for (int i =0, len = IdentifyPinyin.PINYIN.length; i < len; i++){
			PinyinWords.PYTrie.insert(IdentifyPinyin.PINYIN[i]);
		}
		PinyinWords.EWTrie = new CaseInsensitiveTrie("Oxford.txt");
		/*排除一定不是的日期*/
		String[] falsepositive={"111111", "123123", "111000", "112233","100200",
				"111222", "121212", "520520", "110110", "123000",
				"101010", "111333","110120", "102030", "110119",
				"121314", "521125", "120120", "010203", "122333",
				"121121", "101101", "131211", "100100", "321123", 
				"110112", "112211", "111112","520521", "110111"};
		for(String fps:falsepositive){
			DatePattern.fplist.add(fps);
		}
		
		/*for whole dataset*/
//		String[] fileSet = {"csdn"};
		String[] fileSet = {"csdn","duduniu","rockyou","yahoo"};
		for (String filename : fileSet){
			filename+="_pwd_count";
			System.out.println("\n not unique pattern of "+filename+"...");
			countPwdSpecialPattern(filename);
			System.out.println("\n unique pattern of "+filename+"...");
			countUniquePwdSpecialPattern(filename);
		}
		
		/*for sample and guess result*/
		String dataSet = "csdn";
//		String dataSet = "duduniu";
//		String dataSet = "rockyou";
//		String dataSet = "yahoo";
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
					
					System.out.println("\n not unique pattern of "+filename+"...");
					countPwdSpecialPattern(filename);
					System.out.println("\n unique pattern of "+filename+"...");
					countUniquePwdSpecialPattern(filename);
				}
			}
		}

	}

	private static void countUniquePwdSpecialPattern(String filename) throws Exception {
		int total =0;
		int[] specialpatterns = new int[4];
		int p1=0;
		int p2=0;
		int p1p2 = 0;

		BufferedReader br = new BufferedReader(new FileReader(new File(filename
				+ ".txt")));

		String line;
		String passwd;
		String[] split;
		boolean b1,b2,b3,b4;
		while ((line = br.readLine()) != null) {
			if(line.length()>0){
				split = line.split("\t");
				passwd = split[0];
				if (passwd == null || passwd.length() == 0)
					continue;
				total++;
				
				b1 = isPattern(passwd);
				b2 = isKeyBoard(kbp, passwd);
				b3 = isDate(passwd);
				b4 = isPinyinWord(passwd);
				if(b1){
					specialpatterns[Pattern]++;
				}
				if(b2){
					specialpatterns[KeyBoard]++;
				}
				if(b3){
					specialpatterns[Date]++;
				}
				if(b4){
					specialpatterns[PinyinWord]++;
				}
				
				if(b1||b2){
					p1++;
				}
				if(b3||b4){
					p2++;
				}
				
				if(b1||b2||b3||b4){
					p1p2++;
				}
			}
		}
		br.close();

		System.out.println("Total\t" + total);
		System.out.println("Pattern\t" + specialpatterns[0]);
		System.out.println("KeyBoard\t" + specialpatterns[1]);
		System.out.println("Date\t" + specialpatterns[2]);
		System.out.println("PinyinWord\t" + specialpatterns[3]);
		System.out.println("p1\t" + p1);
		System.out.println("p2\t" + p2);
		System.out.println("p1p2\t" + p1p2);
	}
	
	private static void countPwdSpecialPattern(String filename) throws Exception {
		int total =0;
		int[] specialpatterns = new int[4];
		int p1=0;
		int p2=0;
		int p1p2 = 0;

		BufferedReader br = new BufferedReader(new FileReader(new File(filename
				+ ".txt")));

		String line;
		String passwd;
		String[] split;
		int count=0;
		boolean b1,b2,b3,b4;
		while ((line = br.readLine()) != null) {
			if(line.length()>0){
				split = line.split("\t");
				passwd = split[0];
				if (passwd == null || passwd.length() == 0)
					continue;
				if(split.length==2){
					count=Integer.parseInt(split[1]);
				}
				else if(split.length==3){
					count=Integer.parseInt(split[2]);
				}
				total+=count;
				b1 = isPattern(passwd);
				b2 = isKeyBoard(kbp, passwd);
				b3 = isDate(passwd);
				b4 = isPinyinWord(passwd);
				if(b1){
					specialpatterns[Pattern]+=count;
				}
				if(b2){
					specialpatterns[KeyBoard]+=count;
				}
				if(b3){
					specialpatterns[Date]+=count;
				}
				if(b4){
					specialpatterns[PinyinWord]+=count;
				}
				
				if(b1||b2){
					p1+=count;
				}
				if(b3||b4){
					p2+=count;
				}
				
				if(b1||b2||b3||b4){
					p1p2+=count;
				}
			}
		}
		br.close();

		System.out.println("Total\t" + total);
		System.out.println("Pattern\t" + specialpatterns[0]);
		System.out.println("KeyBoard\t" + specialpatterns[1]);
		System.out.println("Date\t" + specialpatterns[2]);
		System.out.println("PinyinWord\t" + specialpatterns[3]);
		System.out.println("p1\t" + p1);
		System.out.println("p2\t" + p2);
		System.out.println("p1p2\t" + p1p2);
	}
	
	private static boolean isPattern(String passwd) {
		if(Patterns.isSingle(passwd)||Patterns.isPair(passwd)||
				Patterns.isReverse(passwd)||Patterns.isContinuous(passwd)||
				Patterns.isRepeat(passwd)||Patterns.isNcontinuous(passwd)){
			return true;
		}
		return false;
	}
	
	private static boolean isKeyBoard(KeyBoardPattern kbp, String passwd){
		int shape = kbp.snake(passwd);
		if (shape == KeyBoardPattern.SAMEROW||shape==KeyBoardPattern.SNAKE||
				shape==KeyBoardPattern.ZIGZAG){
			return true;
		}
		return false;
	}
	
	private static boolean isDate(String passwd) {
		if(DatePattern.hasDate(passwd,6)||DatePattern.hasDate(passwd,8)||
				DatePattern.hasSepDate(passwd,6)||DatePattern.hasSepDate(passwd,8)){
			return true;
		}
		return false;
	}
	
	private static boolean isPinyinWord(String passwd){
		boolean ispure = false, iselecit = false;
		
		boolean[] isCompose = PinyinWords.pure(passwd);
		if (isCompose[0] && !isCompose[1]){
			ispure = true;
		}else if(!isCompose[0]&&isCompose[1]){
			ispure = true;
		}
		boolean[] isCompose2 = PinyinWords.elecit(passwd);
		if (isCompose2[0] && !isCompose2[1]){
			iselecit = true;
		}else if(!isCompose2[0]&&isCompose2[1]){
			iselecit = true;
		}
		
		if (ispure||iselecit){
			return true;
		}
		return false;
	}
	
}
