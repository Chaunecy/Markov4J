package pwdutils;

public class MyNode {
	private String baseStruct;
	private String preTerminal;
	private int pivot=0;
	private double p;
	
	//2016.7.2 change from  20 to 41
	public int[] posCounter = new int[41];	// This counter count where it goes for each child structure when we using the priority queue.
	
	
	public MyNode(){
		initPosCounter();
	}

	public MyNode(String baseStruct, double p){
		initPosCounter();
		this.baseStruct = baseStruct;
		this.p = p;
	}
	
	public void setBaseStruct(String str){
		baseStruct = str;
	}
	
	public String getBaseStruct(){
		return baseStruct;
	}
	
	public void setPreTerminal(String str){
		preTerminal = str;
	}
	
	public String getPreTerminal(){
		return preTerminal;
	}
	
	public void setP(double p){
		this.p = p;
	}
	
	public double getP(){
		return p;
	}
	
	public void setPivot(int pivot){
		this.pivot = pivot;
	}
	
	public int getPivot(){
		return pivot;
	}
	
	private void initPosCounter(){
		for (int i = 0, len = posCounter.length; i < len; i++){
			posCounter[i] = 0;	// The first one is 0 and has been used in the pre-Terminal. 
		}
	}
}
