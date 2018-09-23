package com.test.friendmanagement.repository;

import java.util.List;

public interface RelationshipRepositoryCustom {

    List<String> getFriendList(String email);

    List<String> getCommonFriendList(List<String> emails);

    List<String> getUpdatesRecipients(String email);
}
