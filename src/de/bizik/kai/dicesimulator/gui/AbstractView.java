package de.bizik.kai.dicesimulator.gui;

import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;


public abstract class AbstractView {

	// Idea from afterburner: https://github.com/AdamBien/afterburner.fx/
	// Basically a stripped down version without Injection capabilities
	
	private final static String VIEWSUFFIX = "view";
	private FXMLLoader loader;
	private Object presenter;
	private String basename;
	private String bundleName;
	private Parent parent;
	
	public AbstractView() {
	}

	private void initLoader() {
		if (loader == null) {

			String name = this.getClass().getSimpleName().toLowerCase();
			if (name.endsWith(VIEWSUFFIX))
				name = name.substring(0, name.indexOf(VIEWSUFFIX));
			basename = name;
			
			URL resource = getClass().getResource(name + ".fxml");
			bundleName = this.getClass().getPackage().getName() + "." + basename;
			ResourceBundle rb = null;
			try {
				ResourceBundle.getBundle(bundleName);
			} catch (MissingResourceException e) {
			};
			loader = new FXMLLoader(resource, rb);
			loader.setBuilderFactory(new JavaFXBuilderFactory());
			
			try {
				parent = loader.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			presenter = loader.getController();
		}
	}
	
	public Parent getView() {
		this.initLoader();
		URL u = getClass().getResource(basename + ".css");
		if (u != null && !parent.getStylesheets().contains(u.toExternalForm()))
			parent.getStylesheets().add(u.toExternalForm());
		return parent;
	}
	
	public Object getPresenter() {
		this.initLoader();
		return presenter;
	}
}
