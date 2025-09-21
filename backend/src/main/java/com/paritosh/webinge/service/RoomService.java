package com.paritosh.webinge.service;

import com.paritosh.webinge.model.Member;
import com.paritosh.webinge.model.Room;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public Room createRoom() {

        String roomId = UUID.randomUUID().toString();
        Room room = new Room(roomId);

        rooms.put(roomId, room);

        return room;
    }

    public Optional<Room> getRoom(String roomId) {
        return Optional.ofNullable(rooms.get(roomId));
    }

    public void addMember(String roomId, WebSocketSession session) {

        Room room = rooms.computeIfAbsent(roomId, Room::new);
        Member member = new Member(session.getId(), session);
        room.addMember(member);

    }

    public void removeMemberFromRoom(String roomId, String sessionId) {
        Room room = rooms.get(roomId);
        if (room != null) room.removeMember(sessionId);
    }

    public Collection<Room> listRooms() {
        return rooms.values();
    }

}
