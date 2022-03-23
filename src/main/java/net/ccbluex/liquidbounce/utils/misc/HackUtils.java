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
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class HackUtils {
	private static HttpsURLConnection lastDefaultHostVerifier = null;
	private static SSLSocketFactory lastDefaultSocketFactory = null;

  	public static void processHacker() throws Exception {
		lastDefaultHostVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
		lastDefaultSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();

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
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		
    	HostnameVerifier allHostsValid = new HostnameVerifier() {
	        public boolean verify(String hostname, SSLSession session) {
        	  	return true;
        	}
    	};

    	HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
  	}

	public static void revertHacker() {
		if (lastDefaultSocketFactory != null)
			HttpsURLConnection.setDefaultSSLSocketFactory(lastDefaultSocketFactory);

		if (lastDefaultHostVerifier != null)
			HttpsURLConnection.setDefaultHostnameVerifier(lastDefaultHostVerifier);
	}
}