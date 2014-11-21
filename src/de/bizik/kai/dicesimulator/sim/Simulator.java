package de.bizik.kai.dicesimulator.sim;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;

public enum Simulator {
	INSTANCE;

	private ExecutorService executorService = null;
	private ReadOnlyBooleanWrapper isRunning = new ReadOnlyBooleanWrapper(false);
	private ReadOnlyIntegerWrapper simulationIterationsCounter = new ReadOnlyIntegerWrapper(0);

	
	public static Simulator getSimulator() {
		return INSTANCE;
	}
	
	public ReadOnlyBooleanProperty runningProperty() {
		return isRunning.getReadOnlyProperty();
	}

	public ReadOnlyIntegerProperty simulationIterationsCountProperty() {
		return simulationIterationsCounter.getReadOnlyProperty();
	}
	
	public void startSimulationAsync() {
		new Thread(() -> startSimulationSync()).start();
	}
	
	public void stopSimulationAsync() {
		new Thread(() -> stopSimulationSync()).start();
	}
	
	public synchronized void startSimulationSync() {
		if (!isRunning.get()) {
			executorService = Executors.newSingleThreadExecutor();
			executorService.execute(new SimulationTask(executorService, isRunning, simulationIterationsCounter));
		}
	}
	
	public synchronized void stopSimulationSync() {
		if (!isRunning.get())
			return;
		try {
			executorService.shutdown();
			executorService.awaitTermination(30, TimeUnit.SECONDS);
			executorService.shutdownNow();
		} catch (InterruptedException e) {
			executorService.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
	
}
