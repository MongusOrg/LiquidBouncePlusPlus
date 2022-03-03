/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.utils.misc;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class HackUtils {
  	public static void fixConnection(HttpsURLConnection connection) throws Exception {
		/*
	 	*  fix for
	 	*    Exception in thread "main" javax.net.ssl.SSLHandshakeException:
	 	*       sun.security.validator.ValidatorException:
	 	*           PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:
	 	*               unable to find valid certification path to requested target
	 	*/
		TrustManager[] trustAllCerts = new TrustManager[] {
	   	new X509TrustManager() {
		  	public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
		  	}

		  	public void checkClientTrusted(X509Certificate[] certs, String authType) {  }

		  	public void checkServerTrusted(X509Certificate[] certs, String authType) {  }

	   	}
		};
		
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		connection.setSSLSocketFactory(sc.getSocketFactory());

		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
		  	return true;
			}
		};

		// Install the all-trusting host verifier
		connection.setHostnameVerifier(allHostsValid);
  	}
}