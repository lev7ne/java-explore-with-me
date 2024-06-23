package ru.ewm.handler;

import jakarta.persistence.EntityListeners;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Builder
public class ApiError {
    private String reason;
    private String message;
    @CreatedDate
    private String timestamp;
}
