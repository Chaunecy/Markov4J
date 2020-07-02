package pwdutils;

import java.io.File;
import java.util.HashSet;

public class Constants {
	public static final String TOKEN = "\t";
	public static final int LEN_LIMIT = 40;
	public static final boolean debug = true;
	public static File workingDir;

	public static void main(String[] args){
		System.out.println(checkPasswd("qq51521258"));
		for(int i =0;i<ALLCHARS.length;i++){
			System.out.print(i+"="+ALLCHARS[i]+";");
			if(i%10==0){
				System.out.println();
			}
		}
		
	}
	public static boolean checkPasswd(String passwd){
		if (passwd.length() < 4 || passwd.length() > LEN_LIMIT)
			return false;
		
		char[] charArray = passwd.toCharArray();
		for (char c : charArray)
			if (Character.isISOControl(c) || !Character.isDefined(c) || c >= 128)
				return false;
		return true;
	}

	public static final char NULLCHAR = (char) 0;
    public static final char ENDCHAR = (char) 3;
    public static final char STARTCHAR = (char) 2;
    public static final double EPSILON = 1E-12;

    public static final char [] ALLCHARS = generateAllChars();		// All of the printable characters.
    public static final HashSet<Character> ALLCHARSET = getCharSet(ALLCHARS);		// All of the printable characters.

    /**
     * the end char and 95 printable characters()
     * 0=;
	   1= ;2=!;3=";4=#;5=$;6=%;7=&;8=';9=(;10=);
	   11=*;12=+;13=,;14=-;15=.;16=/;17=0;18=1;19=2;20=3;
	   21=4;22=5;23=6;24=7;25=8;26=9;27=:;28=;;29=<;30==;
	   31=>;32=?;33=@;34=A;35=B;36=C;37=D;38=E;39=F;40=G;
	   41=H;42=I;43=J;44=K;45=L;46=M;47=N;48=O;49=P;50=Q;
	   51=R;52=S;53=T;54=U;55=V;56=W;57=X;58=Y;59=Z;60=[;
	   61=\;62=];63=^;64=_;65=`;66=a;67=b;68=c;69=d;70=e;
	   71=f;72=g;73=h;74=i;75=j;76=k;77=l;78=m;79=n;80=o;
	   81=p;82=q;83=r;84=s;85=t;86=u;87=v;88=w;89=x;90=y;
	   91=z;92={;93=|;94=};95=~;
     * @return
     */
    private static char [] generateAllChars () {
        String s = Character.toString(ENDCHAR);
        for (char c = (char) 0; c < 128; c ++) {
            if (Character.isDefined(c) && !Character.isISOControl(c))
                s += c;
        }
        return s.toCharArray();
    }

    private static HashSet<Character> getCharSet(char [] chars) {
        HashSet<Character> set = new HashSet<Character>(chars.length + 1, 0.1F);
        for (char c : chars)
            set.add(c);
        return set;
    }
    
    public static double log2(double num){
    	return Math.log(num) / Math.log(2);
    }
    
    public static String operateTime(long time){
    	long ms;
		long s;
		long min;
		long h;
		long day;
		
		ms = time % 1000;
		s = time / 1000;
		
		min = s / 60;
		s = s % 60;
		
		h = min / 60;
		min = min % 60;
		
		day = h / 24;
		h = h % 24;

		if (day != 0)
			return day + " day " + h + " h";

		if (h != 0)
			return h + " h " + min + " min";
		
		if (min != 0)
			return min + " min " + s + " s";
		
		if (s != 0)
			return s + " s " + ms + " ms";
		
		return ms + " ms";
    }	
	/**
	 * Find the last appearance of token in source. return the index.
	 * -1 for no appearance.
	 * @param source
	 * @param token
	 * @return
	 */
	public static int findLast(String source, String token){
		int index = source.indexOf(token);
		if (index < 0)
			return index;
		
		String last = source.substring(index+2);
		int lastindex = index;
		
		while (last.length() > 0){
			index = last.indexOf(token);
			if (index < 0)
				return lastindex;
			
			lastindex = index + token.length() + lastindex;
			last = last.substring(index+2);
		}
		
		return lastindex;
	}
}
