package de.bizik.kai.dicesimulator.sim;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public enum Simulator {
	INSTANCE;

	public static Simulator getSimulator() {
		return INSTANCE;
	}
	
	private BooleanProperty isRunning = new SimpleBooleanProperty(false);
	
	public ReadOnlyBooleanProperty runningProperty() {
		return isRunning;
	}
	
	public ExecutorService execService = null;
	
	public void startSimulation() {
		new Thread(() -> startSimulationSync()).start();
	}
	
	public void stopSimulation() {
		new Thread(() -> stopSimulationSync()).start();
	}
	
	public synchronized void startSimulationSync() {
		if (!isRunning.get()) {
			execService = Executors.newSingleThreadExecutor();
			execService.execute(new SimulationTask(execService, isRunning));
		}
	}
	
	public synchronized void stopSimulationSync() {
		if (!isRunning.get())
			return;
		try {
			// we might lose some results, but nothing serious
			execService.shutdown();
			execService.awaitTermination(30, TimeUnit.SECONDS);
			execService.shutdownNow();
		} catch (InterruptedException e) {
			execService.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
	
}
