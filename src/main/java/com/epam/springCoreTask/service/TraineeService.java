package com.epam.springCoreTask.service;

import java.util.List;

import com.epam.springCoreTask.model.Trainee;

public interface TraineeService {

    Trainee createTrainee(String firstName, String lastName, java.time.LocalDate dateOfBirth, String address);

    Trainee updateTrainee(Trainee trainee);

    void deleteTrainee(Trainee trainee);

    Trainee getTraineeById(Long id);

    List<Trainee> getAllTrainees();

    Trainee authenticateTrainee(String username, String password);

    Trainee getTraineeByUsername(String username);

    void changeTraineePassword(String username, String oldPassword, String newPassword);

    void activateTrainee(String username);

    void deactivateTrainee(String username);

    void deleteTraineeByUsername(String username);

    void updateTraineeTrainersList(String traineeUsername, List<String> trainerUsernames);
}
