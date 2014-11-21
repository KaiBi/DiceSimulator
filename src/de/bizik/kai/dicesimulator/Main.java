package de.bizik.kai.dicesimulator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import de.bizik.kai.dicesimulator.gui.main.MainView;
import de.bizik.kai.dicesimulator.model.Model;
import de.bizik.kai.dicesimulator.sim.Simulator;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage firstStage) throws Exception {
		firstStage.setScene(new Scene(new MainView().getView(), 480, 800));
		firstStage.setY(0.0d);
		firstStage.show();
	}

	@Override
	public void stop() throws Exception {
		Simulator.INSTANCE.stopSimulationSync();
		Model.getModel().saveToFile();
	}
	
}
