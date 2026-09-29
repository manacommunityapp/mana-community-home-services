package com.manacommunity.homeservices.dto;

import com.manacommunity.homeservices.model.enums.HomeServiceScanType;
import lombok.Data;

@Data
public class HomeServiceGatePassScanRequest {
    private String gatePassId;
    private String workerId;
    private HomeServiceScanType scanType;
    private String gateName;
    private String guardUserId;
    private String guardName;
}
