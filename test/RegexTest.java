package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
////		String passwd = "1199-11-11";
//		String passwd = "19-11-1211";
//		Pattern pureDigit = Pattern.compile("(\\d{4}[-./]\\d{2}[-./]\\d{2})|(\\d{2}[-./]\\d{2}[-./]\\d{4})");
//		Matcher pureMatcher;
//
//		pureMatcher = pureDigit.matcher(passwd);
//
//		String tmp;
//		while (pureMatcher.find()){
//			tmp = pureMatcher.group();
//			System.out.println("tmp="+tmp);
//		}
//		
		Pattern patternSingle = Pattern.compile("(.)\\1*");
//		Pattern YYMMDD = Pattern.compile("\\d\\d((0[3578]|1[02])([0-2][0-9]|30|31)|(04|06|09|11)([0-2][0-9]|30)|02[0-2][0-9])");
		Pattern YYMMDD = Pattern.compile("\\d\\d((0[13578]|1[02])([0-2][0-9]|30|31)|(04|06|09|11)([0-2][0-9]|30)|02[0-2][0-9])");

		System.out.println(YYMMDD.matcher("520520").matches());
		System.out.println(patternSingle.matcher("").matches());
	}

}
