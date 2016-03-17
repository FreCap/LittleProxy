package org.littleshoot.proxy.custom;

import com.google.common.net.HostAndPort;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.impl.ProxyUtils;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fre on 3/15/16.
 */
public class RedirectorHttpFilter extends HttpFiltersSourceAdapter {

    public RedirectorHttpFilter() {
        System.out.println("Started");
    }

    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        return new HttpFiltersAdapter(originalRequest) {


            @Override
            public HttpResponse proxyToServerRequest(HttpObject httpObject) {
                httpTohttpsRedirector(httpObject);
                return null;
            }

            @Override
            public HttpObject serverToProxyResponse(HttpObject httpObject) {
                // TODO: implement your filtering here
                return httpObject;
            }

            @Override
            public InetSocketAddress proxyToServerResolutionStarted(String resolvingServerHostAndPort) {
                return new InetSocketAddress(HostAndPort.fromString(resolvingServerHostAndPort).getHostText(), 80);
            }
        };
    }

    public boolean hasSamePageHTTPandHTTPS(String uriHttp) {

        String uriHttps = uriHttp.replace("http://", "https://");
        return getLengthURI(uriHttp) == getLengthURI(uriHttps) && getLengthURI(uriHttps) != 0;
    }


    //############################ REDIRECTOR ################################
    HttpRequest httpTohttpsRedirector(HttpObject httpObject) {
        if (httpObject instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) httpObject;
            if (ProxyUtils.isCONNECT(httpObject)) {

                if (getLengthURI("https://" + httpRequest.getUri()) == 0) {
                    System.out.println("to HTTP changed " + httpRequest.getUri());

//                    httpRequest.setUri(httpRequest.getUri().replace("https://", "http://"));
                }
            }
        }
        return null;
    }

    private static ConcurrentHashMap<String, Integer> cachedURLS = new ConcurrentHashMap<String, Integer>();

    public static long getLengthURI(String urlToRead) {
        Integer length;
        if ((length = cachedURLS.get(urlToRead)) != null) {
            return length;
        }

        Unirest.setTimeouts(100, 1000);
        try {
            length = Unirest.get(urlToRead).asString().getBody().length();
        } catch (UnirestException e) {
            System.out.println("Timeout " + urlToRead);
            //e.printStackTrace();
            length = 0;

        }
        cachedURLS.put(urlToRead, length);
        return length;
    }

    HttpResponse redirectRequest(String url, boolean toHTTPS) {

        System.out.println(url);
        if (toHTTPS) url = url.replace("http://", "https://");
        System.out.println(url);

        return redirectRequest(url);
    }

    HttpResponse redirectRequest(String url) {
        HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.MOVED_PERMANENTLY);
        HttpHeaders.setHeader(response, HttpHeaders.Names.LOCATION, url);

        return response;
    }

}