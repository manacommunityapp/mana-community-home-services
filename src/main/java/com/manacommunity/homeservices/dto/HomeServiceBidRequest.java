package com.manacommunity.homeservices.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HomeServiceBidRequest {
    private String requestId;
    private String workerId;
    private String workerName;
    private String workerPhone;
    private BigDecimal proposedPrice;
    private String message;
}
