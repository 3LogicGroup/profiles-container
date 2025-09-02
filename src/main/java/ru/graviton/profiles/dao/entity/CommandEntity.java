package ru.graviton.profiles.dao.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import ru.graviton.profiles.dto.ActionDto;
import ru.graviton.profiles.dto.CommandType;

import java.util.UUID;

@Entity
@Table(name = "commands")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommandEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uid;

    @Column(name = "command_name")
    private String commandName;

    @Column(name = "description")
    private String description;
    
    @Column(name = "description_rus")
    private String descriptionRus;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "body")
    @Type(value = JsonBinaryType.class)
    private ActionDto body;

    @Column(name = "profile_uid")
    private UUID profileUid;

    @Column(name = "command_type")
    @Enumerated(EnumType.STRING)
    private CommandType commandType;

    @Column(name = "target")
    private String target;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "rus_name")
    private String rusName;

}

