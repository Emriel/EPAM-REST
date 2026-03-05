package com.epam.springCoreTask.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.epam.springCoreTask.dto.AuthenticationDTO;
import com.epam.springCoreTask.model.Trainee;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    Optional<Trainee> findByUser_Username(String username);

    List<Trainee> findByUser_UsernameIn(List<String> usernames);

    @Query("SELECT t FROM Trainee t WHERE t.user.username = :#{#auth.username} AND t.user.password = :#{#auth.password}")
    Optional<Trainee> findByUsernameAndPassword(@Param("auth") AuthenticationDTO auth);

    boolean existsByUser_Username(String username);

    @Query("SELECT t.user.username FROM Trainee t")
    List<String> findAllUsernames();
}
