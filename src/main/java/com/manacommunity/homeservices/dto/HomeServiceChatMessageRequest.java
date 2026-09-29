package com.manacommunity.homeservices.dto;

import com.manacommunity.homeservices.model.enums.HomeServiceSenderType;
import lombok.Data;

@Data
public class HomeServiceChatMessageRequest {
    private String bookingId;
    private String workerId;
    private String residentUserId;
    private HomeServiceSenderType senderType;
    private String senderName;
    private String messageText;
}
