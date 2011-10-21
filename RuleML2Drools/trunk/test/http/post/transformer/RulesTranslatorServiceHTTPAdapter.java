package http.post.transformer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

import org.mule.umo.UMOEventContext;
import org.mule.umo.lifecycle.Callable;
import org.mule.util.PropertiesUtils;

import ruleml.translator.Util;
import ruleml.translator.service.RulesLanguage;
import ruleml.translator.service.RulesTranslatorService;
import ruleml.translator.service.RulesTranslatorServiceFactory;

public class RulesTranslatorServiceHTTPAdapter implements Callable {
	public String test(String s) {
		System.out.println("The method was called : " + s);
		return "<html> <body>AAAAAAAAAA</body></html>";
	}

	public Integer mist(Integer i) {
		System.out.println("The wrong method was called");
		return 5;
	}

	public Integer list(RulesLanguage l) {
		System.out.println("Language " + l.getName() + " " + l.getVersion());
		return 5;
	}

	@Override
	public Object onCall(UMOEventContext eventContext) throws Exception {
		try {
			byte[] bytes = (byte[]) eventContext.getMessageAsBytes();
			String request;
			request = new String(bytes, "UTF8");
			request = URLDecoder.decode(request, "UTF8");
			int i = request.indexOf('?');
			String query = request.substring(i + 1);
			Properties p = PropertiesUtils.getPropertiesFromQueryString(query);
			for (Object k : p.keySet()) {
				System.out.println("!!! " + k + " - " + p.get(k));
			}

			RulesTranslatorService translatorService = RulesTranslatorServiceFactory
					.createTranslatorService();

			String result = translatorService.translate(p.get("input").toString(),
					new RulesLanguage("Drools", "1.0"), new RulesLanguage(
							"Prova", "1.0"));

			return result;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "Error";
		}
	}
}
