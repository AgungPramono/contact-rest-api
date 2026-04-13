package com.agung.restful.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Token {

    private String token;

    private Long expiredAt;
}
