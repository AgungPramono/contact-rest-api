package com.agung.restful.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginUserRequest {

    @NotBlank
    @Size(max = 100)
    @JsonProperty("username")
    private String userName;

    @NotBlank
    @Size(max = 100)
    private String password;
}
