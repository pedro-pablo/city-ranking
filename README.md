# city-ranking
Final assignment for the Data Structures and Object Oriented Programming course.

This is a desktop application made with JavaFX that uses [rdf4j](https://rdf4j.eclipse.org/) to access [DBpedia](https://wiki.dbpedia.org/) and obtain ordered data on cities of a determined country.

## What it does
This app shows the user a ranking of cities that are ranked based on a certain numeric parameter (total population, altitude, etc). The user searches for a country and then selects one from the list. Next, the user must select a parameter for comparison. Finally, when the user clicks "Get ranking", an ordered list of all cities for the selected country is shown. The countries, cities and available numeric parameters are all dinamically obtained from DBpedia.

## How it does
Using the aforementioned rdf4j framework, the application access DBpedia and executes [SPARQL](https://www.w3.org/TR/rdf-sparql-query/) queries to obtain data extracted from Wikipedia articles. The obtained data is displayed to the user, which must select a country and a parameter to compare all the cities from that country. After that a last SPARQL query is used to obtain and order (based on the selected comparison parameter) the data from the cities of the selected country. The ordered cities are exhibited in a list.

## Known issues
* Since all the data is obtained from DBpedia, which extracts them from Wikipedia article infoboxes, it may be outdated, incomplete or even wrong.
* Some countries (e.g: United States) have too many cities with too much data, which causes the SPARQL query to timeout on execution.
