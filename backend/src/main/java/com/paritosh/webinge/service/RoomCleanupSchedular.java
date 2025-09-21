package com.paritosh.webinge.service;

import com.paritosh.webinge.model.Room;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RoomCleanupSchedular {

    private final RoomService roomService;

    private long roomTtlSeconds = 86400;

    public RoomCleanupSchedular(RoomService roomService) {
        this.roomService = roomService;
    }

    @Scheduled(fixedDelayString = "600000")
    public void cleanup() {
        var oldRooms = roomService.listRooms().stream()
                .filter(r -> {
                    var created = r.getCreatedAt();
                    return created.plusSeconds(roomTtlSeconds).isBefore(Instant.now());
                })
                .map(Room::getRoomId)
                .toList();

        if(!oldRooms.isEmpty()) {
            oldRooms.forEach(id -> {
                roomService.deleteRoom(id);
                log.info("Deleted expired room {}", id);
            });
        }
    }

}
