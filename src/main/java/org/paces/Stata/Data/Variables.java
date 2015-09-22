package org.paces.Stata.Data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.stata.sfi.Data;
import com.stata.sfi.ValueLabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Billy Buchanan
 * @version 0.0.0
 * <h2>Stata Variable MetaData Object</h2>
 * <p>Class used for Stata's Java API to access the metadata for Stata
 * variables.  This class is initialized by the Meta class object.</p>
 */
public class Variables {

	/***
	 * Member variable containing variable indices
 	 */
	@JsonProperty(required = true, value = "Variable Indices")
	public List<Integer> varindex;

	/***
	 * Member variable containing Stata variable names
	 */
	@JsonProperty(required = true, value = "Variable Names")
	public List<String> varnames;

	/***
	 * Member variable containing Stata variable labels
	 */
	@JsonProperty(required = true, value = "Variable Labels")
	public List<String> varlabels;

	/***
	 * Member variable containing Stata value label names associated with a
	 * given variable
	 */
	@JsonProperty(required = true, value = "Value Label Names")
	public List<String> valueLabelNames;

	/***
	 * Member variable containing a list of Map objects with the values and
	 * associated labels contained in the Map object
	 */
	@JsonProperty(required = true, value = "Value Labels")
	public List<Object> valueLabels;

	/***
	 * Member variable containing indicators for whether or not the variable
	 * is of type String
	 */
	@JsonProperty(required = true, value = "Is String Variable Indicators")
	public List<Boolean> varTypes;

	/***
	 * Number of variables passed from javacall
	 */
	@JsonProperty(required = true, value = "Number of Variables")
	public int nvars;

	/***
	 * Generic constructor when no varlist is passed
	 */
	@JsonCreator
	Variables() {

		// Set the variable index member variable
		setVariableIndex();

		// Set the number of variables member variable
		setNvars(this.varindex);

		// Set the variable name member variable
		setVariableNames();

		// Set the variable label member variable
		setVariableLabels();

		// Set the value label name member variable
		setValueLabelNames();

		// Set the value label value/label pair member variable
		setValueLabels();

		// Set the variable is string index member variable
		setVariableTypeIndex();

	} // End constructor method

	/***
	 * Populates the variable index member variable with the indices used to
	 * identify variables in the Stata dataset in memory.
	 */
	@JsonSetter
	public void setVariableIndex() {

		// Initialize an empty array list of Integer objects
		List<Integer> vars = new ArrayList<>();

		// Get the number of parsed variables
		int parsed = Data.getParsedVarCount();

		// Get the total number of variables in the dataset
		int allvars = Data.getVarCount();

		// If there are variables parsed from the variable list
		if (parsed != allvars) {

			// Loop over the variables
			for (int i = 1; i <= parsed; i++) {

				// Get the index for the individual variables passed as a
				// varlist
				vars.add(Data.mapParsedVarIndex(i));

			} // End Loop over varlist variables

			// Set the variable index member variable's values
			this.varindex = vars;

		} else {

			// Loop over the total indices of variables
			for (int i = 0; i < allvars; i++) {

				// Add the index value to the list object
				vars.add(i + 1);

			} // End Loop over values

			// Set the variable index member variable's values
			this.varindex = vars;

		} // End IF/ELSE Block for variable list handling

	} // End setter method for varindex variable

	/***
	 * Method to set the number of variables passed to javacall
	 * @param varidx A variable index
	 */
	@JsonSetter
	public void setNvars(List<Integer> varidx) {

		// Set nvars based on the size of the List of integer objects
		this.nvars = varidx.size();

	} // End setter method for nvars

	/***
	 * Method to access the number of variables passed from javacall
	 * @return An integer value with the number of variables passed to javacall
	 */
	@JsonGetter
	public int getNvars() {

		// Returns the nvars member variable
		return this.nvars;

	} // End of getter method for nvars member variable

	/***
	 * Sets an object containing variable names from Stata data set.
	 * Requires the variable index.
	 */
	@JsonSetter
	public void setVariableNames() {

		List<String> tmp = new ArrayList<>();

		// Iterate over the variable indices
		// Add the variable name to the list object
		tmp.addAll(this.varindex.stream().map(Data::getVarName).
					collect(Collectors.toList()));

		// Set the variable names member variable
		this.varnames = tmp;

	} // End setter method for variable names

	/***
	 * Sets an object containing variable labels from Stata data set.
	 * Requires the variable index.
	 */
	@JsonSetter
	public void setVariableLabels() {

		List<String> tmp = new ArrayList<>();

		// Iterate over the variable indices
		// Add the variable name to the list object
		tmp.addAll(this.varindex.stream().map(Data::getVarLabel).
				collect(Collectors.toList()));

		// Set the variable names member variable
		this.varlabels = tmp;

	} // End setter method for variable labels

	/***
	 * Sets an object containing name of value label associated with the
	 * index value
	 * Requires the variable index.
	 */
	@JsonSetter
	public void setValueLabelNames() {

		List<String> tmp = new ArrayList<>();

		// Iterate over the variable indices
		for (Integer vdx : this.varindex) {

			// Assign the variable label name to a temporary variable
			String tmpLabel = ValueLabel.getVarValueLabel(vdx);

			// If the method returned null add an empty string to the list
			// object.  Otherwise, add the value returned from the method
			if (tmpLabel == null) tmp.add("");
			else tmp.add(tmpLabel);

		} // End Loop

		// Set the variable names member variable
		this.valueLabelNames = tmp;

	} // End setter method for variable labels


	/***
	 * Sets an object with the value labels defined for a given variable.
	 * Requires the variable index.
	 */
	@JsonSetter
	public void setValueLabels() {

		// Initialize temporary container object
		List<Object> valabs = new ArrayList<>();

		// Loop over the variable indices
		for (String vdx : this.valueLabelNames) {

			// Create temp object to store the value label set
			Map<Integer, String> labels = new HashMap<>();

			// Test whether the object is null/empty
			if (vdx == null) {

				// If the temporary variable is null assign an empty string to
				// the map object
				labels.put(0, "");

			} else if (!vdx.isEmpty()) {

				// Add the value labels to the object
				labels.putAll(ValueLabel.getValueLabels(vdx));

			} else {

				// For variables w/o valid variable labels add this as the
				// pseudo value label
				labels.put(0, "");

			} // End IF/ELSE Block for value labels

			// Add the specific value label metadata to the list object
			valabs.add(labels);

		} // End Loop over value label name index

		// Set the value label index variable
		this.valueLabels = valabs;

	} // End setter method for value label index

	/***
	 * Sets an object containing booleans indicating whether the variable
	 * is/isn't a string.
	 * Requires the variable index.
	 */
	@JsonSetter
	public void setVariableTypeIndex() {

		List<Boolean> tmp = new ArrayList<>();

		// Iterate over the variable indices
		// Add the variable name to the list object
		tmp.addAll(this.varindex.stream().map(Data::isVarTypeString).
				collect(Collectors.toList()));

		// Set the variable type index
		this.varTypes = tmp;

	} // End setter method for variable type index

	/***
	 * Accessor method for variable index variable
	 *
	 * @return A list of Integer objects containing variable indices
	 */
	@JsonGetter
	public List<Integer> getVariableIndex() {
		return this.varindex;
	}

	/***
	 * @return A list of String objects containing variable names
	 */
	@JsonGetter
	public List<String> getVariableNames() {
		return this.varnames;
	}

	/***
	 * @param varidx valid variable index value
	 * @return Name of variable at index varidx
	 */
	@JsonGetter
	public String getName(int varidx) {
		return this.varnames.get(varidx);
	}

	/***
	 * @param varidx valid variable index value
	 * @return Variable Label
	 */
	@JsonGetter
	public String getVarLabel(int varidx) {
		return this.varlabels.get(varidx);
	}

	/***
	 * @param varidx valid variable index value
	 * @return Name of variable at index varidx
	 */
	@JsonGetter
	public Boolean getVarType(int varidx) {
		return this.varTypes.get(varidx);
	}

	/***
	 * @param varidx valid variable index value
	 * @return Name of value label associated with a given variable index
	 */
	@JsonGetter
	public String getValueLabelName(int varidx) { return this.valueLabelNames
			.get(varidx); }

	/***
	 * @return A list of String objects containing variable labels.
	 */
	@JsonGetter
	public List<String> getVariableLabels() {
		return this.varlabels;
	}

	/***
	 * @return A list of String objects containing value label names.
	 */
	@JsonGetter
	public List<String> getValueLabelNames() {
		return this.valueLabelNames;
	}

	/***
	 * @return A list of Map objects containing the value/label pairs for
	 * labeled variables or the keyword "skip" to indicate the variable does not
	 * have any value labels associated with it.
	 */
	@JsonGetter
	public List<Object> getValueLabels() {
		return this.valueLabels;
	}

	/***
	 * @return A list of Boolean objects indicating if variable is a string
	 */
	@JsonGetter
	public List<Boolean> getVariableTypes() {
		return this.varTypes;
	}

} // End Class definition
