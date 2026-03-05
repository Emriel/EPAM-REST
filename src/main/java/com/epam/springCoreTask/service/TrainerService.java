package com.epam.springCoreTask.service;

import java.util.List;

import com.epam.springCoreTask.model.Trainer;

public interface TrainerService {
    Trainer createTrainer(String firstName, String lastName, String specialization);

    Trainer updateTrainer(Trainer trainer);

    Trainer getTrainerById(Long id);

    List<Trainer> getAllTrainers();

    Trainer authenticateTrainer(String username, String password);

    Trainer getTrainerByUsername(String username);

    void changeTrainerPassword(String username, String oldPassword, String newPassword);

    void activateTrainer(String username);

    void deactivateTrainer(String username);

    List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername);
}
