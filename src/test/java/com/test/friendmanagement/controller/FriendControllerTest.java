package com.test.friendmanagement.controller;

import com.test.friendmanagement.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FriendControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MessageSource messageSource;

    @Test
    public void friendRequest() {
        ResponseEntity<FriendResponse> responseEntity = makeFriends(Arrays.asList("a@email.com", "b@email.com"));
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        FriendResponse friendResponse = responseEntity.getBody();
        assertThat(friendResponse).isNotNull().hasFieldOrPropertyWithValue("success", true);
    }

    @Test
    public void friendRequest_sameEmails() {
        ResponseEntity<FriendResponse> responseEntity = makeFriends(Arrays.asList("a@email.com", "a@email.com"));
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        FriendResponse friendResponse = responseEntity.getBody();
        assertThat(friendResponse).isNotNull()
                .hasFieldOrPropertyWithValue("success", false)
                .hasFieldOrPropertyWithValue("errorMessage", messageSource.getMessage("same.email.addresses", null, Locale.getDefault()));
    }

    @Test
    public void friendRequest_sameFriend() {
        friendRequest();
        ResponseEntity<FriendResponse> responseEntity = makeFriends(Arrays.asList("a@email.com", "b@email.com"));
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        FriendResponse friendResponse = responseEntity.getBody();
        assertThat(friendResponse).isNotNull()
                .hasFieldOrPropertyWithValue("success", false)
                .hasFieldOrPropertyWithValue("errorMessage", messageSource.getMessage("already.friend", null, Locale.getDefault()));
    }

    @Test
    public void getFriendList() {
        makeFriends(Arrays.asList("a@email.com", "b@email.com"));
        makeFriends(Arrays.asList("a@email.com", "c@email.com"));
        makeFriends(Arrays.asList("a@email.com", "d@email.com"));
        List<String> expectedFriends = Arrays.asList("b@email.com", "c@email.com", "d@email.com");
        FriendListRequest friendListRequest = new FriendListRequest("a@email.com");
        ResponseEntity<FriendResponse> responseEntity =
                restTemplate.postForEntity("/friend/list", friendListRequest, FriendResponse.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        FriendResponse friendResponse = responseEntity.getBody();
        assertThat(friendResponse).isNotNull()
                .hasFieldOrPropertyWithValue("success", true)
                .hasFieldOrPropertyWithValue("friends", expectedFriends)
                .hasFieldOrPropertyWithValue("count", 3);
    }

    @Test
    public void getFriendList_UserNotFound() {
        makeFriends(Arrays.asList("a@email.com", "b@email.com"));
        makeFriends(Arrays.asList("a@email.com", "c@email.com"));
        FriendListRequest friendListRequest = new FriendListRequest("d@email.com");
        ResponseEntity<FriendResponse> responseEntity =
                restTemplate.postForEntity("/friend/list", friendListRequest, FriendResponse.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        FriendResponse friendResponse = responseEntity.getBody();
        assertThat(friendResponse).isNotNull()
                .hasFieldOrPropertyWithValue("success", false)
                .hasFieldOrPropertyWithValue("errorMessage", messageSource.getMessage("user.not.found", null, Locale.getDefault()));
    }

    @Test
    public void getCommonFriendList() {
        makeFriends(Arrays.asList("a@email.com", "b@email.com"));
        makeFriends(Arrays.asList("a@email.com", "c@email.com"));
        makeFriends(Arrays.asList("a@email.com", "x@email.com"));
        makeFriends(Arrays.asList("d@email.com", "b@email.com"));
        makeFriends(Arrays.asList("d@email.com", "c@email.com"));
        makeFriends(Arrays.asList("d@email.com", "z@email.com"));
        List<String> expectedCommonFriends = Arrays.asList("b@email.com", "c@email.com");
        FriendRequest friendRequest = new FriendRequest(Arrays.asList("a@email.com", "d@email.com"));
        ResponseEntity<FriendResponse> responseEntity =
                restTemplate.postForEntity("/friend/list/common", friendRequest, FriendResponse.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        FriendResponse friendResponse = responseEntity.getBody();
        assertThat(friendResponse).isNotNull()
                .hasFieldOrPropertyWithValue("success", true)
                .hasFieldOrPropertyWithValue("friends", expectedCommonFriends)
                .hasFieldOrPropertyWithValue("count", 2);
    }

    @Test
    public void subscribeForUpdate() {
        makeFriends(Arrays.asList("a@email.com", "b@email.com"));
        makeFriends(Arrays.asList("c@email.com", "d@email.com"));
        ResponseEntity<FriendResponse> subscribeResponseEntity = subscribe("a@email.com", "c@email.com");
        assertThat(subscribeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void subscribeForUpdate_sameSubscribe() {
        subscribeForUpdate();
        ResponseEntity<FriendResponse> subscribeResponseEntity = subscribe("a@email.com", "c@email.com");
        assertThat(subscribeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        FriendResponse friendResponse = subscribeResponseEntity.getBody();
        assertThat(friendResponse).isNotNull()
                .hasFieldOrPropertyWithValue("success", false)
                .hasFieldOrPropertyWithValue("errorMessage", messageSource.getMessage("already.subscribed", null, Locale.getDefault()));
    }

    @Test
    public void blockFromUpdate() {
        makeFriends(Arrays.asList("a@email.com", "b@email.com"));
        makeFriends(Arrays.asList("c@email.com", "d@email.com"));
        ResponseEntity<FriendResponse> responseEntity = blockFriend("a@email.com", "c@email.com");
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void blockFromUpdate_sameBlock() {
        blockFromUpdate();
        ResponseEntity<FriendResponse> responseEntity = blockFriend("a@email.com", "c@email.com");
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        FriendResponse friendResponse = responseEntity.getBody();
        assertThat(friendResponse).isNotNull()
                .hasFieldOrPropertyWithValue("success", false)
                .hasFieldOrPropertyWithValue("errorMessage", messageSource.getMessage("already.blocked", null, Locale.getDefault()));
    }

    @Test
    public void friendRequest_blocked() {
        makeFriends(Arrays.asList("a@email.com", "b@email.com"));
        makeFriends(Arrays.asList("c@email.com", "d@email.com"));
        ResponseEntity<FriendResponse> blockResponseEntity = blockFriend("c@email.com", "a@email.com");
        assertThat(blockResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        // if they are not connected as friends, then no new friends connection can be added
        ResponseEntity<FriendResponse> friendResponseEntity = makeFriends(Arrays.asList("a@email.com", "c@email.com"));
        assertThat(friendResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        FriendResponse friendResponse = friendResponseEntity.getBody();
        assertThat(friendResponse).isNotNull()
                .hasFieldOrPropertyWithValue("success", false)
                .hasFieldOrPropertyWithValue("errorMessage", messageSource.getMessage("already.blocked", null, Locale.getDefault()));
    }

    @Test
    public void senderUpdate_isFriend_isSubscribed() {
        makeFriends(Arrays.asList("a@email.com", "b@email.com"));
        makeFriends(Arrays.asList("c@email.com", "d@email.com"));
        ResponseEntity<FriendResponse> subscribeResponseEntity = subscribe("c@email.com", "a@email.com");
        assertThat(subscribeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<String> expectedRecipients = Arrays.asList("b@email.com", "c@email.com", "e@email.com");
        ResponseEntity<FriendResponse> responseEntity = sendUpdate("a@email.com", "Hello World! e@email.com");
        FriendResponse friendResponse = responseEntity.getBody();
        assertThat(friendResponse).isNotNull()
                .hasFieldOrPropertyWithValue("success", true)
                .hasFieldOrPropertyWithValue("recipients", expectedRecipients);
    }

    @Test
    public void senderUpdate_isFriend_isBlocked() {
        makeFriends(Arrays.asList("a@email.com", "b@email.com"));
        makeFriends(Arrays.asList("c@email.com", "d@email.com"));
        ResponseEntity<FriendResponse> blockResponseEntity = blockFriend("b@email.com", "a@email.com");
        assertThat(blockResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<String> expectedRecipients = Arrays.asList("e@email.com");
        ResponseEntity<FriendResponse> sendUpdateResponseEntity = sendUpdate("a@email.com", "Hello World! e@email.com");
        assertThat(sendUpdateResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        FriendResponse friendResponse = sendUpdateResponseEntity.getBody();
        assertThat(friendResponse).isNotNull()
                .hasFieldOrPropertyWithValue("success", true)
                .hasFieldOrPropertyWithValue("recipients", expectedRecipients);
    }


    private ResponseEntity<FriendResponse> makeFriends(List<String> emails) {
        FriendRequest friendRequest = new FriendRequest(emails);
        return restTemplate.postForEntity("/friend", friendRequest, FriendResponse.class);
    }

    private ResponseEntity<FriendResponse> subscribe(String requestorEmail, String targetEmail) {
        TargetActionRequest targetActionRequest = new TargetActionRequest(requestorEmail, targetEmail);
        return restTemplate.postForEntity("/friend/subscribe", targetActionRequest, FriendResponse.class);
    }

    private ResponseEntity<FriendResponse> blockFriend(String requestorEmail, String targetEmail) {
        TargetActionRequest targetActionRequest = new TargetActionRequest(requestorEmail, targetEmail);
        return restTemplate.postForEntity("/friend/block", targetActionRequest, FriendResponse.class);
    }

    private ResponseEntity<FriendResponse> sendUpdate(String sender, String text) {
        SenderUpdateRequest senderUpdateRequest = new SenderUpdateRequest(sender, text);
        return restTemplate.postForEntity("/friend/updates", senderUpdateRequest, FriendResponse.class);
    }
}
