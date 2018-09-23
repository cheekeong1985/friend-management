package com.test.friendmanagement.service;

import com.test.friendmanagement.domain.*;
import com.test.friendmanagement.exception.RelationshipException;
import com.test.friendmanagement.exception.UserException;
import com.test.friendmanagement.repository.RelationshipRepository;
import com.test.friendmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.test.friendmanagement.domain.QUsers.users;

@Service
public class FriendService {

    private UserRepository userRepository;
    private RelationshipRepository relationshipRepository;

    @Autowired
    public FriendService(UserRepository userRepository,
                         RelationshipRepository relationshipRepository) {
        this.userRepository = userRepository;
        this.relationshipRepository = relationshipRepository;
    }

    public boolean friend(List<String> emails) {
        if (emails.get(0).equals(emails.get(1))) {
            throw new UserException("Same email addresses");
        }
        List<Users> newUsers = emails.stream().filter(email ->
                !userRepository.exists(users.email.eq(email))).map(Users::new).collect(Collectors.toList());
        userRepository.saveAll(newUsers);

        Relationship relationship1 = createRelationship(emails.get(0), emails.get(1));
        Relationship relationship2 = createRelationship(emails.get(1), emails.get(0));

        relationshipRepository.save(relationship1);
        relationshipRepository.save(relationship2);
        return true;
    }

    public List<String> getFriends(String email) {
        if (!userRepository.exists(users.email.eq(email))) {
            throw new UserException("User not found");
        }

        return relationshipRepository.getFriendList(email);
    }

    public List<String> getCommonFriends(List<String> emails) {
        if (emails.get(0).equals(emails.get(1))) {
            throw new UserException("Same email addresses");
        }
        emails.stream().filter(email -> !userRepository.exists(users.email.eq(email)))
                .findAny().ifPresent(s -> {
            throw new UserException("User not found");
        });
        return relationshipRepository.getCommonFriendList(emails);
    }

    private Relationship createRelationship(String userEmail, String friendEmail) {
        // Users story 5 - cannot create new relationship if user is blocked by friend
        if (isBlocked(friendEmail, userEmail)) {
            throw new RelationshipException("Blocked by friend - cannot create relationship");
        }
        QRelationship qRelationship = QRelationship.relationship;
        RelationshipId relationshipId = new RelationshipId(userEmail, friendEmail);
        Optional<Relationship> relationshipResult = relationshipRepository.findOne(
                qRelationship.relationshipId.eq(relationshipId));
        Relationship relationship;
        if (relationshipResult.isPresent()) { // possibly subscribed to update but not friends yet
            relationship = relationshipResult.get();
            if (relationship.isFriend()) {
                throw new RelationshipException("Already friends. Nothing to update.");
            }
            relationship.setFriend(true);
        } else {
            relationship = new Relationship(relationshipId, true, false, false);
        }
        return relationship;
    }

    private boolean isBlocked(String source, String target) {
        QRelationship qRelationship = QRelationship.relationship;
        RelationshipId relationshipId = new RelationshipId(source, target);
        Optional<Relationship> relationship = relationshipRepository.findOne(
                qRelationship.relationshipId.eq(relationshipId));
        return relationship.map(Relationship::isBlocked).orElse(false);
    }
}
