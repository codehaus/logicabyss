package ruleml.translator.service.mule;

import org.mule.extras.client.MuleClient;
import org.mule.umo.UMOException;
import org.mule.umo.UMOMessage;

public class AxisTestCase {
	public static void main(String[] args) {
		try {
			MuleClient muleClient = new MuleClient();
			UMOMessage result = muleClient.send(
					"axis:http://localhost:9988/RulesTranslatorService?method=getSupportedInputLanguages",new Object[]{}, null);
			Object payload = result.getPayload();
			Object[] languages = (Object[]) payload;
			for (Object object : languages) {
				System.out.println(object);
			}
			
		} catch (UMOException e) {
			e.printStackTrace();
		}
	}
}
