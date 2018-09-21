package com.test.friendmanagement.controller;

import com.test.friendmanagement.dto.FriendRequest;
import com.test.friendmanagement.dto.FriendResponse;
import com.test.friendmanagement.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/friend")
public class FriendController {

    private FriendService friendService;

    @Autowired
    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @PostMapping
    public FriendResponse friend(@RequestBody @Valid FriendRequest friendRequest) {
        boolean status = friendService.friend(friendRequest.getFriends());
        return FriendResponse.builder().success(status).build();
    }
}
