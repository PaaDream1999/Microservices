package com.apisak.authservice.service;

import com.apisak.authservice.domain.User;
import com.apisak.authservice.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/** ช่วยกระจาย Event ภายใน Spring Context */
@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishUserCreated(User user) {
        publisher.publishEvent(new UserCreatedEvent(user.getId(), user.getUsername()));
    }
}