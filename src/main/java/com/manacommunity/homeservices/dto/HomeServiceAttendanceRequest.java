package com.manacommunity.homeservices.dto;

import com.manacommunity.homeservices.model.enums.HomeServiceAttendanceStatus;
import com.manacommunity.homeservices.model.enums.HomeServiceMarkedBy;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class HomeServiceAttendanceRequest {
    private String bookingId;
    private String workerId;
    private String residentUserId;
    private LocalDate serviceDate;
    private HomeServiceAttendanceStatus status;
    private LocalTime inTime;
    private LocalTime outTime;
    private HomeServiceMarkedBy markedBy;
    private String notes;
}
