package com.example.https;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLContext;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
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

    final KeyStore keyStore = load("/client-keystore.jks");
    final KeyStore trustStore = load("/client-truststore.jks");

    SSLContext sslContext = SSLContexts.custom()
        .setProtocol(SSLConnectionSocketFactory.TLS)
        .loadKeyMaterial(keyStore, "secret".toCharArray())
        .loadTrustMaterial(trustStore, TrustSelfSignedStrategy.INSTANCE).build();
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

  private KeyStore load(String path)
      throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
    KeyStore store = KeyStore.getInstance("jks");
    final InputStream resourceAsStream = getClass()
        .getResourceAsStream(path);
    store.load(resourceAsStream, "secret".toCharArray());
    return store;
  }
}
