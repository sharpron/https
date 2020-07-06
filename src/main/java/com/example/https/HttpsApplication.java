package com.example.https;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class HttpsApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(HttpsApplication.class, args);
  }

  @GetMapping
  public String hello() {
    return "Hello, World!";
  }

  @Override
  public void run(String... args) throws Exception {

    KeyStore trustStore = KeyStore.getInstance("jks");
    final InputStream resourceAsStream = getClass()
        .getResourceAsStream("/keystore.jks");
    trustStore.load(resourceAsStream, "123456".toCharArray());
    SSLContext sslContext = SSLContexts.custom()
        .setProtocol(SSLConnectionSocketFactory.TLS)
        .loadTrustMaterial(trustStore, new TrustAllStrategy()).build();
    SSLConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext,
        NoopHostnameVerifier.INSTANCE);

    HttpClientBuilder hcb = HttpClientBuilder.create()
        .setSSLSocketFactory(sslSF);

    try (final CloseableHttpClient client = hcb.build()) {
      final HttpGet httpGet = new HttpGet("https://localhost:8080");
      final CloseableHttpResponse execute = client.execute(httpGet);
      final HttpEntity entity = execute.getEntity();

      final String content = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
      System.out.println(content);
    }
  }
}
