package com.paritosh.webinge.model;

import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@Data
public class Member {

    private final String sessionId;
    private final WebSocketSession session;

    public Member(String sessionId, WebSocketSession session) {
        this.sessionId = sessionId;
        this.session = session;
    }

}
