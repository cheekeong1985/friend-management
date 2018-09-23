package com.test.friendmanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "success", "friends", "count", "recipients", "errorMessage"
})
public class FriendResponse {

    private Boolean success;
    private List<String> friends;
    private Integer count;
    private List<String> recipients;
    private String errorMessage;
}
