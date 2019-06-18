import org.eclipse.rdf4j.model.IRI;

public class City extends Resource {

	private double value;
	private Resource country;

	public City(String name, String value, Resource country, IRI iri) {
		super(name, iri);
		setValue(value);
		setCountry(country);
	}

	public double getValue() {
		return value;
	}

	private void setValue(String value) {
		this.value = Double.parseDouble(value);
	}

	public Resource getPais() {
		return country;
	}

	private void setCountry(Resource country) {
		this.country = country;
	}
	
	@Override
	public String toString() {
		return super.toString() + " - " + getValue();
	}

}
