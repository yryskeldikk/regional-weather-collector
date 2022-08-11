package com.siemens.mo.ogcio.data.regionalweather;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;

@Configuration
@EnableAutoConfiguration
@EnableAsync
@ComponentScan(basePackages={"com.siemens.mo.ogcio.data.regionalweather"})
@EntityScan(basePackages = {"com.siemens.mo.ogcio.data.regionalweather.bean"})
public class ApplicationConfig {
    @Value("${proxy.use:false}")
    private boolean proxyUse;
    @Value("${proxy.http:}")
    private String proxyHttp;
    @Value("${proxy.http.port:0}")
    private String proxyHttpPort;

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }
    @Resource
    @Lazy
    private  RestTemplateBuilder restTemplateBuilder;

    @Bean
    public RestTemplate restTemplate()
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout( 10 * 1000 )
                .setSocketTimeout( 15 * 1000 )
                .setConnectTimeout( 15 * 1000 )
                .build();

        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setDefaultRequestConfig( requestConfig )
                .setSSLSocketFactory(csf);

        if (proxyUse) {
            httpClientBuilder = httpClientBuilder.setProxy(
                    new HttpHost(proxyHttp, Integer.parseInt(proxyHttpPort), "http")
            );
        }

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient( httpClientBuilder.build() );

        requestFactory.setConnectTimeout(15 * 1000);
        requestFactory.setReadTimeout(15 * 1000);

        RestTemplate restTemplate = restTemplateBuilder
                .requestFactory( () -> requestFactory )
                .setConnectTimeout(Duration.ofMillis(3000))
                .setReadTimeout(Duration.ofMillis(3000))
                .additionalMessageConverters(
                        new Jaxb2RootElementHttpMessageConverter(),
                        new MappingJackson2HttpMessageConverter(),
                        new StringHttpMessageConverter()
                ).build();

        return restTemplate;
    }
}
