package ru.graviton.profiles.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.graviton.profiles.dao.entity.CommandEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommandRepository extends JpaRepository<CommandEntity, UUID> {


    @Query("select c from CommandEntity c where c.profileUid=:profileUid and c.isActive")
    List<CommandEntity> findAllCommandsByProfileUid(@Param("profileUid") UUID profileUid);

    @Query("select c from CommandEntity c where c.profileUid=:profileUid and c.commandName=:commandName and c.commandType=:commandType")
    List<CommandEntity> findAllCommandsByProfileAndCommandName(@Param("profileUid") UUID profileUid,
                                                               @Param("commandName") String commandName,
                                                               @Param("commandType") String commandType);

}
