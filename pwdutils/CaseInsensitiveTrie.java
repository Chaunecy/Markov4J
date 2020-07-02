package pwdutils;


import java.io.*;

public class CaseInsensitiveTrie {
	public class TrieNode {
		
		private TrieNode[] son;
		private boolean isValue;//is end of a word	
		private char val;
		
		TrieNode(){
			son = new TrieNode[26];
			isValue = false;
		}
		
	}
	
	private TrieNode root;
	public CaseInsensitiveTrie(){
		root = new TrieNode();
	}
	public CaseInsensitiveTrie(String filename) throws IOException{
		root = new TrieNode();
		BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
		String line = br.readLine();
		
		while(line != null){
			if (line.length() > 0){
				insert(line.trim());
			}
			line = br.readLine();
		}
		
		br.close();
	}
	public void insert(String str){
		
		if (str == null || str.length() ==0)
			return;
		
		/* VERY IMPORTANT! */
		str = str.toLowerCase();
		TrieNode node = root;
		char[] letters = str.toCharArray();
		
		try{
			for (int i = 0, len = str.length(); i < len; i++){

				int pos = letters[i] -'a';			
				if (node.son[pos] == null){
					node.son[pos] = new TrieNode();
					node.son[pos].val = letters[i];
				}
				
				node = node.son[pos];
			}
		}catch(ArrayIndexOutOfBoundsException x){
			System.out.println();
		}
		node.isValue = true;
		
	}
	
	/**
	 * 整个str由trie树种的单词组成
	 * @return
	 */
	public boolean canCompose(String str){
		boolean result = false;
		//System.out.println("str="+str);
		if (str == null || str.length() == 0)
			return result;
		
		/* VERY IMPORTANT */
		str = str.toLowerCase();
		
		char[] letters = str.toCharArray();
		
		TrieNode node = root;
		
		for (int i = 0, len = letters.length; i < len; i++){
			int pos = letters[i] - 'a';
			
			if (node.son[pos] == null){
				if (i == 0)
					return false;
				
				if (node.isValue == false)
					return false;
				
				return result = canCompose(str.substring(i));
			}
			
			else{
				
				node = node.son[pos];
				
				if (node.isValue){
					if (canCompose(str.substring(i+1)))
						return true;
				}
			}
		}
		
		return node.isValue;
	}
	
	public static void main(String[] args) throws IOException{
		CaseInsensitiveTrie cit = new CaseInsensitiveTrie();
		cit.insert("CA");
		cit.insert("DF");
		cit.insert("nn");
		
		/*System.out.println(cit.canCompose("CADF"));
		System.out.println(cit.canCompose("DDCA"));
		System.out.println(cit.contain("DDSCA"));*/
		System.out.println(cit.canCompose("nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"));
		
		/*CaseInsensitiveTrie IDIOMTrie = new CaseInsensitiveTrie();
		BufferedReader br_Initial = new BufferedReader(new FileReader(new File("toponymyAcronymquanpin1.txt")));
		String tmp = br_Initial.readLine();
		//while (tmp != null){
			IDIOMTrie.insert(tmp);
			tmp = br_Initial.readLine();
		//}
		br_Initial.close();
		System.out.println(IDIOMTrie.root.isValue);
		System.out.println(IDIOMTrie.canCompose("fargocat"));
		System.out.println(IDIOMTrie.contain("qaz"));*/
	}
	
}