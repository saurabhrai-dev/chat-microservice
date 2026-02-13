package com.chat.chat_microservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMessageRequest {

    @NotBlank(message = "Sender is required")
    @Pattern(regexp = "^(user|assistant)$", message = "Sender must be either 'user' or 'assistant'")
    private String sender;

    @NotBlank(message = "Content is required")
    private String content;

    private String context; // Optional RAG context
}