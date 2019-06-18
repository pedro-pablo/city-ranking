import org.eclipse.rdf4j.model.IRI;

public class Resource {

	private String name;
	private IRI iri;
	
	public Resource(String name, IRI iri) {
		setName(name);
		setIri(iri);
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public IRI getIri() {
		return iri;
	}

	private void setIri(IRI iri) {
		this.iri = iri;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
