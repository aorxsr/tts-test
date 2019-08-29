package com.example.demo;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * HttpClient工具类
 */
public class HttpClientUtil {

	/**
	 * 请求编码
	 */
	private static final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * 执行HTTP POST请求
	 *
	 * @param url   url
	 * @param param 参数
	 * @return
	 */
	public static String httpPostWithJSON(String url, Map<String, ?> param) {
		CloseableHttpClient client = null;
		try {
			if (url == null || url.trim().length() == 0) {
				throw new Exception("URL is null");
			}
			HttpPost httpPost = new HttpPost(url);
			client = HttpClients.createDefault();
			if (param != null) {
				StringEntity entity = new StringEntity(JSON.toJSONString(param), DEFAULT_CHARSET);
				entity.setContentEncoding(DEFAULT_CHARSET);
				entity.setContentType("application/json");
				httpPost.setEntity(entity);
			}
			HttpResponse resp = client.execute(httpPost);
			if (resp.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(resp.getEntity(), DEFAULT_CHARSET);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(client);
		}
		return null;
	}

	/**
	 * 执行HTTP GET请求
	 *
	 * @param url   url
	 * @param param 参数
	 * @return
	 */
	public static String httpGetWithJSON(String url, Map<String, ?> param) {
		CloseableHttpClient client = null;
		try {
			if (url == null || url.trim().length() == 0) {
				throw new Exception("URL is null");
			}
			client = HttpClients.createDefault();
			if (param != null) {
				StringBuffer sb = new StringBuffer("?");
				for (String key : param.keySet()) {
					sb.append(key).append("=").append(param.get(key)).append("&");
				}
				url = url.concat(sb.toString());
				url = url.substring(0, url.length() - 1);
			}
			HttpGet httpGet = new HttpGet(url);
			HttpResponse resp = client.execute(httpGet);
			if (resp.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(resp.getEntity(), DEFAULT_CHARSET);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(client);
		}
		return null;
	}

	public static void downloadFile(String url, Map<String, ?> param, String path) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
			try {
				System.out.println(response1.getStatusLine());
				HttpEntity httpEntity = response1.getEntity();
				InputStream is = httpEntity.getContent();
				// 根据InputStream 下载文件
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				byte[] buffer = new byte[4096];
				int r = 0;
				long totalRead = 0;
				while ((r = is.read(buffer)) > 0) {
					output.write(buffer, 0, r);
					totalRead += r;
				}
				FileOutputStream fos = new FileOutputStream(path);
				output.writeTo(fos);
				output.flush();
				output.close();
				fos.close();
				EntityUtils.consume(httpEntity);
			} finally {
				response1.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 关闭HTTP请求
	 *
	 * @param client
	 */
	private static void close(CloseableHttpClient client) {
		if (client == null) {
			return;
		}
		try {
			client.close();
		} catch (Exception e) {
		}
	}

	public static void testDownLoad() {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		String HTTP_URL = "http://f0.topitme.com/0/7a/63/113144393585b637a0o.jpg";
		try {
			int contentLength = getConnection(HTTP_URL).getContentLength();
			System.out.println("文件的大小是:" + contentLength);
			if (contentLength > 32) {
				InputStream is = getConnection(HTTP_URL).getInputStream();
				bis = new BufferedInputStream(is);
				FileOutputStream fos = new FileOutputStream("C:/test/美女.jpg");
				bos = new BufferedOutputStream(fos);
				int b = 0;
				byte[] byArr = new byte[1024];
				while ((b = bis.read(byArr)) != -1) {
					bos.write(byArr, 0, b);
				}
				System.out.println("下载的文件的大小是----------------------------------------------:" + contentLength);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static HttpURLConnection getConnection(String httpUrl) throws Exception {
		URL url = new URL(httpUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type", "application/octet-stream");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.connect();
		return connection;

	}

	public static void download3(String url3, Map<String, String> par, String localFileName) {
		FileOutputStream out = null;
		InputStream in = null;

		try {
			URL url = new URL(url3);
			URLConnection urlConnection = url.openConnection();
			HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;

// true -- will setting parameters
			httpURLConnection.setDoOutput(true);
// true--will allow read in from
			httpURLConnection.setDoInput(true);
// will not use caches
			httpURLConnection.setUseCaches(false);
//// setting serialized
//			httpURLConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
//// default is GET
//			httpURLConnection.setRequestMethod("POST");
//			httpURLConnection.setRequestProperty("connection", "Keep-Alive");
//			httpURLConnection.setRequestProperty("Charsert", "UTF-8");
// 1 min
			httpURLConnection.setConnectTimeout(60000);
// 1 min
			httpURLConnection.setReadTimeout(60000);

			par.forEach((k, v) -> httpURLConnection.addRequestProperty(k, v));

// connect to server (tcp)
			httpURLConnection.connect();

			in = httpURLConnection.getInputStream();// send request to
// server
			File file = new File(localFileName);
			if (!file.exists()) {
				file.createNewFile();
			}

			out = new FileOutputStream(file);
			byte[] buffer = new byte[4096];
			int readLength = 0;
			while ((readLength = in.read(buffer)) > 0) {
				byte[] bytes = new byte[readLength];
				System.arraycopy(buffer, 0, bytes, 0, readLength);
				out.write(bytes);
			}

			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}