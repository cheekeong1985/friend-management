package com.test.friendmanagement.controller;

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

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FriendControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void friendRequest_success() {
        FriendRequest friendRequest = new FriendRequest(Arrays.asList("a@mail.com", "b@mail.com"));
        ResponseEntity<FriendResponse> responseResponseEntity =
                restTemplate.postForEntity("/friend", friendRequest, FriendResponse.class);
        assertThat(responseResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        FriendResponse friendResponse = responseResponseEntity.getBody();
        assertThat(friendResponse).isNotNull().hasFieldOrPropertyWithValue("success", true);
    }

    @Test
    public void friendRequest_error_sameEmails() {
        FriendRequest friendRequest = new FriendRequest(Arrays.asList("b@mail.com", "b@mail.com"));
    }
}
