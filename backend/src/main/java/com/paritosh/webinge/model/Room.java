package com.paritosh.webinge.model;

import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Room {

    private final String roomId;
    private final Map<String, Member> members = new ConcurrentHashMap<>();
    private final Instant createdAt = Instant.now();

    public Room(String roomId) {
        this.roomId = roomId;
    }

    public void addMember(Member member) {
        members.put(member.getSessionId(), member);
    }

    public void removeMember(String sessionId) {
        members.remove(sessionId);
    }

}
