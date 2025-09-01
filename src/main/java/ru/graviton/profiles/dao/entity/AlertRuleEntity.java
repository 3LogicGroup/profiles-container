package ru.graviton.profiles.dao.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.graviton.profiles.dto.AlertRuleType;
import ru.graviton.profiles.dto.Severity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Описание правила оповещения
 */
@Entity
@Table(name = "alert_rules")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "uid", callSuper = false)
public class AlertRuleEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uid;

    @Column(name = "rule_name")
    private String ruleName;

    @Column(name = "description")
    private String description;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "rule_expression")
    private String expression;

    @Column(name = "is_prometheus_expression")
    private Boolean isPrometheusExpression;

    @Column(name = "severity")
    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Column(name = "alert_rule_type")
    @Enumerated(EnumType.STRING)
    private AlertRuleType alertRuleType;

    @ManyToMany(mappedBy = "rules")
    private Set<AlertGroupEntity> alertGroups;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "alert_rules_notifications",
            joinColumns = {@JoinColumn(name = "rule_uid")},
            inverseJoinColumns = {@JoinColumn(name = "notification_uid")})
    private Set<NotificationRuleEntity> notificationRules;


    public Set<AlertGroupEntity> getAlertGroups() {
        if (alertGroups == null) {
            alertGroups = new HashSet<>();
        }
        return alertGroups;
    }
}
