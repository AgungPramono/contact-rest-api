package com.agung.restful.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponse {

    private String token;

    private String refreshToken;

    @JsonIgnore
    private Long expiredAt;

    @JsonProperty("expiredAt")
    private String formatStringExpireAt;

    @JsonIgnore
    private Long refreshTokenExpired;

    @JsonProperty("refreshTokenExpiredAt")
    private String formatStringRefreshTokenExpireAt;
}
