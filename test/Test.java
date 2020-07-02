package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
//		System.out.println(Math.exp(-22.284770));
//		System.out.println(Math.exp(-19.900183));
//		System.out.println(3%1==0);
//		System.out.println((~2)>1);
//		long n = 100;
//		System.out.println(n+=100*0.111);
//		
//		BufferedReader br = new BufferedReader(new FileReader(new File("test.txt")));
//		String line = br.readLine();
//		if(line==null){
//			System.out.println("1null");
//		}
//		if(line.length()==0){
//			System.out.println("1length=0");
//		}
//		line = br.readLine();
//		if(line==null){
//			System.out.println("2null");
//		}
//		if(line.length()==0){
//			System.out.println("2length=0");
//		}
		
		
//		List<Integer> list = new ArrayList<Integer>();
//		list.add(1);
//		list.add(2);
//		System.out.println(list.remove(0));
//		System.out.println(list.remove(0));
//		System.out.println(Math.exp(Math.log(2)));
//		long a = 4;
//		long b = 8;
//		System.out.println(a/b);
//		long targetNumber = 442287;
//		double v5 = 6.782926018625915E-6;
//		double base = 6.7829260186259125E-6;
//		
//		System.out.println(targetNumber*v5);
//		System.out.println(targetNumber*base);
		
//		digitElicit("123456789",3);
		boolean[] isCompose = new boolean[3];
		System.out.println(isCompose[0]);
	}
	private static void digitElicit(String pwd,int length) throws Exception{
				String regex = String.format("\\d{%d}", length);
//		String regex_not = String.format("\\d{%d,}", length+1);

		Pattern exactPattern = Pattern.compile(regex);
		Pattern pureDigit = Pattern.compile("\\d+");
		Matcher exactMatcher,pureMatcher;
	
			String digits = null;
		
				pureMatcher = pureDigit.matcher(pwd);

				while (pureMatcher.find()){
					digits = pureMatcher.group();
					exactMatcher = exactPattern.matcher(digits);
					if (exactMatcher.matches())
						
						System.out.println(pwd + "->" + digits + "\n");
				}

	}	
}
