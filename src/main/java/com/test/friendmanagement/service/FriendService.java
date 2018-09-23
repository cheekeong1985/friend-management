package com.test.friendmanagement.service;

import com.test.friendmanagement.domain.Relationship;
import com.test.friendmanagement.domain.RelationshipId;
import com.test.friendmanagement.domain.Users;
import com.test.friendmanagement.exception.RelationshipException;
import com.test.friendmanagement.exception.UserException;
import com.test.friendmanagement.repository.RelationshipRepository;
import com.test.friendmanagement.repository.UserRepository;
import org.nibor.autolink.LinkExtractor;
import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.test.friendmanagement.domain.QRelationship.relationship;
import static com.test.friendmanagement.domain.QUsers.users;

@Service
public class FriendService {

    private static final String SAME_EMAIL_ADDRESSES = "Same email addresses";
    private static final String USER_NOT_FOUND       = "User not found";
    private static final String ALREADY_SUBSCRIBED   = "Already subscribed";
    private static final String ALREADY_BLOCKED      = "Already blocked";
    private static final String ALREADY_FRIEND       = "Already friend";
    private UserRepository userRepository;
    private RelationshipRepository relationshipRepository;

    @Autowired
    public FriendService(UserRepository userRepository,
                         RelationshipRepository relationshipRepository) {
        this.userRepository = userRepository;
        this.relationshipRepository = relationshipRepository;
    }

    public boolean makeFriend(List<String> emails) {
        if (emails.get(0).equals(emails.get(1))) {
            throw new UserException(SAME_EMAIL_ADDRESSES);
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
            throw new UserException(USER_NOT_FOUND);
        }

        return relationshipRepository.getFriendList(email);
    }

    public List<String> getCommonFriends(List<String> emails) {
        if (emails.get(0).equals(emails.get(1))) {
            throw new UserException(SAME_EMAIL_ADDRESSES);
        }
        emails.stream().filter(email -> !userRepository.exists(users.email.eq(email)))
                .findAny().ifPresent(s -> {
            throw new UserException(USER_NOT_FOUND);
        });
        return relationshipRepository.getCommonFriendList(emails);
    }

    public boolean subscribeToUpdates(String requestorEmail, String targetEmail) {
        if (requestorEmail.equals(targetEmail)) {
            throw new UserException(SAME_EMAIL_ADDRESSES);
        }
        if (!userRepository.exists(users.email.eq(requestorEmail))
                || !userRepository.exists(users.email.eq(targetEmail))) {
            throw new UserException(USER_NOT_FOUND);
        }
        RelationshipId relationshipId = new RelationshipId(requestorEmail, targetEmail);
        Optional<Relationship> relationshipResult = relationshipRepository.findOne(
                relationship.relationshipId.eq(relationshipId));
        Relationship relationship;
        if (relationshipResult.isPresent()) {
            relationship = relationshipResult.get();
            if (relationship.isSubscribed()) {
                throw new RelationshipException(ALREADY_SUBSCRIBED);
            }
            relationship.setSubscribed(true);
        } else {
            relationship = new Relationship(relationshipId, false, false, true);
        }
        relationshipRepository.save(relationship);
        return true;
    }

    public boolean blockFromUpdates(String requestorEmail, String targetEmail) {
        if (requestorEmail.equals(targetEmail)) {
            throw new UserException(SAME_EMAIL_ADDRESSES);
        }
        if (!userRepository.exists(users.email.eq(requestorEmail))
                || !userRepository.exists(users.email.eq(targetEmail))) {
            throw new UserException(USER_NOT_FOUND);
        }
        RelationshipId relationshipId = new RelationshipId(requestorEmail, targetEmail);
        Optional<Relationship> relationshipResult = relationshipRepository.findOne(
                relationship.relationshipId.eq(relationshipId));
        Relationship relationship;
        if (relationshipResult.isPresent()) {
            relationship = relationshipResult.get();
            if (relationship.isBlocked()) {
                throw new RelationshipException(ALREADY_BLOCKED);
            }
            relationship.setBlocked(true);
        } else {
            relationship = new Relationship(relationshipId, false, true, false);
        }
        relationshipRepository.save(relationship);
        return true;
    }

    public List<String> getUpdatesRecipients(String senderEmail, String text) {
        if (!userRepository.exists(users.email.eq(senderEmail))) {
            throw new UserException(USER_NOT_FOUND);
        }
        List<String> mentionedEmails = extractEmailFromText(text);
        List<String> recipients = relationshipRepository.getUpdatesRecipients(senderEmail);
        recipients.addAll(mentionedEmails);

        return recipients;
    }

    private Relationship createRelationship(String userEmail, String friendEmail) {
        // Users story 5 - cannot create new relationship if user is blocked by friend
        if (isBlocked(friendEmail, userEmail)) {
            throw new RelationshipException(ALREADY_BLOCKED);
        }
        RelationshipId relationshipId = new RelationshipId(userEmail, friendEmail);
        Optional<Relationship> relationshipResult = relationshipRepository.findOne(
                relationship.relationshipId.eq(relationshipId));
        Relationship relationship;
        if (relationshipResult.isPresent()) { // possibly subscribed to update but not friends yet
            relationship = relationshipResult.get();
            if (relationship.isFriend()) {
                throw new RelationshipException(ALREADY_FRIEND);
            }
            relationship.setFriend(true);
        } else {
            relationship = new Relationship(relationshipId, true, false, false);
        }
        return relationship;
    }

    private boolean isBlocked(String source, String target) {
        RelationshipId relationshipId = new RelationshipId(source, target);
        Optional<Relationship> relationshipResult = relationshipRepository.findOne(
                relationship.relationshipId.eq(relationshipId));
        return relationshipResult.map(Relationship::isBlocked).orElse(false);
    }

    private List<String> extractEmailFromText(String input) {
        LinkExtractor linkExtractor = LinkExtractor.builder()
                .linkTypes(EnumSet.of(LinkType.EMAIL))
                .build();
        List<String> emails = new ArrayList<>();
        Iterable<LinkSpan> emailLinkSpans = linkExtractor.extractLinks(input);
        for (LinkSpan emailLinkSpan : emailLinkSpans) {
            String emailLink = input.substring(emailLinkSpan.getBeginIndex(), emailLinkSpan.getEndIndex());
            emails.add(emailLink);
        }
        return emails;
    }
}
