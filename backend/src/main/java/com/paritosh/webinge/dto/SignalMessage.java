package com.paritosh.webinge.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class SignalMessage {

    private String type;
    private String from;
    private String to;
    private JsonNode payload;

}
