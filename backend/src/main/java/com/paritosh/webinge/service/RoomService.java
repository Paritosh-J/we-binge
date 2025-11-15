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

    /**
     * Create a new binge room
     * @return returns an object of Room class
     */
    public Room createRoom() {
        String roomId = UUID.randomUUID().toString();
        Room room = new Room(roomId);

        rooms.put(roomId, room);

        return room;
    }

    /**
     * Fetches Room with passed roomId
     * @param roomId String representing the room id
     * @return returns a container object of Room class
     */
    public Optional<Room> getRoom(String roomId) {
        return Optional.ofNullable(rooms.get(roomId));
    }

    /**
     * Adds new member to a room with passed roomId and session
     * @param roomId String representing the room id
     * @param session WebSocket session
     */
    public void addMember(String roomId, WebSocketSession session) {

        Room room = rooms.computeIfAbsent(roomId, Room::new);
        Member member = new Member(session.getId(), session);
        room.addMember(member);

    }

    /**
     * Remove a chat member from room with passed roomId & sessionId
     * @param roomId String representing the room id
     * @param sessionId String representing the session id
     */
    public void removeMemberFromRoom(String roomId, String sessionId) {
        Room room = rooms.get(roomId);
        if (room != null) room.removeMember(sessionId);
    }

    /**
     * Get list of binge rooms
     * @return returns a collection of Rooms
     */
    public Collection<Room> listRooms() {
        return rooms.values();
    }

    /**
     * Delete a binge room
     * @param roomId String representing the room id
     */
    public void deleteRoom(String roomId) {
        rooms.remove(roomId);
    }

}
