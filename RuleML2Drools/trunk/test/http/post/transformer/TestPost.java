package http.post.transformer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

public class TestPost {
	public static void main(String[] args) {
		String url = "http://localhost:8080/TranslatorService/TranslatorServiceServlet";
		String fileName = "rules/drools/test.drl";
		try {
//			URL url1 = new URL("http://localhost:8080/TranslatorService/rrml2prova_1.0.xsl");
//			InputStream in = url1.openStream();
			
			File file = new File(fileName);
			FileEntity requestEntity = new FileEntity(file,"text/plain");
			requestEntity.setContentEncoding("UTF-8");
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(requestEntity);
			HttpParams params = new BasicHttpParams();
			params.setParameter("transformer", "Drools2RuleMLTranslator");
			httpPost.setParams(params);

			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httpPost);
			printResult(response);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static void printResult(HttpResponse response)
			throws IllegalStateException, IOException {
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream instream = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					instream));

			String result = "";
			String line = reader.readLine();

			while (line != null) {
				result += line +"\r\n";
				line = reader.readLine();
			}
			// return result;
			System.out.println(result);
		}
	}
}
