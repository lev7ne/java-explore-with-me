package ru.practicum.ewm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "endpoint_hit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hit {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "app", nullable = false, length = 50)
    private String app;
    @Column(name = "uri", nullable = false, length = 200)
    private String uri;
    @Column(name = "ip", nullable = false, length = 50)
    private String ip;
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public Hit(String app, String uri, String ip, LocalDateTime timestamp) {
        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.timestamp = timestamp;
    }
}
