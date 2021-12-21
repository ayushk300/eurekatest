package org.codejudge.sb.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codejudge.sb.dto.HTTPResponse;

import static org.apache.http.HttpHeaders.ACCEPT;

@Slf4j
public class HttpClientCustom {

    private static final String CONNECTION_TIMEOUT = "CONNECTION_TIMEOUT";
    private static final String CONNECTION_REQUEST_TIMEOUT = "CONNECTION_REQUEST_TIMEOUT";
    private static final String SOCKET_TIMEOUT = "SOCKET_TIMEOUT";
    private static final String APPLICATION_JSON = "application/json";

    private HttpClientCustom() {

    }

    private static HttpClientCustom instance;

    public static HttpClientCustom getInstance() {

        if(instance == null) {
            instance = new HttpClientCustom();
        }
        return instance;
    }

    public HTTPResponse httpGet(String url, Map<String, String> requestHeaders) {
        log.info("httpGet request with url: {}", url);

        try {
            HttpGet getRequest = this.prepareHttpGet(url, requestHeaders);
            return this.processRequest(getRequest, new HashMap());
        } catch (IOException var6) {
            log.error(CommonUtil.getDetailedExceptionMessage(var6));
            throw new RuntimeException(var6);
        }
    }

    private HttpGet prepareHttpGet(String url, Map<String, String> requestHeaders) {
        HttpGet getRequest = new HttpGet(url);

        getRequest.addHeader(ACCEPT, APPLICATION_JSON);

        if (MapUtils.isNotEmpty(requestHeaders)) {
            requestHeaders.forEach(getRequest::addHeader);
        }
        return getRequest;
    }


    private HTTPResponse processRequest(HttpUriRequest httpUriRequest, Map<String,Object> configMap) throws IOException {
        log.info("processing request");
        CloseableHttpResponse executeResponse = null;

        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(getRequestConfig(configMap)).build()){

                executeResponse = httpClient.execute(httpUriRequest);


            return new HTTPResponse(executeResponse.getStatusLine().getStatusCode(), executeResponse.getStatusLine().getReasonPhrase(),
                    IOUtils.toString(executeResponse.getEntity().getContent(), StandardCharsets.UTF_8), executeResponse.getAllHeaders());
        } finally {
            if(executeResponse != null)
                executeResponse.close();
        }
    }

    private static  RequestConfig getRequestConfig(Map<String,Object> configMap) {
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        if(MapUtils.isNotEmpty(configMap)){
            if(configMap.get(CONNECTION_TIMEOUT)!=null){
                configBuilder.setConnectTimeout((Integer) configMap.get(CONNECTION_TIMEOUT));
            }
            if(configMap.get(SOCKET_TIMEOUT)!=null){
                configBuilder.setSocketTimeout((Integer) configMap.get(SOCKET_TIMEOUT));
            }
            if(configMap.get(CONNECTION_REQUEST_TIMEOUT)!=null){
                configBuilder.setConnectionRequestTimeout((Integer) configMap.get(CONNECTION_REQUEST_TIMEOUT));
            }
        }
        return configBuilder.build();
    }

}
