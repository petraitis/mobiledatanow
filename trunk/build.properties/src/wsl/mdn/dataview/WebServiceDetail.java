package wsl.mdn.dataview;
import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.*;
import javax.wsdl.*;
public class WebServiceDetail  implements Serializable {
	private Operation operation = null;
	private String objectName = null;
	private ArrayList parameters = new ArrayList();
	private String type = null;
	public final static String TYPE_COMPLEX    = "COMPLEX";
	public final static String TYPE_SIMPLE     = "SIMPLE";
	//private Vector operations = null;
	public WebServiceDetail(Operation operation) {
		super();
		this.operation = operation;
	}

	public void addParameter(String parameter){
		parameters.add(parameter);
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public ArrayList getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList parameters) {
		this.parameters = parameters;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
}
