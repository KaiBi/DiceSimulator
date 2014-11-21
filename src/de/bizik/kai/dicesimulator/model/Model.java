package de.bizik.kai.dicesimulator.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.bizik.kai.dicesimulator.model.statistics.Statistics;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = "de.bizik.kai.dicesim")
public final class Model implements ModelElement {

	private static final int[] INITIALLY_ALLOWED_SIDES = new int[] { 2, 4, 6, 8, 12, 20, 100 };
	private static final long AUTOSAVE_INTERVAL_IN_MILLIS = 300000;
	private static final String DATAFILENAME = "data.gz";

	@XmlTransient
	private static Consumer<String> logger = System.err::println;
	@XmlTransient
	private static JAXBContext jaxbcontext = null;

	@XmlTransient
	private final Statistics statistics;
	private final Map<Integer, DieKind> dieKindFromItsSidesIndex = new HashMap<Integer, DieKind>();

	private long timestampOfLastSaveInMillis = 0;

	private static final class InstanceHolder {
		static Model INSTANCE = null;
	}

	private Model() {
		statistics = new Statistics(this);
		for (int sides : INITIALLY_ALLOWED_SIDES)
			dieKindFromItsSidesIndex.put(sides, new DieKind(sides));
	}

	public static Model getModel() {
		if (InstanceHolder.INSTANCE == null) {
			InstanceHolder.INSTANCE = Model.load();
		}
		return InstanceHolder.INSTANCE;
	}
	
	@Override
	public void accept(ModelVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public Collection<? extends ModelElement> getChildren() {
		return getDieKinds();
	}
	
	public Statistics getStatistics() {
		return statistics;
	}

	public Collection<DieKind> getDieKinds() {
		return Collections.unmodifiableCollection(dieKindFromItsSidesIndex.values());
	}
	
	public DieKind getDieKind(int numberOfDieSides) {
		return dieKindFromItsSidesIndex.get(numberOfDieSides);
	}
	
	public int getNextDieKindsNumberOfSides(int currentDieKindsNumberOfSides) {
		List<Integer> validSides = new ArrayList<Integer>(dieKindFromItsSidesIndex.keySet());
		validSides.sort(Integer::compare);
		int index = validSides.indexOf(currentDieKindsNumberOfSides);
		if (index < validSides.size() - 1)
			index++;
		return dieKindFromItsSidesIndex.get(validSides.get(index)).getNumberOfDieSides();
	}
	
	public int getPreviousDieKindsNumberOfSides(int currentDieKindsNumberOfSides) {
		List<Integer> validSides = new ArrayList<Integer>(dieKindFromItsSidesIndex.keySet());
		validSides.sort(Integer::compare);
		int index = validSides.indexOf(currentDieKindsNumberOfSides);
		if (index > 0)
			index--;
		return dieKindFromItsSidesIndex.get(validSides.get(index)).getNumberOfDieSides();
	}

	public static void setLogger(Consumer<String> logger) {
		Model.logger = logger;
	}
	
	public void autosaveIfIntervalPassed() {
		if (System.currentTimeMillis() - timestampOfLastSaveInMillis > AUTOSAVE_INTERVAL_IN_MILLIS)
			try {
				saveToFile();
			} catch (IOException | JAXBException e) {
				logger.accept(e.toString());
			}
	}
	
	public void saveToFile() throws IOException, JAXBException {
		File f = new File(DATAFILENAME);
		f.createNewFile();
		GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(f));
		getJAXBContext().createMarshaller().marshal(this, out);
		out.close();
		timestampOfLastSaveInMillis = System.currentTimeMillis();
	}
	
	private static JAXBContext getJAXBContext() throws JAXBException {
		if (jaxbcontext == null)
			jaxbcontext = JAXBContext.newInstance(Model.class);
		return jaxbcontext;
	}
	
	private static Model load() {
		Model model = null;
		try {
			model = loadFromFilesystem();
		} catch (IOException | JAXBException e) {
			logger.accept("Could not load a saved model from the filesystem.\nWill try to load from the Jar-File.");
			try {
				model = loadFromJar();
			} catch (IOException | JAXBException | NullPointerException e1) {
				logger.accept("Could not load a saved model from the JAR-file.\n Will use a standard model.");
				model = new Model();
			}
		}
		return model;
	}

	private static Model loadFromFilesystem() throws IOException, JAXBException {
		File file = new File(DATAFILENAME);
		if (jaxbcontext == null)
			jaxbcontext = JAXBContext.newInstance(Model.class);
		GZIPInputStream in = new GZIPInputStream(new FileInputStream(file));
		Model model = (Model) jaxbcontext.createUnmarshaller().unmarshal(in);
		in.close();
		return model;
	}

	private static Model loadFromJar() throws IOException, JAXBException, NullPointerException {
		Model model = null;
		GZIPInputStream in = new GZIPInputStream(Model.class.getResourceAsStream(DATAFILENAME));
		model = (Model) getJAXBContext().createUnmarshaller().unmarshal(in);
		in.close();
		return model;
	}

}
