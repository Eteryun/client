package com.eteryun.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {
	private static String AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36 Edg/87.0.664.60";

	static {
		HttpURLConnection.setFollowRedirects(true);
	}

	public static HttpURLConnection make(String url, String method) throws IOException {

		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

		connection.setRequestMethod(method);
		connection.setConnectTimeout(2000);
		connection.setReadTimeout(10000);

		connection.setRequestProperty("User-Agent", AGENT);

		connection.setInstanceFollowRedirects(true);
		connection.setDoOutput(true);

		return connection;
	}

	public static String request(String url, String method) throws IOException {
		HttpURLConnection connection = make(url, method);

		return connection.getInputStream().toString();
	}

	public static void download(String url, File file) throws IOException {
		HttpURLConnection connection = make(url, "GET");
		if (connection != null) {
			FileOutputStream output;
			output = new FileOutputStream(file);
			connection.getInputStream().transferTo(output);
			output.close();
			connection.disconnect();
		}
	}
}
