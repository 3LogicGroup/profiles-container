package ru.graviton.profiles.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.graviton.profiles.dao.entity.AlertGroupEntity;
import ru.graviton.profiles.dao.entity.AlertRuleEntity;
import ru.graviton.profiles.dao.entity.NotificationRuleEntity;
import ru.graviton.profiles.dao.repository.AlertGroupRepository;
import ru.graviton.profiles.dao.repository.AlertRuleRepository;
import ru.graviton.profiles.dao.repository.NotificationRuleRepository;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AlertSqlService {
    private final ObjectMapper objectMapper;
    private final AlertRuleRepository alertRuleRepository;
    private final AlertGroupRepository alertGroupRepository;
    private final NotificationRuleRepository notificationRuleRepository;

    @Transactional
    public String generateSql() {
        List<NotificationRuleEntity> notifications = notificationRuleRepository.findActiveNotificationRulesByUid();
        List<AlertRuleEntity> alertRules = alertRuleRepository.getActiveAlertRules();
        List<AlertGroupEntity> alertGroups = alertGroupRepository.getActiveAlertGroups();
        StringBuilder sb = new StringBuilder();
        sb.append("""
                DROP TABLE IF EXISTS public.alert_rules_temp;
                CREATE TABLE public.alert_rules_temp (
                uid uuid NOT NULL,
                rule_name varchar(200) NOT NULL,
                description varchar(1000) NULL,
                enabled bool NOT NULL,
                rule_expression varchar(4000) NOT NULL,
                severity varchar(100) NOT NULL,
                date_create timestamp NOT NULL,
                date_update timestamp NULL,
                alert_rule_type varchar(50) NULL,
                is_prometheus_expression bool NULL,
                is_master_rule bool NULL,
                CONSTRAINT "alertRuleTempPK" PRIMARY KEY (uid)
                );
                """);
        sb.append("""
                DROP TABLE IF EXISTS public.alert_groups_temp;
                CREATE TABLE public.alert_groups_temp (
                uid uuid NOT NULL,
                enabled bool NOT NULL,
                group_name varchar(200) NOT NULL,
                description varchar(1000) NULL,
                date_create timestamp NOT NULL,
                date_update timestamp NULL,
                CONSTRAINT "alertGroupTempPK" PRIMARY KEY (uid)
                );
                """);
        sb.append(
                """
                DROP TABLE IF EXISTS public.notification_rules_temp;
                CREATE TABLE public.notification_rules_temp (
                uid uuid NOT NULL,
                notification_type varchar(100) NOT NULL,
                notification_name varchar(255) NOT NULL,
                notification_description varchar(1000) NOT NULL,
                notification_rule jsonb NOT NULL,
                fire_message_template varchar(8000) NOT NULL,
                resolve_message_template varchar(8000) NOT NULL,
                alert_delay int4 NOT NULL,
                date_create timestamp NOT NULL,
                date_update timestamp NULL,
                enabled bool NOT NULL,
                initial_delay int4 NULL,
                CONSTRAINT "notificationRuleTempPK" PRIMARY KEY (uid)
                );
                """);
        sb.append("""
                DROP TABLE IF EXISTS public.alert_groups_notifications_temp;
                CREATE TABLE public.alert_groups_notifications_temp (
                group_uid uuid NOT NULL,
                notification_uid uuid NOT NULL,
                CONSTRAINT alert_groups_notifications_temp_pkey PRIMARY KEY (notification_uid, group_uid)
                );
                """);
        sb.append("""
                DROP TABLE IF EXISTS public.alert_groups_rules_temp;
                CREATE TABLE public.alert_groups_rules_temp (
                group_uid uuid NOT NULL,
                rule_uid uuid NOT NULL,
                CONSTRAINT alert_groups_rules_temp_pkey PRIMARY KEY (rule_uid, group_uid)
                );
                """);
        sb.append("""
                DROP TABLE IF EXISTS public.alert_rules_notifications_temp;
                CREATE TABLE public.alert_rules_notifications_temp (
                rule_uid uuid NOT NULL,
                notification_uid uuid NOT NULL,
                CONSTRAINT alert_rules_notifications_temp_pkey PRIMARY KEY (notification_uid, rule_uid)
                );
                """);
        for (NotificationRuleEntity n : notifications) {
            String json = toJson(n.getNotificationRule());

            sb.append("INSERT INTO notification_rules_temp (uid, notification_type, notification_name, notification_description, ")
                    .append("notification_rule, fire_message_template, resolve_message_template, alert_delay, ")
                    .append("date_create, date_update, enabled, initial_delay) VALUES (")
                    .append("'").append(n.getUid()).append("', ")
                    .append("'").append(n.getType().name()).append("', ")
                    .append("'").append(esc(n.getName())).append("', ")
                    .append("'").append(esc(n.getDescription())).append("', ")
                    .append("'").append(esc(json)).append("', ")
                    .append("'").append(esc(n.getFireMessageTemplate())).append("', ")
                    .append("'").append(esc(n.getResolveMessageTemplate())).append("', ")
                    .append(n.getAlertDelay()).append(", ")
                    .append("'").append(n.getDateCreate()).append("', ")
                    .append(n.getDateUpdate() != null ? "'" + n.getDateUpdate() + "'" : "NULL").append(", ")
                    .append(n.isEnabled()).append(", ")
                    .append(n.getInitialDelay() != null ? n.getInitialDelay() : "NULL")
                    .append(") ON CONFLICT (uid) DO UPDATE SET ")
                    .append("notification_type = EXCLUDED.notification_type, ")
                    .append("notification_name = EXCLUDED.notification_name, ")
                    .append("notification_description = EXCLUDED.notification_description, ")
                    .append("notification_rule = EXCLUDED.notification_rule, ")
                    .append("fire_message_template = EXCLUDED.fire_message_template, ")
                    .append("resolve_message_template = EXCLUDED.resolve_message_template, ")
                    .append("alert_delay = EXCLUDED.alert_delay, ")
                    .append("date_update = EXCLUDED.date_update, ")
                    .append("enabled = EXCLUDED.enabled, ")
                    .append("initial_delay = EXCLUDED.initial_delay;\n\n");
        }

        for (AlertRuleEntity r : alertRules) {

            sb.append("INSERT INTO alert_rules_temp (uid, rule_name, description, enabled, rule_expression, severity, ")
                    .append("date_create, date_update, alert_rule_type, is_prometheus_expression, is_master_rule) VALUES (")
                    .append("'").append(r.getUid()).append("', ")
                    .append("'").append(esc(r.getRuleName())).append("', ")
                    .append(r.getDescription() != null ? "'" + esc(r.getDescription()) + "'" : "NULL").append(", ")
                    .append(r.isEnabled()).append(", ")
                    .append("'").append(esc(r.getExpression())).append("', ")
                    .append("'").append(r.getSeverity().name()).append("', ")
                    .append("'").append(r.getDateCreate()).append("', ")
                    .append(r.getDateUpdate() != null ? "'" + r.getDateUpdate() + "'" : "NULL").append(", ")
                    .append(r.getAlertRuleType() != null ? "'" + r.getAlertRuleType().name() + "'" : "NULL").append(", ")
                    .append(r.getIsPrometheusExpression() != null ? r.getIsPrometheusExpression() : "NULL").append(", ")
                    .append(r.getIsMasterRule() != null ? r.getIsMasterRule() : "NULL")
                    .append(") ON CONFLICT (uid) DO UPDATE SET ")
                    .append("rule_name = EXCLUDED.rule_name, ")
                    .append("description = EXCLUDED.description, ")
                    .append("enabled = EXCLUDED.enabled, ")
                    .append("rule_expression = EXCLUDED.rule_expression, ")
                    .append("severity = EXCLUDED.severity, ")
                    .append("date_update = EXCLUDED.date_update, ")
                    .append("alert_rule_type = EXCLUDED.alert_rule_type, ")
                    .append("is_prometheus_expression = EXCLUDED.is_prometheus_expression, ")
                    .append("is_master_rule = EXCLUDED.is_master_rule;\n\n");
        }

        for (AlertGroupEntity g : alertGroups) {

            sb.append("INSERT INTO alert_groups_temp (uid, enabled, group_name, description, date_create, date_update) VALUES (")
                    .append("'").append(g.getUid()).append("', ")
                    .append(g.isEnabled()).append(", ")
                    .append("'").append(esc(g.getGroupName())).append("', ")
                    .append(g.getDescription() != null ? "'" + esc(g.getDescription()) + "'" : "NULL").append(", ")
                    .append("'").append(g.getDateCreate()).append("', ")
                    .append(g.getDateUpdate() != null ? "'" + g.getDateUpdate() + "'" : "NULL")
                    .append(") ON CONFLICT (uid) DO UPDATE SET ")
                    .append("enabled = EXCLUDED.enabled, ")
                    .append("group_name = EXCLUDED.group_name, ")
                    .append("description = EXCLUDED.description, ")
                    .append("date_update = EXCLUDED.date_update;\n\n");
        }

        for (AlertRuleEntity r : alertRules) {
            for (NotificationRuleEntity n : safe(r.getNotificationRules())) {
                sb.append("INSERT INTO alert_rules_notifications_temp (rule_uid, notification_uid) VALUES ('")
                        .append(r.getUid()).append("','")
                        .append(n.getUid())
                        .append("') ON CONFLICT DO NOTHING;\n");
            }
        }

        for (AlertGroupEntity g : alertGroups) {

            for (AlertRuleEntity r : safe(g.getRules())) {
                sb.append("INSERT INTO alert_groups_rules_temp (group_uid, rule_uid) VALUES ('")
                        .append(g.getUid()).append("','")
                        .append(r.getUid())
                        .append("') ON CONFLICT DO NOTHING;\n");
            }

            for (NotificationRuleEntity n : safe(g.getNotificationRules())) {
                sb.append("INSERT INTO alert_groups_notifications_temp (group_uid, notification_uid) VALUES ('")
                        .append(g.getUid()).append("','")
                        .append(n.getUid())
                        .append("') ON CONFLICT DO NOTHING;\n");
            }
        }

        return sb.toString();
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String esc(String s) {
        if (s == null) return null;
        return s.replace("'", "''");
    }

    private <T> Set<T> safe(Set<T> set) {
        return set == null ? Set.of() : set;
    }
}