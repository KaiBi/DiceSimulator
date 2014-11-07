package de.bizik.kai.dicesimulator.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "de.bizik.kai.dicesim")
public class Model {
	
	private static final int[] INITIALDIEKINDS = new int[] {2, 4, 6, 8, 12, 20, 100};
	private static final long AUTOSAVEINTERVAL = 300000;
	private static final String DATAFILENAME = "data.gz";
	
	@XmlElement(name="NSidedDice")
	private final Map<Integer, NSidedDie> dice = new HashMap<Integer, NSidedDie>();
	
	// lazy thread-safe singleton
	private static final class InstanceHolder {
		static final Model INSTANCE = Model.load();
	}
	
	public static Model getModel() {
		return InstanceHolder.INSTANCE;
	}
	
	private Model() {
		for (int i : INITIALDIEKINDS)
			dice.put(i, new NSidedDie(i));
	}
	
	private static JAXBContext jaxbcontext = null;
	
	private static Model load() {
		Model m = loadFromFile();
		if (m == null) {
			System.err.println("Could not load from file " + DATAFILENAME + ". Will try to use the shipped set.");
			m = loadFromProvided();
		}
		if (m == null) {
			System.err.println("Could not load the shipped set of data. Will use an empty standard model.");
			m = new Model();
		}
		m.updateStatistics();
		return m;
	}
	
	private static Model loadFromFile() {
		File f = new File("data.gz");
		Model m = null;
		if (f.exists() && f.canRead()) {
			try {
				if (jaxbcontext == null)
					jaxbcontext = JAXBContext.newInstance(Model.class);
				GZIPInputStream in = new GZIPInputStream(new FileInputStream(f));
				m = (Model) jaxbcontext.createUnmarshaller().unmarshal(in);
				in.close();
			} catch (IOException | JAXBException e) {
				e.printStackTrace();
				m = null;
			}
		}
		return m;
	}
	
	private static Model loadFromProvided() {
		Model m = null;
		try {
			if (jaxbcontext == null)
				jaxbcontext = JAXBContext.newInstance(Model.class);
			GZIPInputStream in = new GZIPInputStream(Model.class.getResourceAsStream(DATAFILENAME));
			m = (Model) jaxbcontext.createUnmarshaller().unmarshal(in);
			in.close();
		} catch (IOException | JAXBException e) {
			e.printStackTrace();
			m = null;
		} catch (NullPointerException e1) {
			m = null;
		}
		return m;
	}
	
	public void save() {
		File f = new File("data.gz");
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!f.exists() || !f.canWrite()) {
			System.err.println("File not writable. Aborting save operation.");
			return;
		}
		try {
			if (jaxbcontext == null)
				jaxbcontext = JAXBContext.newInstance(Model.class);
			GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(f));
			jaxbcontext.createMarshaller().marshal(this, out);
			out.close();
			timeOfLastSave = System.currentTimeMillis();
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private long timeOfLastSave = 0;
	public void autosaveIfNecessary() {
		if (System.currentTimeMillis() - timeOfLastSave > AUTOSAVEINTERVAL)
			save();
	}
	
	public NSidedDie getNSidedDie(int numSides) {
		if (!dice.containsKey(numSides) && numSides > 1) {
			dice.put(numSides, new NSidedDie(numSides));
			updateAllowedDieKinds();
		}
		return dice.get(numSides);
	}

	private ObjectProperty<int[]> allowedDieKindsProperty = new SimpleObjectProperty<int[]>();
	public ReadOnlyObjectProperty<int[]> allowedDieKindsProperty() {
		return allowedDieKindsProperty;
	}
	private void updateAllowedDieKinds() {
		Collection<Integer> is = dice.keySet();
		int[] dk = new int[is.size()];
		int index = 0;
		for (Integer i : is) {
			dk[index++] = i;
		}
		Arrays.sort(dk);
		allowedDieKindsProperty.set(dk);
	}
	
	public void updateStatistics() {
		for (NSidedDie d : dice.values()) {
			d.updateStatistics();
		}
		updateOwnStatistics();
	}
	
	private void updateOwnStatistics() {
		updateAllowedDieKinds();
	}
}
