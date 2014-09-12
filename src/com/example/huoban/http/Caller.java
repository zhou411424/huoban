package com.example.huoban.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.ConnectionPoolTimeoutException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.example.huoban.utils.LogUtil;




/**
 * Http 
 * 
 * 
 */
public class Caller {
	

	public static String httpExecute(Task task) {
		String resultStr = null;
		switch (task.taskHttpTpye) {
		case HTTPConfig.HTTP_GET:
			resultStr = doGet(task);
			break;
		case HTTPConfig.HTTP_POST:
			resultStr = doPost(task);
			break;
		}

		return resultStr;
	}

	@SuppressWarnings("rawtypes")
	 private static String doGet(Task task) {
		String resultStr = null;

		task.bTimeout = false;

		// 先将参数放入List，再对参数进行URL编码
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();

		if (task.taskParam != null && ((Map) task.taskParam).size() > 0) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) task.taskParam;
			Set<String> keys = map.keySet();

			for (String key : keys) {
				params.add(new BasicNameValuePair(key, (String) map.get(key)));
			}
		}

		// 对参数编码
		String param = URLEncodedUtils.format(params, "UTF-8");

		// 将URL与参数拼接
		HttpGet getMethod = new HttpGet(task.taskUrl + param);

		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = null;

//		Log.d("httpGet", "URI = " + getMethod.getURI());
		LogUtil.logE("URI = " + getMethod.getURI());
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, HTTPConfig.CONNECTION_TIME_OUT);
		// 读取超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				HTTPConfig.READ_TIME_OUT);

		try {
			response = httpClient.execute(getMethod); // 发起GET请求
		} catch (ConnectionPoolTimeoutException e) {
			task.bTimeout = true;
		} catch (ConnectTimeoutException e) {
			task.bTimeout = true;
		} catch (SocketTimeoutException e) {
			task.bTimeout = true;
		} catch (ClientProtocolException e) {
			task.bTimeout = true;
			e.printStackTrace();
		} catch (IOException e) {
			task.bTimeout = true;
			e.printStackTrace();
		}

		if (response == null) {
			return null;
		}

		HttpEntity resEntity = response.getEntity();

		if (resEntity != null) {
			try {
				resultStr = EntityUtils.toString(resEntity, "utf-8");

			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (resEntity != null) {
						resEntity.consumeContent(); // Finish
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		httpClient.getConnectionManager().shutdown(); // Close all connection

		return resultStr;
	}

	/*
	 * private static String doPost(Task task) { String BOUNDARY =
	 * java.util.UUID.randomUUID().toString(); String PREFIX = "--", LINEND =
	 * "\r\n"; String MULTIPART_FROM_DATA = "multipart/form-data"; String
	 * CHARSET = "UTF-8";
	 * 
	 * String resultStr = null; try { String url = Constant.HOST_NAME +
	 * task.taskQuery; URL uri = new URL(url); HttpURLConnection conn =
	 * (HttpURLConnection) uri.openConnection(); conn.setReadTimeout(10 * 1000);
	 * conn.setConnectTimeout(15 * 1000); conn.setDoInput(true); //允许输入�?
	 * conn.setDoOutput(true);// 允许输出�?conn.setUseCaches(false);
	 * 
	 * conn.setRequestMethod("POST"); // Post请求方式
	 * conn.setRequestProperty("connection", "keep-alive");
	 * conn.setRequestProperty("Charsert", "UTF-8");
	 * 
	 * conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA +
	 * ";boundary=" + BOUNDARY);
	 * 
	 * // 拼接参数 StringBuilder sb = new StringBuilder(); for (Map.Entry<String,
	 * String> entry : ((Map<String, String>) task.taskParam) .entrySet()) {
	 * sb.append(PREFIX); sb.append(BOUNDARY); sb.append(LINEND);
	 * sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() +
	 * "\"" + LINEND); sb.append("Content-Type: text/plain; charset=" + CHARSET
	 * + LINEND); sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
	 * sb.append(LINEND); sb.append(entry.getValue()); sb.append(LINEND); }
	 * 
	 * DataOutputStream outStream = new DataOutputStream(
	 * conn.getOutputStream()); outStream.write(sb.toString().getBytes());
	 * FormFile file = task.taskFile; if (file != null) { StringBuilder sb1 =
	 * new StringBuilder(); sb1.append(PREFIX); sb1.append(BOUNDARY);
	 * sb1.append(LINEND);
	 * sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\"" +
	 * file.getFormName() + "\"" + LINEND);
	 * sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET +
	 * LINEND); sb1.append(LINEND); outStream.write(sb1.toString().getBytes());
	 * 
	 * InputStream is = new FileInputStream(file.getFile()); byte[] buffer = new
	 * byte[1024]; int len = 0; while ((len = is.read(buffer)) != -1) {
	 * outStream.write(buffer, 0, len); }
	 * 
	 * is.close(); outStream.write(LINEND.getBytes()); }
	 * 
	 * 
	 * // 结束�?byte[] end_data = (PREFIX + BOUNDARY + PREFIX +
	 * LINEND).getBytes(); outStream.write(end_data); outStream.flush();
	 * outStream.close(); resultStr =
	 * convertStreamToString(conn.getInputStream()); conn.disconnect();
	 * Log.d("httpPost", "url:" + url); Log.d("httpPost", "result:" +
	 * resultStr); } catch (MalformedURLException e) { e.printStackTrace(); }
	 * catch (IOException e) { e.printStackTrace(); } return resultStr; }
	 */
	@SuppressWarnings("rawtypes")
	private static String doPost(Task task) {
		String sRes = null;

		task.bTimeout = false;

		if (task.taskParam == null) {
			return null;
		}

		String url = task.taskUrl;
		// 需要下载apache公司下的HttpComponents项目下的HTTPCLIENT-httpmime-4.2.5.jar
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		// IOS都是60s
		// 请求超时
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, HTTPConfig.CONNECTION_TIME_OUT);
		// 读取超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				HTTPConfig.READ_TIME_OUT);

		if (task.taskParam instanceof Map) {
			/*
			 * httpPost.addHeader("Content-Type", "application/json");
			 * 
			 * JSONObject obj = new JSONObject();
			 * 
			 * // 拼接参数 for (Map.Entry<String, String> entry : ((Map<String,
			 * String>) task.taskParam).entrySet()) { try {
			 * obj.put(entry.getKey(), entry.getValue()); } catch (JSONException
			 * e) { e.printStackTrace(); } }
			 */

			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();

			if (task.taskParam != null && ((Map) task.taskParam).size() > 0) {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) task.taskParam;
				Set<String> keys = map.keySet();

				for (String key : keys) {
					params.add(new BasicNameValuePair(key, (String) map
							.get(key)));
				}
			}

			try {
				// httpPost.setEntity(new StringEntity(obj.toString(),
				// "UTF-8"));
				// httpPost.setEntity(new
				// StringEntity(URLEncoder.encode(obj.toString(), "UTF-8")));
				httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else if (task.taskParam instanceof LinkedList) {
			@SuppressWarnings("unchecked")
			List<BasicNameValuePair> params = (LinkedList<BasicNameValuePair>) task.taskParam;

			try {
				httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else if (task.taskParam instanceof MultipartEntity) {
			MultipartEntity mulentity = (MultipartEntity) task.taskParam;

			// httpPost.addHeader("Content-Type", "multipart/form-data");
			httpPost.setEntity(mulentity);
		}

		HttpResponse response = null;

		try {
			response = httpClient.execute(httpPost);
		} catch (ConnectionPoolTimeoutException e) {
			task.bTimeout = true;
		} catch (ConnectTimeoutException e) {
			task.bTimeout = true;
		} catch (SocketTimeoutException e) {
			task.bTimeout = true;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (response == null) {
			return null;
		}

		HttpEntity resEntity = response.getEntity();

		if (resEntity != null) {
			try {
				sRes = EntityUtils.toString(resEntity, "utf-8");

				 LogUtil.logE("httpPost", "url:" + httpPost.getURI()+task.taskParam );
				// Log.d("httpPost", "result:" + sRes);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					resEntity.consumeContent(); // Finish
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		httpClient.getConnectionManager().shutdown(); // Close all connection

		return sRes;
	}

	// 输入流转换成字符串
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}
}
