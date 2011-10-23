package ruleml.translator.web;

import javax.faces.model.SelectItem;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

public class TranslationBean {

	private String input;
	private String output;
	private String inputLanguage = "Drools:1.0";
	private String outputLanguage = "Prova:1.0";

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getInputLanguage() {
		return inputLanguage;
	}

	public void setInputLanguage(String inputLanguage) {
		this.inputLanguage = inputLanguage;
	}

	public String getOutputLanguage() {
		return outputLanguage;
	}

	public void setOutputLanguage(String outputLanguage) {
		this.outputLanguage = outputLanguage;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public String getOutput() {
		return output;
	}

	public void translate() {
		System.out.println("Sending the request to the Mule ESB ...");
		output = callWS(
				"translate",
				new String[] { "input", input, "inputLanguage",
						inputLanguage.split(":")[0], "inputVersion",
						inputLanguage.split(":")[1], "outputLanguage",
						outputLanguage.split(":")[0], "outputVersion",
						outputLanguage.split(":")[1] });
	}

	public SelectItem[] getSupportedInputLanguages() {

		String[] split = callWS("getSupportedInputLanguages").split("\n");
		SelectItem[] supportedInputLanguages = new SelectItem[split.length];
		for (int i = 0; i < supportedInputLanguages.length; i++) {
			supportedInputLanguages[i] = new SelectItem(split[i], split[i]);
		}

		return supportedInputLanguages;
	}

	public SelectItem[] getSupportedOutputLanguages() {
		String[] split = callWS("getSupportedOutputLanguages").split("\n");
		SelectItem[] supportedInputLanguages = new SelectItem[split.length];
		for (int i = 0; i < supportedInputLanguages.length; i++) {
			supportedInputLanguages[i] = new SelectItem(split[i], split[i]);
		}

		return supportedInputLanguages;
	}

	private String callWS(String method, String... args) {
		EndpointReference targetEPR = new EndpointReference(
				"http://localhost:9988/RulesTranslatorServiceHTTP");
		try {
			OMFactory fac = OMAbstractFactory.getOMFactory();
			OMNamespace ns = fac.createOMNamespace("http://soapinterop.org/",
					"ns1");
			OMElement payload = fac.createOMElement(method, ns);

			for (int i = 0; i < args.length; i += 2) {
				OMElement arg = fac.createOMElement(args[i], ns);
				arg.addChild(fac.createOMText(arg, args[i + 1]));
				payload.addChild(arg);
			}

			Options options = new Options();
			ServiceClient client = new ServiceClient();
			options.setTo(targetEPR);
			client.setOptions(options);

			// Blocking invocation
			OMElement result = client.sendReceive(payload);
			System.out.println(result.getFirstElement().getText());
			String s = result.getFirstElement().getText();

			return s;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
