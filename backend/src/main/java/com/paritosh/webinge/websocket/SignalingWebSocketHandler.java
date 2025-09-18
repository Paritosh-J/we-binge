package com.paritosh.webinge.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paritosh.webinge.dto.SignalMessage;
import com.paritosh.webinge.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;


@Component
@Slf4j
public class SignalingWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RoomService roomService;


    public SignalingWebSocketHandler(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = extractRoomId(session.getUri());

        log.info("WS connected - sessionId={}, roomId={}", session.getId(), roomId);

        roomService.addMember(roomId, session);

        var joinMssg = Map.of(
                "type", "member-joined",
                "from", session.getId()
        );

        broadcastToRoom(roomId, new TextMessage(objectMapper.writeValueAsString(joinMssg)), session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        SignalMessage msg = objectMapper.readValue(payload, SignalMessage.class);
        String roomId = extractRoomId(session.getUri());


        // Attach sender id if not present
        if (msg.getFrom() == null || msg.getFrom().isEmpty()) {
            msg.setFrom(session.getId());
        }


        if (msg.getTo() != null && !msg.getTo().isEmpty()) {
            // direct message to single participant
            var roomOpt = roomService.getRoom(roomId);
            if (roomOpt.isPresent()) {
                var participant = roomOpt.get().getMembers().get(msg.getTo());
                if (participant != null) {
                    sendJson(participant.getSession(), msg);
                } else {
                    log.warn("Target participant not found: {} in room {}", msg.getTo(), roomId);
                }
            }
        } else {
            // broadcast to everyone except sender
            broadcastToRoom(roomId, new TextMessage(objectMapper.writeValueAsString(msg)), session.getId());
        }
    }

    private void sendJson(WebSocketSession session, Object obj) throws IOException {
        String json = objectMapper.writeValueAsString(obj);
        session.sendMessage(new TextMessage(json));
    }


    private void broadcastToRoom(String roomId, TextMessage message, String excludeSessionId) {
        var roomOpt = roomService.getRoom(roomId);
        if (roomOpt.isEmpty()) return;
        var participants = roomOpt.get().getMembers();
        participants.forEach((sid, part) -> {
            try {
                if (!sid.equals(excludeSessionId) && part.getSession().isOpen()) {
                    part.getSession().sendMessage(message);
                }
            } catch (IOException e) {
                log.warn("Failed to send ws message to {}", sid, e);
            }
        });
    }


    private String extractRoomId(URI uri) {
        if (uri == null) return "";
        String path = uri.getPath(); // e.g. /ws/room/1234
        String[] segs = path.split("/");
        if (segs.length >= 3) {
            return segs[segs.length - 1];
        }
        return "";
    }

}
