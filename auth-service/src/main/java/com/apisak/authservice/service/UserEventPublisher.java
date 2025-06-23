package com.apisak.authservice.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * ยิง Domain Event ต่าง ๆ เกี่ยวกับผู้ใช้
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class UserEventPublisher {

    private final ApplicationEventPublisher publisher;

    /** Event: ผู้ใช้สมัครสมาชิกสำเร็จ */
    @Getter
    public static class UserRegisteredEvent extends ApplicationEvent {
        private final Long userId;

        public UserRegisteredEvent(Object source, Long userId) {
            super(source);
            this.userId = userId;
        }
    }

    /** เรียกหลังบันทึกผู้ใช้ใหม่ เพื่อแจ้ง service อื่นให้สร้าง Profile */
    public void publishRegistered(Long userId) {
        publisher.publishEvent(new UserRegisteredEvent(this, userId));
    }
}