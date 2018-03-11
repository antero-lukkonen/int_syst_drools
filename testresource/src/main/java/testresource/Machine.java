package testresource;

import java.util.Arrays;
import java.util.List;

public class Machine {
	private final List<BaseRequirement> capabilities;
	private final int id;

	public Machine(int id, BaseRequirement... capability) {
		this.id = id;
		this.capabilities = Arrays.asList(capability);		
	}

	public List<BaseRequirement> getCapabilities() {
		return capabilities;
	}

	public int getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		Machine x = (Machine) obj;
		return this.getId() == x.getId();		
	}
	
	@Override
	public int hashCode() {
	    return this.getId();
	}
	
	@Override
	public String toString() {
	    return this.getClass().getName() + ":" + this.id + " Capabilities:" + this.capabilities;
	}
}
