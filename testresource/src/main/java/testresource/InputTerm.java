package testresource;

public class InputTerm {
	protected final String id;
	
	public InputTerm(String id) {
		this.id = id;
	}
	
	public String getId() {		
		return this.id;
	}
		
	@Override
	public boolean equals(Object obj) {
		InputTerm other = (InputTerm) obj;
		return this.getClass() == obj.getClass() && this.id == other.getId();		
	}
	
	@Override
	public String toString() {
	    return this.getClass().getName() + ":" + this.id;
	}
}
