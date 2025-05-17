package com.barraviera.login_auth_api.dto;

// record ja vem com os getter e setter automaticos e usamos pra criar DTOs
public record LoginRequestDTO(String email, String password) {
}
