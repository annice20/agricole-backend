package com.agriculture.backend.dto;

public class OtpRequest {
    private String email;
    private String codeOtp;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCodeOtp() { return codeOtp; }
    public void setCodeOtp(String codeOtp) { this.codeOtp = codeOtp; }
}