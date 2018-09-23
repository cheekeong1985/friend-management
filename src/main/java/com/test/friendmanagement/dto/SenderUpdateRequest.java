package com.test.friendmanagement.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "sender", "text"
})
public class SenderUpdateRequest {
    @Email
    @NotEmpty
    private String sender;
    @NotEmpty
    private String text;
}
