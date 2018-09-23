package com.test.friendmanagement.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "requestor", "target"
})
public class TargetActionRequest {

    private String requestor;
    private String target;
}
