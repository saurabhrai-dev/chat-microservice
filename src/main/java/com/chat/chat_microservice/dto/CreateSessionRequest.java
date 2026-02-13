package com.chat.chat_microservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSessionRequest {

    @NotBlank(message = "User ID is mandatory")
    private String userId;

    @NotBlank(message = "Title is mandatory")
    private String title;
}
