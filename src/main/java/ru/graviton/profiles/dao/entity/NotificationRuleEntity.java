package ru.graviton.profiles.dao.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import ru.graviton.profiles.dto.AbstractNotificationRule;
import ru.graviton.profiles.dto.NotificationType;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "notification_rules")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRuleEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uid;

    @Column(name = "notification_type")
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "notification_name")
    private String name;

    @Column(name = "notification_description")
    private String description;

    @Column(name = "initial_delay")
    private Integer initialDelay;

    @Column(name = "alert_delay")
    private Integer alertDelay;

    @Column(name = "fire_message_template")
    private String fireMessageTemplate;

    @Column(name = "resolve_message_template")
    private String resolveMessageTemplate;

    @Column(name = "notification_rule")
    @Type(value = JsonBinaryType.class)
    private AbstractNotificationRule notificationRule;

    @Column(name = "enabled")
    private boolean enabled;

    @ManyToMany(mappedBy = "notificationRules")
    private Set<AlertRuleEntity> alertRules;

    @ManyToMany(mappedBy = "notificationRules")
    private Set<AlertGroupEntity> alertGroups;

}
