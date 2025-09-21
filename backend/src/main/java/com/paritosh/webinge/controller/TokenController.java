package com.paritosh.webinge.controller;

import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/token")
public class TokenController {

    @Value("livekit.apiKey")
    private String livekitApiKey;

    @Value("livekit.apiSecret")
    private String livekitApiSecret;

    @Value("livekit.ttl")
    private long livekitTtl;


    @PostMapping("/livekit/{roomId}")
    public ResponseEntity<?> mintLiveKitToken(@PathVariable String roomId, @RequestParam(required = false) String identity) {

        if (livekitApiKey == null || livekitApiKey.isBlank() || livekitApiSecret == null || livekitApiSecret.isBlank()) {
            return ResponseEntity.status(501).body(Map.of("error", "LiveKit not configured on server"));
        }

        if (identity == null || identity.isBlank())
            identity = "anon-" + UUID.randomUUID().toString().substring(0, 8);

        try {
            AccessToken token = new AccessToken(livekitApiKey, livekitApiSecret);
            String jwt = token.toJwt();

            token.setIdentity(identity);
            token.setName(identity);
            token.addGrants(new RoomJoin(true), new RoomName(roomId));
            token.setTtl(livekitTtl);

            return ResponseEntity.ok(Map.of(
                    "token", jwt,
                    "identity", identity,
                    "roomId", roomId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "failed too generate token",
                    "message", e.getMessage()
            ));
        }

    }
}
