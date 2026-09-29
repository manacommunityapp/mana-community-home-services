package com.manacommunity.homeservices.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeServiceJobActionRequest {
    private String otp;
    private String beforePhotoUrl;
    private String afterPhotoUrl;
    private String notes;
    private Integer rating;
    private String reviewText;
}
