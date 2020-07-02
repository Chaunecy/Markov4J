package pwdutils;

import java.io.*;


public class IdentifyPinyin {
	public static String[] PINYIN = {"a","ai","an","ao",
			"ba","bai","ban","bang","bao","bei","ben","beng","bi","bian","biao","bie","bin","bing","bo","bu",
			"ca","cai","can","cang","cao","ce","cen","ceng","cha","chai","chan","chang","chao","che","chen","cheng",
			"chi","chong","chou","chu","chuai","chuan","chuang","chui","chun","chuo","ci","cong","cou","cu","cuan","cui",
			"cun","cuo","da","dai","dan","dang","dao","de","dei","deng","di","dia","dian","diao","die","ding","diu",
			"dong","dou","du","duan","dui","dun","duo","e","en","eng","er","fa","fan","fang","fei","fen","feng","fo",
			"fou","fu","ga","gai","gan","gang","gao","ge","gei","gen","geng","gong","gou","gu","gua","guai","guan","guang",
			"gui","gun","guo","ha","hai","han","hang","hao","he","hei","hen","heng","hong","hou","hu","hua","huai","huan",
			"huang","hui","hun","huo","ji","jia","jian","jiang","jiao","jie","jin","jing","jiong","jiu","ju","juan","jue",
			"ka","kai","kan","kang","kao","ke","ken","keng","kong","kou","ku","kua","kuai","kuan","kuang","kui","kun","kuo",
			"la","lai","lan","lang","lao","le","lei","leng","li","lia","lian","liang","liao","lie","lin","ling","liu","lo",
			"long","lou","lu","luan","lun","luo","lv","lue","ma","mai","man","mang","mao","me","mei","men","meng","mi","mian",
			"miao","mie","min","ming","miu","mo","mou","mu","na","nai","nan","nang","nao","ne","nei","nen","neng","ni","nian",
			"niang","niao","nie","nin","ning","niu","nong","nou","nu","nuan","nuo","nv","nue","o","ou","pa","pai","pan","pang",
			"pao","pei","pen","peng","pi","pian","piao","pie","pin","ping","po","pou","pu","qi","qia","qian","qiang","qiao",
			"qie","qin","qing","qiong","qiu","qu","quan","que","qun","ran","rang","rao","re","ren","reng","ri","rong","rou",
			"ru","rua","ruan","rui","run","ruo","sa","sai","san","sang","sao","se","sen","seng","sha","shai","shan","shang",
			"shao","she","shen","sheng","shi","shou","shu","shua","shuai","shuan","shuang","shui","shun","shuo","si","song",
			"sou","su","suan","sui","sun","suo","ta","tai","tan","tang","tao","te","tei","teng","ti","tian","tiao","tie",
			"ting","tong","tou","tu","tuan","tui","tun","tuo","wa","wai","wan","wang","wei","wen","weng","wo","wu","xi",
			"xia","xian","xiang","xiao","xie","xin","xing","xiong","xiu","xu","xuan","xue","xun","ya","yan","yang","yao",
			"ye","yi","yin","ying","yo","yong","you","yu","yuan","yue","yun","za","zai","zan","zang","zao","ze","zei","zen",
			"zeng","zha","zhai","zhan","zhang","zhao","zhe","zhen","zheng","zhi","zhong","zhou","zhu","zhua","zhuai","zhuan",
			"zhuang","zhui","zhun","zhuo","zi","zong","zou","zu","zuan","zui","zun","zuo"};
	public static String[] QUANPIN = {"a","ai","an","ang","ao",
		"ba","bai","ban","bang","bao","bei","ben","beng","bi","bian","biao","bie","bin","bing","bo","bu",
		"ca","cai","can","cang","cao","ce","cen","ceng","cha","chai","chan","chang","chao","che","chen","cheng",
		"chi","chong","chou","chu","chuai","chuan","chuang","chui","chun","chuo","ci","cong","cou","cu","cuan","cui",
		"cun","cuo","da","dai","dan","dang","dao","de","dei","deng","di","dia","dian","diao","die","ding","diu",
		"dong","dou","du","duan","dui","dun","duo","e","ei","en","eng","er","fa","fan","fang","fei","fen","feng","fo",
		"fou","fu","ga","gai","gan","gang","gao","ge","gei","gen","geng","gong","gou","gu","gua","guai","guan","guang",
		"gui","gun","guo","ha","hai","han","hang","hao","he","hei","hen","heng","hong","hou","hu","hua","huai","huan",
		"huang","hui","hun","huo","ji","jia","jian","jiang","jiao","jie","jin","jing","jiong","jiu","ju","juan","jue","jun",
		"ka","kai","kan","kang","kao","ke","kei","ken","keng","kong","kou","ku","kua","kuai","kuan","kuang","kui","kun","kuo",
		"la","lai","lan","lang","lao","le","lei","leng","li","lia","lian","liang","liao","lie","lin","ling","liu","lo",
		"long","lou","lu","luan","lun","luo","lv","lue","ma","mai","man","mang","mao","me","mei","men","meng","mi","mian",
		"miao","mie","min","ming","miu","mo","mou","mu","na","nai","nan","nang","nao","ne","nei","nen","neng","ni","nian",
		"niang","niao","nie","nin","ning","niu","nong","nou","nu","nuan","nuo","nv","nue","o","ou","pa","pai","pan","pang",
		"pao","pei","pen","peng","pi","pian","piao","pie","pin","ping","po","pou","pu","qi","qia","qian","qiang","qiao",
		"qie","qin","qing","qiong","qiu","qu","quan","que","qun","ran","rang","rao","re","ren","reng","ri","rong","rou",
		"ru","rua","ruan","rui","run","ruo","sa","sai","san","sang","sao","se","sen","seng","sha","shai","shan","shang",
		"shao","she","shei","shen","sheng","shi","shou","shu","shua","shuai","shuan","shuang","shui","shun","shuo","si","song",
		"sou","su","suan","sui","sun","suo","ta","tai","tan","tang","tao","te","tei","teng","ti","tian","tiao","tie",
		"ting","tong","tou","tu","tuan","tui","tun","tuo","wa","wai","wan","wang","wei","wen","weng","wo","wu","xi",
		"xia","xian","xiang","xiao","xie","xin","xing","xiong","xiu","xu","xuan","xue","xun","ya","yan","yang","yao",
		"ye","yi","yin","ying","yo","yong","you","yu","yuan","yue","yun","za","zai","zan","zang","zao","ze","zei","zen",
		"zeng","zha","zhai","zhan","zhang","zhao","zhe","zhen","zheng","zhi","zhong","zhou","zhu","zhua","zhuai","zhuan",
		"zhuang","zhui","zhun","zhuo","zi","zong","zou","zu","zuan","zui","zun","zuo"};
	
	public static void main(String[] args) throws IOException{
		
		
		CaseInsensitiveTrie cit = new CaseInsensitiveTrie();
		
		for (int i = 0, len = PINYIN.length; i< len; i++){
			cit.insert(PINYIN[i]);
		}
		
		long x = System.currentTimeMillis();
		
		System.out.println(cit.canCompose("nihao"));
		System.out.println(cit.canCompose("tianmel"));
		System.out.println(cit.canCompose("likeyou"));
		System.out.println(cit.canCompose("loveyou"));
		System.out.println(cit.canCompose("dictionary"));
		System.out.println(cit.canCompose("changhal"));
		
		System.out.println("TIME(ms):" + (System.currentTimeMillis()-x));
	}
	
	/**
	 * Simple method to test if a str is composed of Piniyin. Do not use this method beacuse of its low efficiency.
	 * @param str
	 * @return
	 */
	public static boolean isPinyin(String str){
		
		boolean result = false;
		int length = PINYIN.length;
		
		/* VERY IMPORTANT! */
		str = str.toLowerCase();
		
		for (int i = 0; i < length; i++){
			if (str.startsWith(PINYIN[i])){
				if (PINYIN[i].length() == str.length()){
					result = true;
					break;
				}
				else{
					result = isPinyin(str.substring(PINYIN[i].length()));
					if (result == true)
						break;
				}
			}
		}
		return result;
	}
}