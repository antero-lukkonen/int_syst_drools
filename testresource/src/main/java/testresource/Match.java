package testresource;

public class Match {
	private Machine value;

	public Match(Machine machine) {
		this.value = machine;
	}

	public Machine getValue() {
		return value;
	}
	

		
	@Override
	public boolean equals(Object obj) {
		Match x = (Match) obj;
		return this.getValue() == x.getValue();		
	}
}
