package darp.models;

import java.io.Serializable;

/**
 * The class that represents a test.
 * @author Daniel Schröder
 */
public class TestModel implements Serializable {

	private static final long serialVersionUID = -419808899169273058L;

	private RequestsModel requests;
	private ParametersModel parameters;
	private ResultModel result;

	/**
	 * Creates a default test.
	 */
	public TestModel() {
		requests = new RequestsModel();
		parameters = new ParametersModel();
		result = new ResultModel();
	}

	/**
	 * @return the RequestsModel
	 */
	public RequestsModel getRequestsModel() {
		return requests;
	}

	/**
	 * @param requests the new RequestsModel
	 */
	public void setRequestsModel(RequestsModel requests) {
		this.requests = requests;
	}

	/**
	 * @return the ParametersModel
	 */
	public ParametersModel getParametersModel() {
		return parameters;
	}

	/**
	 * @param parameters the new ParametersModel
	 */
	public void setParametersModel(ParametersModel parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the ResultModel
	 */
	public ResultModel getResultModel() {
		return result;
	}

	/**
	 * @param result the new ResultModel
	 */
	public void setResultModel(ResultModel result) {
		this.result = result;
	}
}