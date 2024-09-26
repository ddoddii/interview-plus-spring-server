package com.ddoddii.resume.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserCheckVerificationCodeDTO {
    private String email;
    private String verificationCode;
}
