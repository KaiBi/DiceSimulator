package de.bizik.kai.dicesimulator.gui.main;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import de.bizik.kai.dicesimulator.gui.main.stats.StatsView;

public class MainPresenter implements Initializable {

	@FXML
	private StackPane mainStack;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Font.loadFont(this.getClass().getResource("FiraSans-Bold.ttf").toExternalForm(), 16.0d);
		Font.loadFont(this.getClass().getResource("FiraSans-BoldItalic.ttf").toExternalForm(), 16.0d);
		Font.loadFont(this.getClass().getResource("FiraSans-Italic.ttf").toExternalForm(), 16.0d);
		Font.loadFont(this.getClass().getResource("FiraSans-Regular.ttf").toExternalForm(), 16.0d);
		Font.loadFont(this.getClass().getResource("FiraMono-Bold.ttf").toExternalForm(), 16.0d);
		Font.loadFont(this.getClass().getResource("FiraMono-Regular.ttf").toExternalForm(), 16.0d);
		mainStack.getChildren().add(new StatsView().getView());
	}
	
}
