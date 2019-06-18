import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

	private static final String MAIN_SCENE_TITLE = "City Ranking";
	private static final int MAIN_SCENE_WIDTH = 600;
	private static final int MAIN_SCENE_HEIGHT = 600;

	private DBPedia dbPedia;

	// Window components
	private Stage mainScene;
	private Alert messageDialog;
	private TextField txtSearchCountries;
	private ComboBox<Resource> cbCountries;
	private ComboBox<Resource> cbParameters;
	private ListView<City> listRanking;

	public Main() {
		dbPedia = new DBPedia();
	}
	
	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage primaryStage) {
		mainScene = primaryStage;
		configureMainScene();
		mainScene.show();
	}

	private void configureMainScene() {
		mainScene.setTitle(MAIN_SCENE_TITLE);
		mainScene.setWidth(MAIN_SCENE_WIDTH);
		mainScene.setHeight(MAIN_SCENE_HEIGHT);
		mainScene.setResizable(false);

		// Country search
		txtSearchCountries = new TextField();
		txtSearchCountries.setMinWidth(MAIN_SCENE_WIDTH * 0.65);

		Button btnSearchCountries = new Button("Search countries");
		btnSearchCountries.setMinWidth(MAIN_SCENE_WIDTH * 0.25);
		btnSearchCountries.setOnAction(e -> searchCountries());

		// Country selection
		Label labelCbCountries = new Label("Country:");
		labelCbCountries.setMinWidth(MAIN_SCENE_WIDTH * 0.10);

		cbCountries = new ComboBox<Resource>();
		cbCountries.setMinWidth(MAIN_SCENE_WIDTH * 0.55);
		cbCountries.setMaxWidth(MAIN_SCENE_WIDTH * 0.55);
		cbCountries.setOnAction(e -> clearParameters());

		Button btnGetParameters = new Button("Get parameters");
		btnGetParameters.setMinWidth(MAIN_SCENE_WIDTH * 0.25);
		btnGetParameters.setOnAction(e -> getComparisonParameters());

		// Comparison parameter definition
		Label labelCbParameters = new Label("Comparison parameter:");
		labelCbParameters.setMinWidth(MAIN_SCENE_WIDTH * 0.25);

		cbParameters = new ComboBox<Resource>();
		cbParameters.setMinWidth(MAIN_SCENE_WIDTH * 0.65);
		cbParameters.setMaxWidth(MAIN_SCENE_WIDTH * 0.65);

		// Ranking
		Button btnGetRanking = new Button("Get ranking");
		btnGetRanking.setMinWidth(MAIN_SCENE_WIDTH * 0.92);
		btnGetRanking.setOnAction(e -> fillRanking());

		listRanking = new ListView<City>();
		double listHeight = MAIN_SCENE_HEIGHT * 0.60, listWidth = MAIN_SCENE_WIDTH - 45;
		listRanking.setMaxHeight(listHeight);
		listRanking.setMaxWidth(listWidth);
		listRanking.setEditable(false);

		// Containers
		HBox searchContainer = new HBox(txtSearchCountries, btnSearchCountries);
		searchContainer.setMinHeight(MAIN_SCENE_HEIGHT * 0.08);
		searchContainer.setAlignment(Pos.CENTER);
		searchContainer.setSpacing(5);

		HBox countriesContainer = new HBox(labelCbCountries, cbCountries, btnGetParameters);
		countriesContainer.setMinHeight(MAIN_SCENE_HEIGHT * 0.08);
		countriesContainer.setAlignment(Pos.CENTER);
		countriesContainer.setSpacing(5);

		HBox parametersContainer = new HBox(labelCbParameters, cbParameters);
		parametersContainer.setMinHeight(MAIN_SCENE_HEIGHT * 0.08);
		parametersContainer.setAlignment(Pos.CENTER);
		parametersContainer.setSpacing(10);

		VBox rankingContainer = new VBox(btnGetRanking, listRanking);
		rankingContainer.setMinHeight(MAIN_SCENE_HEIGHT * 0.76);
		rankingContainer.setAlignment(Pos.TOP_CENTER);
		rankingContainer.setSpacing(5);

		VBox mainContainer = new VBox(searchContainer, countriesContainer, 
				parametersContainer, rankingContainer);
		mainContainer.setAlignment(Pos.TOP_CENTER);
		mainContainer.setMinWidth(MAIN_SCENE_WIDTH);
		mainContainer.setMaxWidth(MAIN_SCENE_WIDTH);
		mainContainer.setMinHeight(MAIN_SCENE_HEIGHT);
		mainContainer.setMaxHeight(MAIN_SCENE_HEIGHT);
		mainContainer.setPadding(new Insets(10, 10, 10, 10));

		mainScene.setScene(new Scene(mainContainer));
	}

	private void searchCountries() {
		String searchText = txtSearchCountries.getText();
		if (searchText.isBlank()) {
			return;
		}

		List<Resource> currentCountries = cbCountries.getItems();
		currentCountries.clear();
		clearParameters();
		
		List<Resource> foundCountries = dbPedia.getCountries(searchText);
		if (foundCountries == null) {
			showError("There was an error obtaining the list of countries.");
			return;
		}

		for (Resource country : foundCountries) {
			currentCountries.add(country);
		}
	}

	private void getComparisonParameters() {
		Resource selectedCountry = cbCountries.getSelectionModel().getSelectedItem();
		if (!verifyCountry(selectedCountry)) {
			return;
		}

		List<Resource> currentParameters = cbParameters.getItems();
		currentParameters.clear();
		clearRanking();
		
		List<Resource> foundParameters = dbPedia.getParameters(selectedCountry);
		if (foundParameters == null) {
			showError("There was an error obtaining the list of parameters.");
			return;
		}
		
		for (Resource parameter : foundParameters) {
			currentParameters.add(parameter);
		}
	}

	private void fillRanking() {
		Resource selectedCountry = cbCountries.getSelectionModel().getSelectedItem();
		Resource selectedParameter = cbParameters.getSelectionModel().getSelectedItem();

		if (!verifyCountry(selectedCountry)) {
			return;
		}

		if (selectedParameter == null) {
			showInfo("You must select a comparison parameter to rank the cities.");
			return;
		}

		List<City> rankingCities = dbPedia.getCities(selectedCountry, selectedParameter);

		List<City> currentCities = listRanking.getItems();
		currentCities.clear();

		for (City city : rankingCities) {
			currentCities.add(city);
		}
	}

	private boolean verifyCountry(Resource country) {
		if (country == null) {
			showInfo("You must select a country.");
			return false;
		} else {
			return true;
		}
	}

	private void clearParameters() {
		List<Resource> currentParameters = cbParameters.getItems();
		currentParameters.clear();
		clearRanking();
	}
	
	private void clearRanking() {
		List<City> currentCities = listRanking.getItems();
		currentCities.clear();
	}

	private void showError(String message) {
		showDialog(message, AlertType.ERROR);
	}

	private void showInfo(String message) {
		showDialog(message, AlertType.INFORMATION);
	}

	private void showDialog(String message, AlertType type) {
		if (messageDialog == null || !messageDialog.getAlertType().equals(type)) {
			messageDialog = new Alert(type);
		}

		messageDialog.setHeaderText(null);
		messageDialog.setContentText(message);
		messageDialog.showAndWait();
	}

}
