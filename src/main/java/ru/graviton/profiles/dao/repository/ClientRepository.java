package ru.graviton.profiles.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.graviton.profiles.dao.entity.ClientEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {

    Optional<ClientEntity> findByEmail(String email);

}
