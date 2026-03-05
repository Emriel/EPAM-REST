package com.epam.springCoreTask.facade.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.epam.springCoreTask.facade.GymFacade;
import com.epam.springCoreTask.model.Trainee;
import com.epam.springCoreTask.model.Trainer;
import com.epam.springCoreTask.model.Training;
import com.epam.springCoreTask.model.TrainingType;
import com.epam.springCoreTask.service.TraineeService;
import com.epam.springCoreTask.service.TrainerService;
import com.epam.springCoreTask.service.TrainingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class GymFacadeImpl implements GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public Trainee createTraineeProfile(String firstName, String lastName,
            LocalDate dateOfBirth, String address) {
        log.info("Creating trainee profile through facade: {} {}", firstName, lastName);
        return traineeService.createTrainee(firstName, lastName, dateOfBirth, address);
    }

    public Trainer createTrainerProfile(String firstName, String lastName,
            String specialization) {
        log.info("Creating trainer profile through facade: {} {}", firstName, lastName);
        return trainerService.createTrainer(firstName, lastName, specialization);
    }

    public Training createTrainingSession(Long traineeId, Long trainerId,
            String trainingName, TrainingType trainingType,
            LocalDate trainingDate, int trainingDuration) {
        log.info("Creating training session through facade: {}", trainingName);

        Trainee trainee = traineeService.getTraineeById(traineeId);
        if (trainee == null) {
            log.error("Trainee not found with id: {}", traineeId);
            throw new IllegalArgumentException("Trainee not found with id: " + traineeId);
        }

        Trainer trainer = trainerService.getTrainerById(trainerId);
        if (trainer == null) {
            log.error("Trainer not found with id: {}", trainerId);
            throw new IllegalArgumentException("Trainer not found with id: " + trainerId);
        }

        log.debug("Both trainee and trainer validated, creating training session");
        return trainingService.createTraining(traineeId, trainerId, trainingName,
                trainingType, trainingDate, trainingDuration);
    }

    public List<Training> getTraineeTrainings(Long traineeId) {
        log.info("Fetching all trainings for trainee: {}", traineeId);

        List<Training> allTrainings = trainingService.getAllTrainings();
        return allTrainings.stream()
                .filter(training -> training.getTrainee() != null &&
                        training.getTrainee().getId().equals(traineeId))
                .collect(Collectors.toList());
    }

    public List<Training> getTrainerTrainings(Long trainerId) {
        log.info("Fetching all trainings for trainer: {}", trainerId);

        List<Training> allTrainings = trainingService.getAllTrainings();
        return allTrainings.stream()
                .filter(training -> training.getTrainer() != null &&
                        training.getTrainer().getId().equals(trainerId))
                .collect(Collectors.toList());
    }

    public Trainee updateTraineeProfile(Trainee trainee) {
        log.info("Updating trainee profile through facade: {}", trainee.getId());
        return traineeService.updateTrainee(trainee);
    }

    public Trainer updateTrainerProfile(Trainer trainer) {
        log.info("Updating trainer profile through facade: {}", trainer.getId());
        return trainerService.updateTrainer(trainer);
    }

    public void deleteTraineeProfile(Long traineeId) {
        log.info("Deleting trainee profile and associated trainings: {}", traineeId);

        Trainee trainee = traineeService.getTraineeById(traineeId);
        if (trainee == null) {
            log.warn("Trainee not found for deletion: {}", traineeId);
            return;
        }

        traineeService.deleteTrainee(trainee);
        log.info("Trainee profile deleted successfully: {}", traineeId);
    }

    public Trainee getTraineeById(Long id) {
        return traineeService.getTraineeById(id);
    }

    public Trainer getTrainerById(Long id) {
        return trainerService.getTrainerById(id);
    }

    public Training getTrainingById(Long id) {
        return trainingService.getTrainingById(id);
    }

    public List<Trainee> getAllTrainees() {
        return traineeService.getAllTrainees();
    }

    public List<Trainer> getAllTrainers() {
        return trainerService.getAllTrainers();
    }

    public List<Training> getAllTrainings() {
        return trainingService.getAllTrainings();
    }

    public Trainee authenticateTrainee(String username, String password) {
        log.info("Authenticating trainee through facade: username={}", username);
        return traineeService.authenticateTrainee(username, password);
    }

    public Trainer authenticateTrainer(String username, String password) {
        log.info("Authenticating trainer through facade: username={}", username);
        return trainerService.authenticateTrainer(username, password);
    }

    public Trainee getTraineeByUsername(String username) {
        log.info("Fetching trainee by username through facade: {}", username);
        return traineeService.getTraineeByUsername(username);
    }

    public Trainer getTrainerByUsername(String username) {
        log.info("Fetching trainer by username through facade: {}", username);
        return trainerService.getTrainerByUsername(username);
    }

    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        log.info("Changing trainee password through facade: username={}", username);
        traineeService.changeTraineePassword(username, oldPassword, newPassword);
    }

    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        log.info("Changing trainer password through facade: username={}", username);
        trainerService.changeTrainerPassword(username, oldPassword, newPassword);
    }

    public void activateTrainee(String username) {
        log.info("Activating trainee through facade: username={}", username);
        traineeService.activateTrainee(username);
    }

    public void deactivateTrainee(String username) {
        log.info("Deactivating trainee through facade: username={}", username);
        traineeService.deactivateTrainee(username);
    }

    public void activateTrainer(String username) {
        log.info("Activating trainer through facade: username={}", username);
        trainerService.activateTrainer(username);
    }

    public void deactivateTrainer(String username) {
        log.info("Deactivating trainer through facade: username={}", username);
        trainerService.deactivateTrainer(username);
    }

    public void deleteTraineeByUsername(String username) {
        log.info("Deleting trainee by username through facade: {}", username);
        traineeService.deleteTraineeByUsername(username);
    }

    public List<Training> getTraineeTrainingsWithCriteria(String traineeUsername, LocalDate fromDate,
            LocalDate toDate, String trainerName, String trainingTypeName) {
        log.info("Fetching trainee trainings with criteria through facade: traineeUsername={}", traineeUsername);
        return trainingService.getTraineeTrainingsWithCriteria(traineeUsername, fromDate, toDate, trainerName,
                trainingTypeName);
    }

    public List<Training> getTrainerTrainingsWithCriteria(String trainerUsername, LocalDate fromDate,
            LocalDate toDate, String traineeName) {
        log.info("Fetching trainer trainings with criteria through facade: trainerUsername={}", trainerUsername);
        return trainingService.getTrainerTrainingsWithCriteria(trainerUsername, fromDate, toDate, traineeName);
    }

    public List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername) {
        log.info("Fetching trainers not assigned to trainee through facade: traineeUsername={}", traineeUsername);
        return trainerService.getTrainersNotAssignedToTrainee(traineeUsername);
    }

    public void updateTraineeTrainersList(String traineeUsername, List<String> trainerUsernames) {
        log.info("Updating trainee trainers list through facade: traineeUsername={}", traineeUsername);
        traineeService.updateTraineeTrainersList(traineeUsername, trainerUsernames);
    }
}
