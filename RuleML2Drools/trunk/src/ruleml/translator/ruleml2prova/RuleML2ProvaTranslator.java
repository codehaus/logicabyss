package ruleml.translator.ruleml2prova;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.mule.transformer.AbstractTransformer;

import ruleml.translator.service.RulesLanguage;
import ruleml.translator.service.Translator;

/**
 * <code>RuleML2ProvaTranslator</code> translate a Reaction RuleML message into a Prova message The
 * translator uses the specified XSLT.
 * 
 * If the RuleML message can not be translated, the original RuleML message will be returned
 * 
 * @author <a href="mailto:adrian.paschke@gmx.de">Adrian Paschke</a>
 * @version
 */
public class RuleML2ProvaTranslator implements Translator{
	protected static transient Logger LOGGER = Logger.getLogger(RuleML2ProvaTranslator.class
			.getName());
	/**
	 * Serial version
	 */
	private static final long serialVersionUID = -408128452488674866L;
	private final TransformerFactory tFactory = TransformerFactory.newInstance();
	private InputStream is = null;
	private Source xmlSource = null;
	private Source xslSource = null;
	private Transformer transformer = null;
	private String xslt = "rrml2prova.xsl";

	public RuleML2ProvaTranslator() {
		super();
	}

	public RuleML2ProvaTranslator(String _xslt) {
		super();
		xslt = _xslt;
	}

	public void setXSLT(String _xslt) {
		xslt = _xslt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.transformers.AbstractTransformer#doTransform(java.lang.Object)
	 * 
	 * @returns the input message if the translation fails
	 */
	@Override
	public Object translate(Object src) {
		if (src instanceof String) {
			try {
				InputStream in = null;
				try {
					// read model from file on classpath
					in = Thread.currentThread().getContextClassLoader().getResourceAsStream(xslt);
					if (in == null) {
						// read xslt from file given a relative path
						in = new FileInputStream(xslt);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					// read model from URL
					URL url = new URL(xslt);
					in = url.openStream();
				}
				if (in == null) {
					LOGGER.log(Level.SEVERE, "Can not load XSLT");
					return src; // no XSLT translator; return untranslated
					// message
				}
				xslSource = new StreamSource(in); // XSLT Source
				// Get the XML input
				String message = src.toString();
				if (message.indexOf("'") != -1) {
					String query = message.substring(message.indexOf("'"), message.lastIndexOf("'") + 1);
					String encodedQuery = URLEncoder.encode(query.substring(1, query.length() - 1), "UTF-8");
					message = message.replace(query, encodedQuery);
				}
				if ((message != null) && (message.length() > 0)) {
					is = new ByteArrayInputStream(message.getBytes());
				}
				if (is == null) {
					LOGGER.log(Level.SEVERE, "XML input message invalid");
					return src;
				}
				xmlSource = new StreamSource(is); // XML Source
				// transform XML message into Prova RMessage
				transformer = tFactory.newTransformer(xslSource);
				// Perform the transformation.
				StringWriter provaMessage = new StringWriter();
				transformer.transform(xmlSource, new StreamResult(provaMessage));
				// Extract complex objects from String
				String output = provaMessage.toString();
				// ProvaList list = new String2ProvaList().createProvaList(output);
				return output;
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.log(Level.SEVERE, "Error during translation of RuleML message into RMessage");
				LOGGER.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
				return src; // simply return untranslated message
			}
		}
		return src; // no translator found; return untranslated message
	}

	@Override
	public RulesLanguage getInputLanguage() {
		return new RulesLanguage("RuleML","1.0");
	}

	@Override
	public RulesLanguage getOutputLanguage() {
		return new RulesLanguage("Prova","1.0");
	}
}