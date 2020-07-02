/**
 * ------------------------------->Axis X
 * | ` 1 2 3 4 5 6 7 8 9 0 - =
 * |   q w e r t y u i o p [ ] \
 * |   a s d f g h j k l ; '
 * |   z x c v b n m , . /
 * V
 Axis Y 
 * 
 * ------------------------------->Axis X
 * | ~ ! @ # $ % ^ & * ( ) _ +
 * |   Q W E R T Y U I O P { }
 * |   A S D F G H J K L : "
 * |   Z X C V B N M < > ?
 * V
 Axis Y
 
 * The position of every character.
 */

package passwordAnalysis;

import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class KeyBoardPattern {
	static int SAMEROW = 1;
	static int ZIGZAG = 2;
	static int SNAKE = 3;

	HashMap<Character, Pos> hashmap = new HashMap<Character, Pos>();

	public KeyBoardPattern() {
		initiatePos();
	}

	private void initiatePos() {
		String[] line = new String[4];
		line[0] = "`1234567890-=";
		line[1] = " qwertyuiop[]\\";
		line[2] = " asdfghjkl;\'";
		line[3] = " zxcvbnm,./";

		String[] shiftLine = new String[4];
		shiftLine[0] = "~!@#$%^&*()_+";
		shiftLine[1] = " QWERTYUIOP{}|";
		shiftLine[2] = " ASDFGHJKL:\"";
		shiftLine[3] = " ZXCVBNM<>?";

		for (int i = 0; i < 4; i++) {
			char[] lineset = line[i].toCharArray();
			char[] shiftset = shiftLine[i].toCharArray();
			if (lineset.length == shiftset.length) {
				for (int j = 0, len = lineset.length; j < len; j++) {
					hashmap.put(lineset[j], new Pos(j, i));
					hashmap.put(shiftset[j], new Pos(j, i));
				}
			} else {
				System.out.println("WRONG");
			}
		}

		hashmap.put(' ', new Pos(-2, -2)); // Actually, we ignore SPACE.
	}

	public Pos getPos(char c) {
		return hashmap.get(c);
	}

	/**
	 * 2 3 |/ 1- x -4 /| 6 5 The position in each number are treated as adjacent
	 * to x. And the return value illustrate the position(y to x). 0 means they
	 * are not adjacent. For example , x's position is (1,1) and y's is (2,1),
	 * then adjacent(x,y) will return 4. return 0 if x and y are int the same
	 * position.
	 */
	public int adjacent(char x, char y) {
		Pos xPos = hashmap.get(x);
		Pos yPos = hashmap.get(y);

		if (xPos == null || yPos == null) {
			return -1;
		}

		if (xPos.x == yPos.x && xPos.y == yPos.y)
			return 0;

		if (xPos.x - 1 == yPos.x && xPos.y == yPos.y)
			return 1;

		if (xPos.x == yPos.x && xPos.y == yPos.y + 1)
			return 2;

		if (xPos.x == yPos.x - 1 && xPos.y == yPos.y + 1)
			return 3;

		if (xPos.x + 1 == yPos.x && xPos.y == yPos.y)
			return 4;

		if (xPos.x == yPos.x && xPos.y + 1 == yPos.y)
			return 5;

		if (xPos.x - 1 == yPos.x && xPos.y + 1 == yPos.y)
			return 6;

		return -1;
	}

	/**
	 * Snake pattern: Sequence of contiguous keys. 0 : the password is not in a
	 * snake pattern. 1 : the passwords are all in the same row. 2 : the
	 * passwords are zig-zag. 3 : otherwise.
	 * 
	 * @return
	 */
	public int snake(String passwd) {
		if (passwd == null || passwd.length() < 3) {
			return 0;
		}
		char[] passwdset = passwd.toCharArray();
		boolean snake = true;
		boolean samerow = true;
		boolean zigzag = true;
		// boolean loop; // We do not use this type.
		int re;
		for (int i = 1, len = passwdset.length; i < len; i++) {
			re = adjacent(passwdset[i - 1], passwdset[i]);
			if (re == 0)
				continue;
			if (re > 0) {
				samerow = samerow && ((re == 1) || (re == 4));
				zigzag = zigzag && ((re != 1) && (re != 4));
			} else
				return 0;
		}

		if (samerow)
			return SAMEROW;
		else if (zigzag)
			return ZIGZAG;
		else if (snake)
			return SNAKE;
		else
			return 0;
	}

	public static void main(String[] args) throws Exception {
		/*for whole dataset*/
//		String[] fileSet = {"csdn","duduniu","rockyou","yahoo"};
//		for (String filename : fileSet){
//			filename+="_pwd_count";
//			System.out.println("\n not unique keyboard patter of "+filename+"...");
//			countPwdKBP(filename);
//			System.out.println("\n unique keyboard patter of "+filename+"...");
//			countUniquePwdKBP(filename);
//		}
		
		/*for sample and guess result*/
//		String dataSet = "csdn";
//		String dataSet = "duduniu";
//		String dataSet = "rockyou";
		String dataSet = "yahoo";
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
					
					System.out.println("\n not unique keyboard patter of "+filename+"...");
					countPwdKBP(filename);
					System.out.println("\n unique keyboard patter of "+filename+"...");
					countUniquePwdKBP(filename);
				}
			}
		}

	}

	/**
	 * This method should not be here. It's not the algorithm, it's the
	 * application.
	 * 
	 * @param set
	 * @throws Exception
	 */
	private static void countUniquePwdKBP(String filename) throws Exception {
		int snake = 0;
		int samerow = 0;
		int zigzag = 0;
		int total = 0;
		KeyBoardPattern kbp = new KeyBoardPattern();

		BufferedReader br = new BufferedReader(new FileReader(new File(filename
				+ ".txt")));

		String line;
		String passwd;
		int shape;
		String[] split;
//		Pattern letter = Pattern.compile("[a-zA-Z]+");
		while ((line = br.readLine()) != null) {
			if(line.length()>0){
				split = line.split("\t");
				passwd = split[0];
				if (passwd == null || passwd.length() == 0)
					continue;
	
				/* Filter - no fileter */
				total++;
				shape = kbp.snake(passwd);
				if (shape == SAMEROW)
					samerow++;
				else if (shape == ZIGZAG)
					zigzag++;
				else if (shape == SNAKE)
					snake++;
				/* Fileter - pure digits */
				// if (passwd.matches("[0-9]+")){
				// total+=count;
				// shape = kbp.snake(passwd);
				// if (shape == SAMEROW)
				// samerow+=count;
				// else if (shape == ZIGZAG)
				// zigzag+=count;
				// else if (shape == SNAKE)
				// snake+=count;
				// }
	
				// Filter - delete digit & letter
				// if (!passwd.matches("[a-zA-Z]") &&
				// letter.matcher(passwd).find()){
				// passwd = passwd.replaceAll("[^a-zA-Z]", "");
				// total++;
				// shape = kbp.snake(passwd);
				// if (shape == SAMEROW)
				// samerow++;
				// else if (shape == ZIGZAG)
				// zigzag++;
				// else if (shape == SNAKE)
				// snake++;
				// }
			}
		}

		br.close();

		System.out.println("Total\t" + total);
		System.out.println("SAMEROW\t" + samerow);
		System.out.println("ZIGZAG\t" + zigzag);
		System.out.println("SNAKE\t" + snake);

	}
	
	private static void countPwdKBP(String filename) throws Exception {
		int snake = 0;
		int samerow = 0;
		int zigzag = 0;
		int total = 0;
		KeyBoardPattern kbp = new KeyBoardPattern();

		BufferedReader br = new BufferedReader(new FileReader(new File(filename
				+ ".txt")));

		String line;
		String passwd;
		int shape;
		int count=0;
		String[] split;
//		Pattern letter = Pattern.compile("[a-zA-Z]+");
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
				/* Filter - no fileter */
				total+=count;
				shape = kbp.snake(passwd);
				if (shape == SAMEROW)
					samerow+=count;
				else if (shape == ZIGZAG)
					zigzag+=count;
				else if (shape == SNAKE)
					snake+=count;
				/* Fileter - pure digits */
				// if (passwd.matches("[0-9]+")){
				// total+=count;
				// shape = kbp.snake(passwd);
				// if (shape == SAMEROW)
				// samerow+=count;
				// else if (shape == ZIGZAG)
				// zigzag+=count;
				// else if (shape == SNAKE)
				// snake+=count;
				// }
	
				// Filter - delete digit & letter
				// if (!passwd.matches("[a-zA-Z]") &&
				// letter.matcher(passwd).find()){
				// passwd = passwd.replaceAll("[^a-zA-Z]", "");
				// total++;
				// shape = kbp.snake(passwd);
				// if (shape == SAMEROW)
				// samerow++;
				// else if (shape == ZIGZAG)
				// zigzag++;
				// else if (shape == SNAKE)
				// snake++;
				// }
			}
		}

		br.close();

		System.out.println("Total\t" + total);
		System.out.println("SAMEROW\t" + samerow);
		System.out.println("ZIGZAG\t" + zigzag);
		System.out.println("SNAKE\t" + snake);

	}
}

class Pos {
	int x;
	int y;

	public Pos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void print() {
		System.out.println(String.format("(%d,%d)", x, y));
	}
}