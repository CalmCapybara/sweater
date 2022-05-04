package com.example.sweater.Entity.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaptchaResponseDto {
    private boolean success;
    @JsonAlias("error-codes")
    private Set<String> erroCodes;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Set<String> getErroCodes() {
        return erroCodes;
    }

    public void setErroCodes(Set<String> erroCodes) {
        this.erroCodes = erroCodes;
    }
}
