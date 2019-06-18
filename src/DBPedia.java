import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

public class DBPedia {

	private static final String REPOSITORY_URL = "http://dbpedia.org/sparql";
	
	private static final String COUNTRY_SEARCH_QUERY = "SELECT DISTINCT ?country ?name WHERE {\n" + 
														"    ?country rdf:type schema:Country ;\n" + 
														"          rdfs:label ?name .\n" + 
														"    FILTER (UCASE(?name) LIKE UCASE(\"%%%s%%\"@en))\n" + 
														"    FILTER LANGMATCHES(LANG(?name), \"en\")\n" +
														"}";
	
	private static final String COUNTRY_CITIES_QUERY = "SELECT DISTINCT ?city ?name ?value WHERE {\n" + 
														"    ?city rdf:type dbo:City ;\n" + 
														"          dbo:country <%s> ;\n" + 
														"          rdfs:label ?name ;\n" + 
														"          <%s> ?value .\n" + 
														"    FILTER LANGMATCHES(LANG(?name), \"en\")\n" +
														"}" + 
														"ORDER BY DESC(?value)";
	
	private static final String CITIES_PARAMETERS_QUERY = "SELECT DISTINCT ?name ?parameter WHERE {\n" + 
			                                              "    ?city rdf:type dbo:PopulatedPlace ;\n" + 
			                                              "          dbo:country <%s> .\n" + 
			                                              "    ?city ?parameter ?obj .\n" + 
			                                              "    FILTER ISNUMERIC(?obj)\n" + 
			                                              "    ?parameter rdfs:label ?name .\n" +
			                                              "    FILTER LANGMATCHES(LANG(?name), \"en\")\n" +
			                                              "}";
	
	private Repository repository;
	private String sparqlPrefixes;

	public DBPedia() {
		setRepository(new SPARQLRepository(REPOSITORY_URL));
		setSparqlPrefixes();
	}

	public List<Resource> getCountries(String searchText) {
		String query = String.format(COUNTRY_SEARCH_QUERY, searchText);

		ArrayList<Resource> countries = new ArrayList<>();
		List<BindingSet> result;

		try {
			result = executeQuery(query);
			for (BindingSet bs : result) {
				Resource pais = new Resource(bs.getValue("name").stringValue(), (IRI)bs.getValue("country"));
				countries.add(pais);
			}
		} catch (Exception ex) {
			countries = null;
		}

		return countries;
	}

	public List<City> getCities(Resource country, Resource parameter) {
		String query = String.format(COUNTRY_CITIES_QUERY, country.getIri(), parameter.getIri());
		
		ArrayList<City> cities = new ArrayList<>();
		
		List<BindingSet> result;

		try {
			result = executeQuery(query);
			for (BindingSet bs : result) {
				City city = new City(bs.getValue("name").stringValue(), 
						bs.getValue("value").stringValue(), country, (IRI)bs.getValue("city"));
				cities.add(city);
			}
		} catch (Exception ex) {
			cities = null;
		}
		
		return cities;
	}
	
	public List<Resource> getParameters(Resource country) {
		String query = String.format(CITIES_PARAMETERS_QUERY, country.getIri());
		
		ArrayList<Resource> parameters = new ArrayList<>();
		
		List<BindingSet> result;

		try {
			result = executeQuery(query);
			for (BindingSet bs : result) {
				Resource parameter = new Resource(bs.getValue("name").stringValue(), (IRI)bs.getValue("parameter"));
				parameters.add(parameter);
			}
		} catch (Exception ex) {
			parameters = null;
		}
		
		return parameters;
	}
	
	private List<BindingSet> executeQuery(String query) throws Exception {
		try {
			RepositoryConnection con = getRepository().getConnection();
			TupleQuery tupleQuery = con.prepareTupleQuery(sparqlPrefixes + query);
			TupleQueryResult result = tupleQuery.evaluate();
			return QueryResults.asList(result);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			getRepository().shutDown();
		}
	}

	private void setSparqlPrefixes() {
		sparqlPrefixes = "";
		sparqlPrefixes += "PREFIX foaf: <" + FOAF.NAMESPACE + "> \n";
		sparqlPrefixes += "PREFIX rdf: <" + RDF.NAMESPACE + "> \n";
		sparqlPrefixes += "PREFIX rdfs: <" + RDFS.NAMESPACE + "> \n";
		sparqlPrefixes += "PREFIX dbo: <http://dbpedia.org/ontology/> \n";
		sparqlPrefixes += "PREFIX dbr: <http://dbpedia.org/resource/> \n";
		sparqlPrefixes += "PREFIX dct: <http://purl.org/dc/terms/> \n";
		sparqlPrefixes += "PREFIX dbc: <http://dbpedia.org/resource/Category:> \n";
	}

	private Repository getRepository() {
		if (!repository.isInitialized()) {
			repository.init();
		}
		return repository;
	}

	private void setRepository(Repository repository) {
		this.repository = repository;
	}

}
