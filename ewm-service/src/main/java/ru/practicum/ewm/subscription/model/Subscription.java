package ru.practicum.ewm.subscription.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "subscription")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "subscribed_user_id")
    private Long subscribedUserId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "mutual_subscription")
    @Builder.Default
    private boolean mutualSubscription = false;
}
