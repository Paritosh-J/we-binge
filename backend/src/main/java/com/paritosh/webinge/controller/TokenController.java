package com.paritosh.webinge.controller;

import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/token")
public class TokenController {

    @Value("${livekit.apiKey:}")
    private String livekitApiKey;

    @Value("${livekit.apiSecret:}")
    private String livekitApiSecret;


    @PostMapping("/livekit/{roomId}")
    public ResponseEntity<?> mintLiveKitToken(@PathVariable String roomId, @RequestParam(required = false) String identity) {
        // For security, implement proper signing with LiveKit SDK or your own JWT generation.
        // This is a placeholder demonstrating where you would return token data to the client.

        if (livekitApiKey.isBlank() || livekitApiSecret.isBlank()) {
            return ResponseEntity.status(501).body(Map.of("error", "LiveKit not configured on server"));
        }


        // TODO: use LiveKit server SDK to mint a token. For now, return a stub.
        return ResponseEntity.ok(Map.of("token", "TODO_GENERATE_TOKEN_ON_SERVER_SIDE"));
    }
}
