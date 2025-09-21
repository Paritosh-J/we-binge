package com.paritosh.webinge.controller;

import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/turn")
public class TurnController {

    @Value("turn.static-auth-secret")
    private String staticAuthSecret;

    @Value("turn.urls")
    private String turnUrls;

    @Value("turn.ttl.seconds")
    private long ttlSeconds;

    @GetMapping
    public ResponseEntity<?> getTurnCredentials(@RequestParam(required = false) String userName) {

        if (staticAuthSecret == null || staticAuthSecret.isBlank()) {
            return ResponseEntity.status(501).body(Map.of(
                    "error", "TURN not configured on server"
            ));
        }

        try {
            long expiry = Instant.now().getEpochSecond() + ttlSeconds;

            if (userName == null || userName.isBlank())
                userName = "user-" + UUID.randomUUID().toString().substring(0, 8);

            String user = expiry + ":" + userName;
            String credential = generateHmacBase64(user, staticAuthSecret);

            String[] urls = turnUrls == null || turnUrls.isBlank() ?
                    new String[0] :
                    Arrays.stream(turnUrls.split(","))
                            .map(String::trim).toArray(String[]::new);

            Map<String, Object> server = new HashMap<>();
            server.put("urls", urls);
            server.put("username", user);
            server.put("credential", credential);

            return ResponseEntity.ok(Map.of(
                    "ttl", ttlSeconds,
                    "iceServers", List.of(server)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "failed to generate TURN credentials",
                    "message", e.getMessage()
            ));
        }

    }

    private String generateHmacBase64(String data, String secret) throws Exception {

        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec ketSpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA1");

        mac.init(ketSpec);

        byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(raw);

    }

}
