package com.mineCryptos.service.ServiceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mineCryptos.model.entitities.enduser.BinanceWithdrawResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BinanceClient {
    @Value("${app.binance.apiKey}")
    private String apiKey;


    @Value("${app.binance.secretKey}")
    private String secretKey;


    @Value("${app.binance.baseUrl}")
    private String baseUrl;


    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();



    private String sign(String payload) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

//    public BinanceWithdrawResponseDto withdrawBNB(String address, String network, String amount) throws Exception {
//        String path = "/sapi/v1/capital/withdraw/apply";
//
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("coin", "BNB");
//        params.add("address", address);
//        params.add("network", network); // BSC
//        params.add("amount", amount);
//
//
//        String query = "";
//        for (String key : params.keySet()) {
//            if (!query.isEmpty()) query += "&";
//            query += URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(params.getFirst(key), "UTF-8");
//        }
//
//
//        long timestamp = System.currentTimeMillis();
//        query += "&timestamp=" + timestamp;
//        String signature = sign(query);
//        String url = baseUrl + path + "?" + query + "&signature=" + signature;
//
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
//        headers.set("X-MBX-APIKEY", apiKey);
//
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//        ResponseEntity<String> resp = rest.exchange(url, HttpMethod.POST, entity, String.class);
//
//
//        if (resp.getStatusCode() == HttpStatus.OK || resp.getStatusCode() == HttpStatus.CREATED) {
//            return objectMapper.readValue(resp.getBody(), BinanceWithdrawResponseDto.class);
//        } else {
//            throw new RuntimeException("Binance withdraw failed: " + resp.getBody());
//        }
//    }

    public BinanceWithdrawResponseDto withdraw(String coin, String address, String network, String amount) throws Exception {

        String timestamp = String.valueOf(System.currentTimeMillis());

        Map<String, String> params = new HashMap<>();
        params.put("coin", coin);
        params.put("address", address);
        params.put("network", network);
        params.put("amount", amount);
        params.put("timestamp", timestamp);

        // convert map → query string
        String query = buildQuery(params);

        // generate signature using SECRET KEY
        String signature = hmacSHA256(query, secretKey);

        WebClient client = WebClient.builder()
                .baseUrl("https://api.binance.com")
                .defaultHeader("X-MBX-APIKEY", apiKey) // API KEY here
                .build();

        return client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/sapi/v1/capital/withdraw/apply")
                        .query(query + "&signature=" + signature)
                        .build())
                .retrieve()
                .bodyToMono(BinanceWithdrawResponseDto.class)
                .block();
    }


    private String buildQuery(Map<String, String> params) {
        return params.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
    }

    private String hmacSHA256(String payload, String secretKey) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(payload.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


}
