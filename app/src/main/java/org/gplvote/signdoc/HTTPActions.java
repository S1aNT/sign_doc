package org.gplvote.signdoc;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class HTTPActions {
    public static final String HTTP_SEND_URL = MainActivity.HTTP_SEND_URL;

    public static String deliver(String document, Activity activity) {
        String error_str = null;

        // Инициируется процесс отправки документа
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(HTTP_SEND_URL);

        try {
            // Add your data
            StringEntity se = null;
            se = new StringEntity(document);
            httppost.setEntity(se);
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            String body = EntityUtils.toString(response.getEntity(), "UTF-8");
            int http_status = response.getStatusLine().getStatusCode();
            if (http_status == 200) {
                Gson gson = new Gson();
                JsonResponse json_resp = gson.fromJson(body, JsonResponse.class);
                if (json_resp.status == 0) {
                    DocSign doc = gson.fromJson(document, DocSign.class);

                    if (doc.type.equals("SIGN")) {
                        // DocsStorage.add_doc(activity.getApplicationContext(), doc.site, doc.doc_id);
                        DocsStorage.set_sign(activity.getApplicationContext(), doc.site, doc.doc_id, doc.sign);
                    }
                } else {
                    error_str = activity.getString(R.string.err_http_send);
                    Log.e("HTTP", "Error HTTP response: " + body);
                }
            } else {
                error_str = activity.getString(R.string.msg_status_http_error);
                Log.e("HTTP", "HTTP response: "+http_status+" body: "+body);
            }
        } catch (UnsupportedEncodingException e) {
            error_str = activity.getString(R.string.msg_status_http_error);
            Log.e("HTTP", "Error HTTP request: ", e);
        } catch (ClientProtocolException e) {
            error_str = activity.getString(R.string.msg_status_http_error);
            Log.e("HTTP", "Error HTTP request: ", e);
        } catch (IOException e) {
            error_str = activity.getString(R.string.msg_status_http_error);
            Log.e("HTTP", "Error HTTP request: ", e);
        }

        return(error_str);
    }
}
