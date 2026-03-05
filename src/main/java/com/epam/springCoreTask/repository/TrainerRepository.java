package com.epam.springCoreTask.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.epam.springCoreTask.dto.AuthenticationDTO;
import com.epam.springCoreTask.model.Trainer;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByUser_Username(String username);

    List<Trainer> findByUser_UsernameIn(List<String> usernames);

    @Query("SELECT t FROM Trainer t WHERE t.user.username = :#{#auth.username} AND t.user.password = :#{#auth.password}")
    Optional<Trainer> findByUsernameAndPassword(@Param("auth") AuthenticationDTO auth);

    boolean existsByUser_Username(String username);

    @Query("SELECT t.user.username FROM Trainer t")
    List<String> findAllUsernames();

    @Query("SELECT t FROM Trainer t WHERE t NOT IN (SELECT tr FROM Trainee tn JOIN tn.trainers tr WHERE tn.user.username = :traineeUsername)")
    List<Trainer> findTrainersNotAssignedToTrainee(@Param("traineeUsername") String traineeUsername);
}
