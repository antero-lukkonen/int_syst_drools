package testresource;

public class ValueRequirement<T> extends BaseRequirement {

	private final T value;

	public ValueRequirement(String id, T value) {
		super(id);
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	@Override
	public String toString() {
	    return super.toString() + "==" + this.value;
	}
}
