package com.apisak.authservice.event;

import java.util.UUID;

/**
 * ถูก publish หลังบันทึก User สำเร็จ
 * subject:  userId & username  (ใช้สร้าง Profile ฝั่ง user-service)
 */
public record UserCreatedEvent(UUID userId, String username) {}
