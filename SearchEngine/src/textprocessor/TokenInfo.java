package textprocessor;

public class TokenInfo {
	private StringBuilder positions;
    private int frequency;

	public TokenInfo() {
		this.positions = new StringBuilder();;
		this.frequency=0;
	}

	public String getPositions(){
		return this.positions.toString();
	}
	
	public int getFrequency(){
		return this.frequency;
	}
	
	public void addPosition(String position){
		this.frequency++;
		if(this.frequency == 1)
			this.positions.append(position);
		else 
			this.positions.append("," + position);
	}
}
