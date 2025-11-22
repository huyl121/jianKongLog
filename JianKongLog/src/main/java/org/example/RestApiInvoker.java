package org.example;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


abstract class RestApiInvoker {

    private static final Logger log = LoggerFactory.getLogger(RestApiInvoker.class);
    private static final OkHttpClient client = new OkHttpClient().newBuilder().build();



    static <T> T callSync(RestApiRequest<T> request) {
        try {
            String str;
            Call call = client.newCall(request.request);

            Response response = call.execute();
            if (response != null && response.body() != null) {
                str = response.body().string();
                response.close();
            } else {
                str = "";
            }
            JsonWrapper jsonWrapper = JsonWrapper.parseFromString(str);
            checkResponse(jsonWrapper);
            return request.jsonParser.parseJson(jsonWrapper);
        } catch (Exception e) {

        }
        return request.jsonParser.parseJson(new JsonWrapper(new JSONObject()));
    }

    static void checkResponse(JsonWrapper json) {

    }

    static WebSocket createWebSocket(Request request, WebSocketListener listener) {
        return client.newWebSocket(request, listener);
    }

}
