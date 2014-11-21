package de.bizik.kai.dicesimulator.gui.main;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import de.bizik.kai.dicesimulator.gui.main.loadscreen.LoadScreenView;
import de.bizik.kai.dicesimulator.gui.main.stats.StatsView;
import de.bizik.kai.dicesimulator.model.Model;


public class MainPresenter implements Initializable {

	private static final double DEFAULT_FONT_SIZE = 16.0d;
	
	@FXML
	private StackPane mainStack;
	private Parent loadScreenView;
	private Parent statsView;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadFonts(DEFAULT_FONT_SIZE);
		showLoadingView();
		transitionToStatsOnModelAvailable();
	}

	private void loadFonts(double defaultFontSize) {
		Font.loadFont(this.getClass().getResource("FiraSans-Bold.ttf").toExternalForm(), defaultFontSize);
		Font.loadFont(this.getClass().getResource("FiraSans-BoldItalic.ttf").toExternalForm(), defaultFontSize);
		Font.loadFont(this.getClass().getResource("FiraSans-Italic.ttf").toExternalForm(), defaultFontSize);
		Font.loadFont(this.getClass().getResource("FiraSans-Regular.ttf").toExternalForm(), defaultFontSize);
		Font.loadFont(this.getClass().getResource("FiraMono-Bold.ttf").toExternalForm(), defaultFontSize);
		Font.loadFont(this.getClass().getResource("FiraMono-Regular.ttf").toExternalForm(), defaultFontSize);
	}
	
	private void showLoadingView() {
		loadScreenView = new LoadScreenView().getView();
		mainStack.getChildren().add(loadScreenView);
	}
	
	private void transitionToStatsOnModelAvailable() {
		new Thread(() -> {
			Model.getModel();
			Platform.runLater(() -> {
				statsView = new StatsView().getView();
				mainStack.getChildren().add(0, statsView);
				TranslateTransition slideOutToRight = new TranslateTransition(Duration.seconds(1.0d), loadScreenView);
				slideOutToRight.byXProperty().set(mainStack.getWidth());
				slideOutToRight.setOnFinished((event) -> {
					mainStack.getChildren().remove(loadScreenView);
				});
				slideOutToRight.play();				
			});
		}).start();
	}

}
