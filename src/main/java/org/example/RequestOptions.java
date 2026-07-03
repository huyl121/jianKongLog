package org.example;


import java.net.URL;

/**
 * The configuration for the request APIs
 */
public class RequestOptions {

    private String url = BinanceApiConstants.API_BASE_URL;

    public RequestOptions() {
    }

    public RequestOptions(RequestOptions option) {
        this.url = option.url;
    }

    /**
     * Set the URL for request.
     *
     * @param url The URL name like "https://fapi.binance.com".
     */
    public void setUrl(String url) {
        try {
            URL u = new URL(url);
            this.url = u.toString();
        } catch (Exception e) {
            return;
        }
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
