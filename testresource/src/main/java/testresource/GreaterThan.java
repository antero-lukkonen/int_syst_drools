package testresource;

public class GreaterThan<T> extends InputTerm {

	private final ValueRequirement<T> value;

	public GreaterThan(ValueRequirement<T> requirement) {
		super("GreaterThan");
		this.value = requirement;
	}

	public ValueRequirement<T> getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {		
		if (!super.equals(obj)) {
			return false;
		}
		GreaterThan<?> other = (GreaterThan<?>) obj;
		return this.getClass() == obj.getClass() && this.id == other.getId();		
	}
	
	@Override
	public String toString() {
	    return super.toString() + "==" + this.value;
	}
}
