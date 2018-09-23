package com.test.friendmanagement.controller;

import com.test.friendmanagement.dto.*;
import com.test.friendmanagement.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/friend")
public class FriendController {

    private FriendService friendService;

    @Autowired
    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @RequestMapping
    public FriendResponse friend(@RequestBody @Valid FriendRequest friendRequest) {
        boolean status = friendService.friend(friendRequest.getFriends());
        return FriendResponse.builder().success(status).build();
    }

    @RequestMapping("/list")
    public FriendResponse friendList(@RequestBody @Valid FriendListRequest friendListRequest) {
        List<String> friends = friendService.getFriends(friendListRequest.getEmail());
        return FriendResponse.builder().success(true).friends(friends).count(friends.size()).build();
    }

    @RequestMapping("/list/common")
    public FriendResponse commonFriendList(@RequestBody @Valid FriendRequest friendRequest) {
        List<String> friends = friendService.getCommonFriends(friendRequest.getFriends());
        return FriendResponse.builder().success(true).friends(friends).count(friends.size()).build();
    }

    @RequestMapping("/subscribe")
    public FriendResponse subscribe(@RequestBody @Valid TargetActionRequest targetActionRequest) {
        boolean status = friendService.subscribeToUpdates(targetActionRequest.getRequestor(), targetActionRequest.getTarget());
        return FriendResponse.builder().success(status).build();
    }

    @RequestMapping("/block")
    public FriendResponse block(@RequestBody @Valid TargetActionRequest targetActionRequest) {
        boolean status = friendService.blockFromUpdates(targetActionRequest.getRequestor(), targetActionRequest.getTarget());
        return FriendResponse.builder().success(status).build();
    }

    @RequestMapping("/updates")
    public FriendResponse updates(@RequestBody @Valid SenderUpdateRequest senderUpdateRequest) {
        List<String> recipients = friendService.getUpdatesRecipients(senderUpdateRequest.getSender(), senderUpdateRequest.getText());
        return FriendResponse.builder().success(true).recipients(recipients).build();
    }

}
