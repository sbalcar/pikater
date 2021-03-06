package org.pikater.core.ontology.subtrees.newoption;

import jade.content.Concept;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.pikater.core.ontology.subtrees.newoption.base.IMergeable;
import org.pikater.core.ontology.subtrees.newoption.base.IWekaItem;
import org.pikater.core.ontology.subtrees.newoption.base.NewOption;
import org.pikater.shared.util.ICloneable;
import org.pikater.shared.util.collections.CollectionUtils;

import com.thoughtworks.xstream.XStream;

public class NewOptions implements Concept, ICloneable, IMergeable, IWekaItem,
		Iterable<NewOption> {
	private static final long serialVersionUID = -8578686409784032991L;

	private List<NewOption> options;

	public NewOptions() {
		this.options = new ArrayList<NewOption>();
	}

	public NewOptions(List<NewOption> list) {
		setOptions(list);
	}

	public List<NewOption> getOptions() {
		return options;
	}

	public void setOptions(List<NewOption> list) {
		this.options = list;
	}

	public boolean containsOptionWithName(String optionName) {
		return fetchOptionByName(optionName) != null;
	}

	public NewOption fetchOptionByName(String optionName) {
		for (NewOption option : options) {
			if (option.getName().equals(optionName)) {
				return option;
			}
		}
		return null;
	}

	public void addOption(NewOption option) {
		this.options.add(option);
	}

	public void addOptions(List<NewOption> options) {
		this.options.addAll(options);
	}

	public void removeOptionByName(String optionName) {
		NewOption option = fetchOptionByName(optionName);
		this.options.remove(option);
	}

	public String exportXML() {

		XStream xstream = new XStream();
		xstream.setMode(XStream.ID_REFERENCES);

		return xstream.toXML(this);
	}

	public static NewOptions importXML(String xml) {

		XStream xstream = new XStream();
		xstream.setMode(XStream.ID_REFERENCES);

		return (NewOptions) xstream.fromXML(xml);
	}

	@Override
	public NewOptions clone() {
		NewOptions result;
		try {
			result = (NewOptions) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		result.setOptions(CollectionUtils.deepCopy(options));
		return result;
	}

	@Override
	public void mergeWith(IMergeable other) {
		NewOptions otherOptionsWrapper = (NewOptions) other;
		for (NewOption optionInOtherI : otherOptionsWrapper.getOptions()) {
			NewOption optionLocal = fetchOptionByName(optionInOtherI.getName());
			if (optionLocal == null) {
				throw new IllegalArgumentException(
						"Could not merge option with name: "
								+ optionInOtherI.getName());
			} else {
				optionLocal.mergeWith(optionInOtherI);
			}
		}
	}

	@Override
	public Iterator<NewOption> iterator() {
		return options.iterator();
	}

	@Override
	public String exportToWeka() {
		return NewOptions.exportToWeka(options);
	}

	public static String exportToWeka(List<NewOption> options) {
		String wekaString = "";
		for (NewOption optionI : options) {
			wekaString += optionI.exportToWeka() + " ";
		}
		return wekaString;
	}
}