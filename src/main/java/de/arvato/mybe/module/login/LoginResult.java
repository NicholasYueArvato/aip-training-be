package de.arvato.mybe.module.login;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResult
{
    String jwttoken;
    String status;
    String errorCode;
}
