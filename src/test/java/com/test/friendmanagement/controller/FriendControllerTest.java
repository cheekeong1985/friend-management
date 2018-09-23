package com.test.friendmanagement.controller;

import com.test.friendmanagement.dto.FriendListRequest;
import com.test.friendmanagement.dto.FriendRequest;
import com.test.friendmanagement.dto.FriendResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FriendControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void friendRequest() {
        ResponseEntity<FriendResponse> responseEntity = makeFriends(Arrays.asList("a@email.com", "b@email.com"));
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        FriendResponse friendResponse = responseEntity.getBody();
        assertThat(friendResponse).isNotNull().hasFieldOrPropertyWithValue("success", true);
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
    public void getCommonFriendList() {
        makeFriends(Arrays.asList("a@email.com", "b@email.com"));
        makeFriends(Arrays.asList("a@email.com", "c@email.com"));
        makeFriends(Arrays.asList("d@email.com", "b@email.com"));
        makeFriends(Arrays.asList("d@email.com", "c@email.com"));
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

    private ResponseEntity<FriendResponse> makeFriends(List<String> emails) {
        FriendRequest friendRequest = new FriendRequest(emails);
        ResponseEntity<FriendResponse> responseEntity =
                restTemplate.postForEntity("/friend", friendRequest, FriendResponse.class);
        return responseEntity;
    }
}
