package testresource;

public class BaseRequirement {
	protected final String id;
	
	public BaseRequirement(String id) {
		this.id = id;
	}
	
	public String getId() {		
		return this.id;
	}
			
	@Override
	public boolean equals(Object obj) {		
		BaseRequirement other = (BaseRequirement) obj;
		return this.getClass() == obj.getClass() && this.id == other.getId();		
	}
	
	@Override
	public String toString() {
	    return this.getClass().getName() + ":" + this.id;
	}
}
