package com.videolib.android.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.webkit.ValueCallback;

import com.worthcloud.avlib.utils.FileUtils;
import com.worthcloud.avlib.utils.LogUtils;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


/**
 * http工具类
 *
 * @author DZS dzsdevelop@163.com
 * @version V2.0
 * @date 2015-12-23 上午9:55:16
 */
public class PDZCtrl extends AsyncTask<Object, Integer, String> {
    private static final String UTF_8 = "UTF-8";
    private final static int TIMEOUT_CONNECTION = 3000;
    private final static int TIMEOUT_READ = 10000;
    private final static int RETRY_TIME = 3;
    private static String BOUNDARY = UUID.randomUUID().toString();
    private static String twoHyphens = "--";
    private static String lineEnd = System.getProperty("line.separator");
    public final static String URL_RELEASE = "http://open.worthcloud.net/v2/devices";
    public final static String URL_DEBUG = "http://opent.worthcloud.net/v2/devices";
    private ValueCallback<String> resultCallback;

    private PDZCtrl() {
    }

    private HttpURLConnection getHttpUrlConnect(String urlString, String method, String auth) throws IOException {
        HttpURLConnection connection;
        URL url = new URL(urlString);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        setConnect(connection, method, auth);
        connection.connect();
        return connection;
    }

    private HttpsURLConnection getHttpsUrlConnect(String urlString, String method, InputStream inputStream, String auth) throws Exception {
        HttpsURLConnection connection;
        URL url = new URL(urlString);
        if (inputStream == null) initSSLAll();
        connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        setConnect(connection, method, auth);
        connection.connect();
        if (inputStream != null) initSSL(connection, inputStream);
        return connection;
    }

    private void setConnect(URLConnection connection, String method, String auth) {
        connection.setDoInput(true);
        connection.setDoOutput(method.equals("POST"));
        connection.setUseCaches(false);
        connection.setConnectTimeout(TIMEOUT_CONNECTION);
        connection.setReadTimeout(TIMEOUT_READ);
        connection.setRequestProperty("Accept-Charset", UTF_8);
        connection.setRequestProperty("User-Agent", getUserAgent().toString());
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Authorization", auth);
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "multipart/form-data" + "; boundary=" + BOUNDARY);
    }

    private StringBuilder getUserAgent() {
        return new StringBuilder("DZSDevelop_Android")
                .append("/").append(android.os.Build.MODEL)//手机型号
                .append("/").append(android.os.Build.VERSION.RELEASE);//手机系统版本
    }

    /**
     * get请求，以表单形式提交
     *
     * @param url 网络地址
     */
    public String httpURLConnect_Get(String url, InputStream inputStream, String auth) {
        int time = 0;
        String resultString = "";
        InputStream is = null;
        HttpURLConnection connection = null;
        HttpsURLConnection httpsURLConnection = null;
        boolean isHttps = url.startsWith("https");
        //进行三次访问网络
        do {
            try {
                connection = isHttps ? null : getHttpUrlConnect(url, "GET", auth);
                httpsURLConnection = isHttps ? getHttpsUrlConnect(url, "GET", inputStream, auth) : null;
                int statueCode = isHttps ? httpsURLConnection.getResponseCode() : connection.getResponseCode();
                if (statueCode != HttpURLConnection.HTTP_OK) {
                    time++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        LogUtils.exception(e1);
                    }
                    continue;
                }
                is = isHttps ? httpsURLConnection.getInputStream() : connection.getInputStream();
                resultString = FileUtils.input2String(is);
                LogUtils.info("Network-URL(GET)返回状态值：" + statueCode);
                LogUtils.info("Network-URL(GET)地址：" + url + "  返回值：" + resultString);
                break;
            } catch (Exception e) {
                LogUtils.exception(e);
                time++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LogUtils.exception(e1);
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                    connection = null;
                }
                if (httpsURLConnection != null) {
                    httpsURLConnection.disconnect();
                    httpsURLConnection = null;
                }
                FileUtils.closeIO(is);
            }
        } while (time < RETRY_TIME);
        return resultString;
    }

    /**
     * 添加表单数据
     *
     * @param params 参数列表
     * @param output 输出流
     */
    private void addFormField(Set<Map.Entry<String, Object>> params, DataOutputStream output) throws IOException {
        if (params != null) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> param : params) {
                sb.append(twoHyphens).append(BOUNDARY).append(lineEnd);
                sb.append("Content-Disposition: form-data; name=\"").append(param.getKey()).append("\"").append(lineEnd);
                sb.append(lineEnd);
                sb.append(param.getValue()).append(lineEnd);
            }
            output.write(new String(sb.toString().getBytes(), UTF_8).getBytes());
        }
    }

    private void initSSL(HttpsURLConnection httpsURLConnection, InputStream inputStream) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate ca = cf.generateCertificate(inputStream);
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(null, null);
        keystore.setCertificateEntry("ca", ca);
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keystore);
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);
        httpsURLConnection.setSSLSocketFactory(context.getSocketFactory());
    }

    @SuppressLint("TrustAllX509TrustManager")
    private void initSSLAll() throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }}, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });
    }

    /**
     * 检查网络是否连接
     *
     * @param context Context
     * @return
     */
    public boolean checkNetConttent(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * map 转换URL
     */
    private String mapToCatchUrl(String url, Map<String, Object> parameters, String... variableKey) throws UnsupportedEncodingException {
        StringBuilder getUrl = new StringBuilder(url);
        if (parameters != null && !parameters.isEmpty()) {
            getUrl.append("?");
            for (Map.Entry<String, Object> param : parameters.entrySet()) {
                if (variableKey != null) {
                    for (String key : variableKey) {
                        if (param.getKey().equals(key)) break;
                    }
                }
                getUrl.append(param.getKey()).append("=").append(URLEncoder.encode(param.getValue() == null ? "" : param.getValue().toString(), UTF_8));
                getUrl.append("&");
            }
            getUrl.deleteCharAt(getUrl.length() - 1);
        }
        return getUrl.toString();
    }

    @Override
    protected String doInBackground(Object... objects) {
        try {
            return httpURLConnect_Get(mapToCatchUrl((String) objects[0], (Map<String, Object>) objects[1]), null, (String) objects[2]);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String json) {
        super.onPostExecute(json);
        try {
            if (TextUtils.isEmpty(json) && resultCallback != null) {
                resultCallback.onReceiveValue("Data parsing exception");
                return;
            }
            JSONObject jsonObject = new JSONObject(json);
            String code = jsonObject.getString("code");
            String message = jsonObject.getString("message");
            if (!"0".equals(code) && resultCallback != null) {
                resultCallback.onReceiveValue(message);
            }
        } catch (Exception e) {
            if (resultCallback != null) {
                resultCallback.onReceiveValue("Data parsing exception");
            }
        }

    }

    public static void ctrl(String url, String mac_id, int direction, int presstime, int action, String token, ValueCallback<String> result) {
        ///v2/devices/:mac_id/:direction/:action
        PDZCtrl pdzCtrl = new PDZCtrl();
        pdzCtrl.resultCallback = result;
        pdzCtrl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url + "/" + mac_id + "/" + direction + "/" + (action == 0 ? "stop" : "start"), new HashMap<>().put("presstime", presstime + ""), token);
    }
}
