INSERT INTO alert_rules (uid, rule_name, description, enabled, rule_expression, severity, date_create,
                         date_update, alert_rule_type, is_prometheus_expression)
VALUES ('e3ffe4c7-85bb-4aec-9ba6-577f9365fb6c'::uuid, 'Повышение скорости вентиляторов',
        'Повышение скорости вентиляторов', true, 'fans>$me.upperThresholdCritical', 'CRITICAL',
        '2025-08-12 12:17:47.061757', '2025-08-14 07:00:17.363478', 'FANS', false),
       ('6130e6ca-005a-427f-a0e4-aee084f906af'::uuid, 'Превышение SWAP', 'Показатель SWAP выше 90%', true,
        '(((node_memory_SwapTotal_bytes - node_memory_SwapFree_bytes) / (node_memory_SwapTotal_bytes)) * 100)>90',
        'CRITICAL', '2025-08-14 08:50:35.721876', '2025-08-14 10:43:06.539452', 'OTHER', true),
       ('749e94c5-adc1-4e6e-b62f-e347c474a6ff'::uuid, 'Снижение температуры', 'Снижение температуры', true,
        'temperatures<$me.lowerThresholdCritical', 'CRITICAL', '2025-08-08 08:47:46.126505',
        '2025-08-12 12:10:40.593956', 'TEMPERATURE', false),
       ('3e3e2b77-5185-43d6-98e1-560732b0d4f6'::uuid, 'Снижение напряжения', 'Снижение напряжения', true,
        'voltages<$me.lowerThresholdCritical', 'CRITICAL', '2025-03-13 14:27:54.693722', '2025-08-12 12:10:50.965507',
        'VOLTAGE', NULL),
       ('ddbb2162-66b8-47c3-9932-ba49dcec89d1'::uuid, 'Снижение скорости вентиляторов',
        'Снижение скорости вентиляторов', true, 'fans<$me.lowerThresholdCritical', 'CRITICAL',
        '2025-03-13 14:27:07.996225', '2025-08-12 12:11:38.269993', 'FANS', false),
       ('32728a3f-2bfb-4f16-b35b-a277dbcfdb6e'::uuid, 'Повышение напряжения', 'Повышение напряжения', true,
        'voltages>$me.upperThresholdCritical', 'CRITICAL', '2025-08-12 12:14:36.502442', NULL, 'VOLTAGE', false),
       ('878d7a40-428a-4747-b1b1-e2ed93a58491'::uuid, 'Повышение температуры', 'Повышение температуры', true,
        'temperatures>$me.upperThresholdCritical', 'CRITICAL', '2025-08-12 12:16:37.482312', NULL, 'TEMPERATURE',
        false);

INSERT INTO alert_groups (uid, enabled, group_name, description, date_create, date_update)
VALUES ('4476a51b-59d7-4fe0-adc6-5796bf666eaa'::uuid, true, 'Снижение показателей здоровья',
        'Группа для веб-интерфейса', '2025-03-13 14:39:15.388731', '2025-07-03 07:28:00.760017'),
       ('25930478-eac5-4af3-b5d9-0430df9513c9'::uuid, true, 'Превышение показателей здоровья',
        'Группа для веб-интерфейса', '2025-08-14 07:38:34.016563', NULL),
       ('3aaf0bc4-fec3-471c-89aa-38b3aa0822df'::uuid, true, 'Превышение SWAP', 'Группа для веб-интерфейса',
        '2025-08-14 08:52:38.015225', '2025-08-14 08:53:19.81657');

INSERT INTO notification_rules (uid, notification_type, notification_name, notification_description,
                                notification_rule, fire_message_template, resolve_message_template, alert_delay,
                                date_create, date_update, enabled, initial_delay)
VALUES ('d3ef326a-dc4f-44d2-89a6-6c5e5aae3473'::uuid, 'UI', 'Превышение показателей здоровья',
        'Превышение показателей здоровья', '{"type": "UI"}', '‼️ ВНИМАНИЕ!
На следующих устройствах показатели здоровья превысили допустимое значение:
{#each by grouping labels.instanceName} Название устройства: <b>{{labels.instanceName}}</b>
Адрес устройства: <b>{{labels.targetUrl}}</b>
{#each by grouping alertRule} Правило:<b>{{alertRule.ruleName}}</b>
   {#each} {{labels.name}}:{{value}}/{{ruleValue}} отклонение на: $eval(abs({{value}}-{{ruleValue}})/{{ruleValue}}*100)%{/each by grouping}
   {/each by grouping}', '‼️ ВНИМАНИЕ!
На следующих устройствах показатели здоровья пришли в норму:
{#each by grouping labels.instanceName} Название устройства: <b>{{labels.instanceName}}</b>
Адрес устройства: <b>{{labels.targetUrl}}</b>
{#each by grouping alertRule} Правило:<b>{{alertRule.ruleName}}</b>
   {#each} {{labels.name}}:{{value}}/{{ruleValue}} отклонение на: $eval(abs({{value}}-{{ruleValue}})/{{ruleValue}}*100)%{/each by grouping}
   {/each by grouping}', 0, '2025-03-13 14:19:24.491997', '2025-08-14 08:59:39.021013', true, 20),
       ('161710fa-112e-49c9-b396-79ff2884568f'::uuid, 'UI', 'Снижение показателей здоровья',
        'Снижение показателей здоровья', '{"type": "UI"}', '‼️ ВНИМАНИЕ!
На следующих устройствах показатели здоровья ниже допустимого значения:
{#each by grouping labels.instanceName} Название устройства: <b>{{labels.instanceName}}</b>
Адрес устройства: <b>{{labels.targetUrl}}</b>
{#each by grouping alertRule} Правило:<b>{{alertRule.ruleName}}</b>
   {#each} {{labels.name}}:{{value}}/{{ruleValue}} отклонение на: $eval(abs({{value}}-{{ruleValue}})/{{ruleValue}}*100)%{/each by grouping}
   {/each by grouping}', '‼️ ВНИМАНИЕ!
На следующих устройствах показатели здоровья пришли в норму:
{#each by grouping labels.instanceName} Название устройства: <b>{{labels.instanceName}}</b>
Адрес устройства: <b>{{labels.targetUrl}}</b>
{#each by grouping alertRule} Правило:<b>{{alertRule.ruleName}}</b>
   {#each} {{labels.name}}:{{value}}/{{ruleValue}} отклонение на: $eval(abs({{value}}-{{ruleValue}})/{{ruleValue}}*100)%{/each by grouping}
   {/each by grouping}', 20, '2025-03-13 14:18:44.946929', '2025-08-14 08:59:28.718765', true, 20),
       ('684bee25-9fe6-4377-bdfb-f6dd592255b1'::uuid, 'UI', 'SWAP превысил 90%', 'SWAP превысил 90%',
        '{"type": "UI"}',
        '‼️ ВНИМАНИЕ! На следующих устройствах показатель SWAP превысил 90%: {#each by grouping labels.instanceName} Название устройства: <b>{{labels.instanceName}}</b> Адрес устройства: <b>{{labels.targetUrl}}</b> {#each by grouping alertRule} Правило:<b>{{alertRule.ruleName}}</b> {#each} {{labels.name}}:{{value}}/{{ruleValue}} отклонение на: $eval(abs({{value}}-{{ruleValue}})/{{ruleValue}}*100)%{/each by grouping} {/each by grouping}',
        '‼️ ВНИМАНИЕ! На следующих устройствах показатель SWAP пришел в норму: {#each by grouping labels.instanceName} Название устройства: <b>{{labels.instanceName}}</b> Адрес устройства: <b>{{labels.targetUrl}}</b> {#each by grouping alertRule} Правило:<b>{{alertRule.ruleName}}</b> {#each} {{labels.name}}:{{value}}/{{ruleValue}} отклонение на: $eval(abs({{value}}-{{ruleValue}})/{{ruleValue}}*100)%{/each by grouping} {/each by grouping}',
        0, '2025-08-14 08:52:10.481058', NULL, true, 20);

INSERT INTO alert_groups_notifications (group_uid, notification_uid)
VALUES ('4476a51b-59d7-4fe0-adc6-5796bf666eaa'::uuid, '161710fa-112e-49c9-b396-79ff2884568f'::uuid),
       ('25930478-eac5-4af3-b5d9-0430df9513c9'::uuid, 'd3ef326a-dc4f-44d2-89a6-6c5e5aae3473'::uuid),
       ('3aaf0bc4-fec3-471c-89aa-38b3aa0822df'::uuid, '684bee25-9fe6-4377-bdfb-f6dd592255b1'::uuid);

INSERT INTO alert_groups_rules (group_uid, rule_uid)
VALUES ('4476a51b-59d7-4fe0-adc6-5796bf666eaa'::uuid, '3e3e2b77-5185-43d6-98e1-560732b0d4f6'::uuid),
       ('4476a51b-59d7-4fe0-adc6-5796bf666eaa'::uuid, 'ddbb2162-66b8-47c3-9932-ba49dcec89d1'::uuid),
       ('4476a51b-59d7-4fe0-adc6-5796bf666eaa'::uuid, '749e94c5-adc1-4e6e-b62f-e347c474a6ff'::uuid),
       ('25930478-eac5-4af3-b5d9-0430df9513c9'::uuid, 'e3ffe4c7-85bb-4aec-9ba6-577f9365fb6c'::uuid),
       ('25930478-eac5-4af3-b5d9-0430df9513c9'::uuid, '878d7a40-428a-4747-b1b1-e2ed93a58491'::uuid),
       ('25930478-eac5-4af3-b5d9-0430df9513c9'::uuid, '32728a3f-2bfb-4f16-b35b-a277dbcfdb6e'::uuid),
       ('3aaf0bc4-fec3-471c-89aa-38b3aa0822df'::uuid, '6130e6ca-005a-427f-a0e4-aee084f906af'::uuid);

INSERT INTO alert_rules_notifications (rule_uid, notification_uid)
VALUES ('ddbb2162-66b8-47c3-9932-ba49dcec89d1'::uuid, '161710fa-112e-49c9-b396-79ff2884568f'::uuid),
       ('3e3e2b77-5185-43d6-98e1-560732b0d4f6'::uuid, '161710fa-112e-49c9-b396-79ff2884568f'::uuid);


