package com.paritosh.webinge.controller;

import com.paritosh.webinge.model.Room;
import com.paritosh.webinge.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/room")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<?> createRoom() {
        Room room = roomService.createRoom();
        return ResponseEntity.ok(Map.of(
                "roomId", room.getRoomId(),
                "roomUrl", "/r/" + room.getRoomId()
        ));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoom(@PathVariable String roomId) {
        return roomService.getRoom(roomId)
                .map(r -> ResponseEntity.ok(Map.of(
                        "roomId", r.getRoomId(),
                        "members", r.getMembers().keySet()
                )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
