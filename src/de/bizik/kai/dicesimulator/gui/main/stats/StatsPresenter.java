package de.bizik.kai.dicesimulator.gui.main.stats;

import java.math.BigInteger;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import de.bizik.kai.dicesimulator.model.Model;
import de.bizik.kai.dicesimulator.model.NSidedDie;
import de.bizik.kai.dicesimulator.sim.Simulator;

public class StatsPresenter implements Initializable {

	@FXML
	private Label dieKindLabel;
	@FXML
	private Label successesStatsLabel;
	@FXML
	private Button simStartingButton;
	@FXML
	private Label dieCountStatsLabel;
	@FXML
	private Button simRunningButton;
	@FXML
	private Label difficultyStatsLabel;
	@FXML
	private Label difficultyLabel;
	@FXML
	private Label dieCountLabel;
	@FXML
	private Button simStoppingButton;
	@FXML
	private Label successesLabel;
	@FXML
	private Label dieKindStatsLabel;
	@FXML
	private Button simStoppedButton;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initSimulatorView();
		initDieKindView();
		initDieCountView();
		initDifficultyView();
		initSuccessesView();
	}
	
	
	private final Simulator simulator = Simulator.INSTANCE;
	private static final String SELECTEDBUTTONCSSCLASS = "simulatorButtonSelected";
	
	private void initSimulatorView() {
		simulator.runningProperty().addListener((p, oldVal, newVal) -> {
			Platform.runLater(() -> {
				if (oldVal && !newVal) {
					simStoppingButton.getStyleClass().remove(SELECTEDBUTTONCSSCLASS);
					simStoppedButton.getStyleClass().add(SELECTEDBUTTONCSSCLASS);
				} else if (!oldVal && newVal) {
					simStartingButton.getStyleClass().remove(SELECTEDBUTTONCSSCLASS);
					simRunningButton.getStyleClass().add(SELECTEDBUTTONCSSCLASS);
				}
			});
		});
	}
	
	private final IntegerProperty dieKindProperty = new SimpleIntegerProperty(Model.getModel().allowedDieKindsProperty().get()[0]);

	private void initDieKindView() {
		dieKindLabel.textProperty().bind(dieKindProperty.asString());
		dieKindLabel.addEventFilter(MouseEvent.MOUSE_PRESSED, (event) -> {
			if (event.isSecondaryButtonDown()) {
				previousDieKind();
			} else if (event.isPrimaryButtonDown()) {
				nextDieKind();				
			}
		});
		dieKindProperty.addListener((p, o, n) -> {
			updateDieKindStatsBinding();
		});
		dieKindStatsProperty.addListener((p, o, n) -> {
			Platform.runLater(() -> {
				dieKindStatsLabel.textProperty().set(n);
			});
		});
		updateDieKindStatsBinding();
		dieKindProperty.set(6);
	}
	
	private StringProperty dieKindStatsProperty = new SimpleStringProperty();
	private void updateDieKindStatsBinding() {
		dieKindStatsProperty.unbind();
		ReadOnlyDoubleProperty avg = 
				Model.getModel()
				.getNSidedDie(dieKindProperty.get())
				.getLevel(1)
				.averageProperty();
		ReadOnlyObjectProperty<BigInteger> samples = 
				Model.getModel()
				.getNSidedDie(dieKindProperty.get())
				.getLevel(1)
				.numRollsProperty();
		StringExpression se = Bindings.concat(
				"Durchschnittliche Augenzahl: ",
				avg,
				"\n\nBasierend auf ",
				samples,
				" Würfen mit 1w",
				dieKindProperty,
				".");
		dieKindStatsProperty.bind(se);
	}
	
	private IntegerProperty dieCountProperty = new SimpleIntegerProperty(2);
	
	private void initDieCountView() {
		dieCountLabel.textProperty().bind(dieCountProperty.asString());
		dieCountLabel.addEventFilter(MouseEvent.MOUSE_PRESSED, (event) -> {
			if (event.isSecondaryButtonDown()) {
				previousDieCount();
			} else if (event.isPrimaryButtonDown()) {
				nextDieCount();
			}
		});
		dieCountProperty.addListener((p, oldVal, newVal) -> {
			updateDieCountStatsBinding();
		});
		dieKindProperty.addListener((p, oldVal, newVal) -> {
			updateDieCountStatsBinding();
		});
		dieCountStatsProperty.addListener((p, o, n) -> {
			Platform.runLater(() -> {
				dieCountStatsLabel.textProperty().set(n);
			});
		});
		updateDieCountStatsBinding();
		dieCountStatsLabel.textProperty().set(dieCountStatsProperty.get());
		dieCountProperty.set(2);
	}

	private StringProperty dieCountStatsProperty = new SimpleStringProperty();
	private void updateDieCountStatsBinding() {
		dieCountStatsProperty.unbind();
		ReadOnlyDoubleProperty avg = 
				Model.getModel()
				.getNSidedDie(dieKindProperty.get())
				.getLevel(dieCountProperty.get())
				.averageProperty();
		ReadOnlyObjectProperty<BigInteger> samples = 
				Model.getModel()
				.getNSidedDie(dieKindProperty.get())
				.getLevel(dieCountProperty.get())
				.numRollsProperty();
		StringExpression se = Bindings.concat(
				"Durchschnittliche Augenzahl (Summe): ",
				avg,
				"\n\nBasierend auf ",
				samples,
				" Würfen mit ",
				dieCountProperty,
				"w",
				dieKindProperty,
				".");
		dieCountStatsProperty.bind(se);
	}
	
	private IntegerProperty difficultyProperty = new SimpleIntegerProperty(3);
	private void initDifficultyView() {
		difficultyLabel.textProperty().bind(difficultyProperty.asString());
		difficultyLabel.addEventFilter(MouseEvent.MOUSE_PRESSED, (event) -> {
			if (event.isSecondaryButtonDown())
				previousDifficulty();
			else if (event.isPrimaryButtonDown())
				nextDifficulty();
		});
		dieCountProperty.addListener((p, oldVal, newVal) -> {
			updateDifficultyStatsBinding();
		});
		dieKindProperty.addListener((p, oldVal, newVal) -> {
			updateDifficultyStatsBinding();
		});
		difficultyProperty.addListener((p, oldVal, newVal) -> {
			updateDifficultyStatsBinding();
		});
		difficultyStatsProperty.addListener((p, o, n) -> {
			Platform.runLater(() -> {
				difficultyStatsLabel.textProperty().set(n);
			});
		});
		updateDifficultyStatsBinding();
		difficultyStatsLabel.textProperty().set(difficultyStatsProperty.get());
		difficultyProperty.set(3);
	}
	
	private StringProperty difficultyStatsProperty = new SimpleStringProperty();
	private void updateDifficultyStatsBinding() {
		difficultyStatsProperty.unbind();
		ReadOnlyDoubleProperty successProbability = Model.getModel()
				.getNSidedDie(dieKindProperty.get())
				.getLevel(dieCountProperty.get())
				.getDifficulty(difficultyProperty.get())
				.successProbabilityProperty(1);
		ReadOnlyObjectProperty<BigInteger> samples = Model.getModel()
				.getNSidedDie(dieKindProperty.get())
				.getLevel(dieCountProperty.get())
				.numRollsProperty();
		ReadOnlyDoubleProperty averageSuccesses = Model.getModel()
				.getNSidedDie(dieKindProperty.get())
				.getLevel(dieCountProperty.get())
				.getDifficulty(difficultyProperty.get())
				.averageNumberOfSuccessesProperty();
		StringExpression se = Bindings.concat(
				"Wahrscheinlichkeit einfacher Erfolg: ",
				successProbability,
				"\n\nDurchschnittliche Anzahl Erfolge: ",
				averageSuccesses,
				"\n\nBasierend auf ",
				samples,
				" Würfen ",
				dieCountProperty,
				"w",
				dieKindProperty,
				"v",
				difficultyProperty,
				".");
		difficultyStatsProperty.bind(se);
	}
	
	private IntegerProperty successesProperty = new SimpleIntegerProperty(1);
	private void initSuccessesView() {
		successesLabel.textProperty().bind(successesProperty.asString());
		successesLabel.addEventFilter(MouseEvent.MOUSE_PRESSED, (event) -> {
			if (event.isSecondaryButtonDown())
				previousSuccesses();
			else if (event.isPrimaryButtonDown())
				nextSuccesses();
		});
		dieCountProperty.addListener((p, oldVal, newVal) -> {
			updateSuccessesStatsBinding();
		});
		dieKindProperty.addListener((p, oldVal, newVal) -> {
			updateSuccessesStatsBinding();
		});
		difficultyProperty.addListener((p, oldVal, newVal) -> {
			updateSuccessesStatsBinding();
		});
		successesProperty.addListener((p, oldVal, newVal) -> {
			updateSuccessesStatsBinding();
		});
		successesStatsProperty.addListener((p, o, n) -> {
			Platform.runLater(() -> {
				successesStatsLabel.textProperty().set(n);
			});
		});
		updateSuccessesStatsBinding();
		successesStatsLabel.textProperty().set(difficultyStatsProperty.get());
		successesProperty.set(1);
	}
	
	private StringProperty successesStatsProperty = new SimpleStringProperty();
	private void updateSuccessesStatsBinding() {
		successesStatsProperty.unbind();
		ReadOnlyDoubleProperty successProbability = Model.getModel()
				.getNSidedDie(dieKindProperty.get())
				.getLevel(dieCountProperty.get())
				.getDifficulty(difficultyProperty.get())
				.successProbabilityProperty(successesProperty.get());
		ReadOnlyObjectProperty<BigInteger> samples = Model.getModel()
				.getNSidedDie(dieKindProperty.get())
				.getLevel(dieCountProperty.get())
				.numRollsProperty();
		ReadOnlyDoubleProperty exactSuccessesProbability = Model.getModel()
				.getNSidedDie(dieKindProperty.get())
				.getLevel(dieCountProperty.get())
				.getDifficulty(difficultyProperty.get())
				.exactSuccessProbability(successesProperty.get());
		StringExpression se = Bindings.concat(
				"Wahrscheinlichkeit komplexer Erfolg: ",
				successProbability,
				"\n\nWahrscheinlichkeit genauer Erfolg: ",
				exactSuccessesProbability,
				"\n\nBasierend auf ",
				samples,
				" Würfen ",
				dieCountProperty,
				"w",
				dieKindProperty,
				"v",
				difficultyProperty,
				".");
		successesStatsProperty.bind(se);
	}
	
	@FXML
	private void toggleSimulation() {
		ReadOnlyBooleanProperty simRunning = simulator.runningProperty();
		if (simRunning.get() && simRunningButton.getStyleClass().contains(SELECTEDBUTTONCSSCLASS)) {
			simRunningButton.getStyleClass().remove(SELECTEDBUTTONCSSCLASS);
			simStoppingButton.getStyleClass().add(SELECTEDBUTTONCSSCLASS);
			simulator.stopSimulation();
		} else if (!simRunning.get() && simStoppedButton.getStyleClass().contains(SELECTEDBUTTONCSSCLASS)) {
			simStoppedButton.getStyleClass().remove(SELECTEDBUTTONCSSCLASS);
			simStartingButton.getStyleClass().add(SELECTEDBUTTONCSSCLASS);
			simulator.startSimulation();
		}
	}

    private void nextDieKind() {
    	int[] allowedDieKinds = Model.getModel().allowedDieKindsProperty().get();
    	int newKind = allowedDieKinds[0];
    	int oldKind = dieKindProperty.get();
    	for (int i = 0; i < allowedDieKinds.length; i++)
    		if (allowedDieKinds[i] == oldKind && i < allowedDieKinds.length - 1) {
    			newKind = allowedDieKinds[i + 1];
    			break;
    		}
    	dieKindProperty.set(newKind);
    }

    private void previousDieKind() {
    	int[] allowedDieKinds = Model.getModel().allowedDieKindsProperty().get();
    	int newKind = allowedDieKinds[allowedDieKinds.length - 1];
    	int oldKind = dieKindProperty.get();
    	for (int i = 0; i < allowedDieKinds.length; i++)
    		if (allowedDieKinds[i] == oldKind && i > 0) {
    			newKind = allowedDieKinds[i - 1];
    			break;
    		}
    	dieKindProperty.set(newKind);
    }

    private void previousDieCount() {
    	int val = dieCountProperty.get();
    	if (val > 1)
    		dieCountProperty.set(val - 1);
    }

    private void nextDieCount() {
    	int val = dieCountProperty.get();
    	if (val < NSidedDie.MAX_LEVEL)
    		dieCountProperty.set(val + 1);
    }

    private void previousDifficulty() {
    	int diff = difficultyProperty.get();
    	if (diff > 1)
    		difficultyProperty.set(diff - 1);
    }

    private void nextDifficulty() {
    	difficultyProperty.set(difficultyProperty.get() + 1);
    }

    private void previousSuccesses() {
    	int succ = successesProperty.get();
    	if (succ > 1)
    		successesProperty.set(succ - 1);
    }

    private void nextSuccesses() {
    	int succ = successesProperty.get();
    	if (succ < dieCountProperty.get())
    		successesProperty.set(succ + 1);
    }
}
