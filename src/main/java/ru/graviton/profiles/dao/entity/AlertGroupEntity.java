package ru.graviton.profiles.dao.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Правило оповещения
 */
@Entity
@Table(name = "alert_groups")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "uid", callSuper = false)
public class AlertGroupEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uid;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "group_name")
    private String groupName;


    @Column(name = "description")
    private String description;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name="alert_groups_rules",
            joinColumns={@JoinColumn(name="group_uid")},
            inverseJoinColumns={@JoinColumn(name="rule_uid")})
    @Builder.Default
    private Set<AlertRuleEntity> rules = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name="alert_groups_notifications",
            joinColumns={@JoinColumn(name="group_uid")},
            inverseJoinColumns={@JoinColumn(name="notification_uid")})
    private Set<NotificationRuleEntity> notificationRules;

    public Set<AlertRuleEntity> getRules() {
        if (rules == null) {
            rules = new HashSet<>();
        }
        return rules;
    }
}
