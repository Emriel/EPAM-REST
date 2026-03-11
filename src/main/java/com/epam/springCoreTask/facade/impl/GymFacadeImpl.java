package com.epam.springCoreTask.facade.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.epam.springCoreTask.dto.request.TraineeRegistrationRequest;
import com.epam.springCoreTask.dto.request.TraineeUpdateRequest;
import com.epam.springCoreTask.dto.request.TrainerRegistrationRequest;
import com.epam.springCoreTask.dto.request.TrainerUpdateRequest;
import com.epam.springCoreTask.dto.request.TrainingRequest;
import com.epam.springCoreTask.dto.response.RegistrationResponse;
import com.epam.springCoreTask.dto.response.TraineeProfileResponse;
import com.epam.springCoreTask.dto.response.TrainerProfileResponse;
import com.epam.springCoreTask.dto.response.TrainerSummary;
import com.epam.springCoreTask.dto.response.TrainingResponse;
import com.epam.springCoreTask.facade.GymFacade;
import com.epam.springCoreTask.mapper.TraineeMapper;
import com.epam.springCoreTask.mapper.TrainerMapper;
import com.epam.springCoreTask.mapper.TrainingMapper;
import com.epam.springCoreTask.model.Trainee;
import com.epam.springCoreTask.model.Trainer;
import com.epam.springCoreTask.model.Training;
import com.epam.springCoreTask.model.TrainingType;
import com.epam.springCoreTask.model.User;
import com.epam.springCoreTask.repository.TrainingTypeRepository;
import com.epam.springCoreTask.service.TraineeService;
import com.epam.springCoreTask.service.TrainerService;
import com.epam.springCoreTask.service.TrainingService;
import com.epam.springCoreTask.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class GymFacadeImpl implements GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final UserService userService;
    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;
    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    public RegistrationResponse createTraineeProfile(TraineeRegistrationRequest request) {
        log.info("Creating trainee profile through facade: {} {}", request.getFirstName(), request.getLastName());
        
        Trainee trainee = traineeService.createTrainee(
            request.getFirstName(),
            request.getLastName(),
            request.getDateOfBirth(),
            request.getAddress()
        );
        
        return new RegistrationResponse(trainee.getUser().getUsername(), trainee.getUser().getPassword());
    }

    @Override
    public RegistrationResponse createTrainerProfile(TrainerRegistrationRequest request) {
        log.info("Creating trainer profile through facade: {} {}", request.getFirstName(), request.getLastName());
        
        Trainer trainer = trainerService.createTrainer(
            request.getFirstName(),
            request.getLastName(),
            request.getSpecialization()
        );
        
        return new RegistrationResponse(trainer.getUser().getUsername(), trainer.getUser().getPassword());
    }

    @Override
    public void createTrainingSession(TrainingRequest request) {
        log.info("Creating training session through facade: {}", request.getTrainingName());

        Trainee trainee = traineeService.getTraineeByUsername(request.getTraineeUsername());
        Trainer trainer = trainerService.getTrainerByUsername(request.getTrainerUsername());

        log.debug("Both trainee and trainer validated, creating training session");
        trainingService.createTraining(
            trainee.getId(),
            trainer.getId(),
            request.getTrainingName(),
            trainer.getSpecialization(),
            request.getTrainingDate(),
            request.getTrainingDuration()
        );
    }

    @Override
    public TraineeProfileResponse updateTraineeProfile(TraineeUpdateRequest request) {
        log.info("Updating trainee profile through facade: {}", request.getUsername());
        
        Trainee trainee = traineeService.getTraineeByUsername(request.getUsername());
        traineeMapper.updateTraineeFromRequest(request, trainee);
        
        Trainee updatedTrainee = traineeService.updateTrainee(trainee);
        return traineeMapper.toProfileResponse(updatedTrainee);
    }

    @Override
    public TrainerProfileResponse updateTrainerProfile(TrainerUpdateRequest request) {
        log.info("Updating trainer profile through facade: {}", request.getUsername());
        
        Trainer trainer = trainerService.getTrainerByUsername(request.getUsername());
        trainerMapper.updateTrainerFromRequest(request, trainer);
        
        TrainingType trainingType = trainingTypeRepository.findByName(request.getSpecialization())
                .orElseThrow(() -> new com.epam.springCoreTask.exception.EntityNotFoundException(
                        "Training type not found: " + request.getSpecialization()));
        trainer.setSpecialization(trainingType);
        
        Trainer updatedTrainer = trainerService.updateTrainer(trainer);
        return trainerMapper.toProfileResponse(updatedTrainer);
    }

    @Override
    public TraineeProfileResponse getTraineeByUsername(String username) {
        log.info("Fetching trainee by username through facade: {}", username);
        Trainee trainee = traineeService.getTraineeByUsername(username);
        return traineeMapper.toProfileResponse(trainee);
    }

    @Override
    public TrainerProfileResponse getTrainerByUsername(String username) {
        log.info("Fetching trainer by username through facade: {}", username);
        Trainer trainer = trainerService.getTrainerByUsername(username);
        return trainerMapper.toProfileResponse(trainer);
    }

    @Override
    public void activateTrainee(String username) {
        log.info("Activating trainee through facade: username={}", username);
        traineeService.activateTrainee(username);
    }

    @Override
    public void deactivateTrainee(String username) {
        log.info("Deactivating trainee through facade: username={}", username);
        traineeService.deactivateTrainee(username);
    }

    @Override
    public void activateTrainer(String username) {
        log.info("Activating trainer through facade: username={}", username);
        trainerService.activateTrainer(username);
    }

    @Override
    public void deactivateTrainer(String username) {
        log.info("Deactivating trainer through facade: username={}", username);
        trainerService.deactivateTrainer(username);
    }

    @Override
    public void deleteTraineeByUsername(String username) {
        log.info("Deleting trainee by username through facade: {}", username);
        traineeService.deleteTraineeByUsername(username);
    }

    @Override
    public List<TrainingResponse> getTraineeTrainingsWithCriteria(String traineeUsername, LocalDate fromDate,
            LocalDate toDate, String trainerName, String trainingTypeName) {
        log.info("Fetching trainee trainings with criteria through facade: traineeUsername={}", traineeUsername);
        
        List<Training> trainings = trainingService.getTraineeTrainingsWithCriteria(
            traineeUsername, fromDate, toDate, trainerName, trainingTypeName
        );
        
        return trainingMapper.toTrainingResponseList(trainings);
    }

    @Override
    public List<TrainingResponse> getTrainerTrainingsWithCriteria(String trainerUsername, LocalDate fromDate,
            LocalDate toDate, String traineeName) {
        log.info("Fetching trainer trainings with criteria through facade: trainerUsername={}", trainerUsername);
        
        List<Training> trainings = trainingService.getTrainerTrainingsWithCriteria(
            trainerUsername, fromDate, toDate, traineeName
        );
        
        return trainingMapper.toTrainingResponseList(trainings);
    }

    @Override
    public List<TrainerSummary> getTrainersNotAssignedToTrainee(String traineeUsername) {
        log.info("Fetching trainers not assigned to trainee through facade: traineeUsername={}", traineeUsername);
        
        List<Trainer> trainers = trainerService.getTrainersNotAssignedToTrainee(traineeUsername);
        return trainerMapper.toTrainerSummaryList(trainers);
    }

    @Override
    public List<TrainerSummary> updateTraineeTrainersList(String traineeUsername, List<String> trainerUsernames) {
        log.info("Updating trainee trainers list through facade: traineeUsername={}", traineeUsername);
        
        traineeService.updateTraineeTrainersList(traineeUsername, trainerUsernames);
        
        Trainee trainee = traineeService.getTraineeByUsername(traineeUsername);
        return trainerMapper.toTrainerSummaryList(trainee.getTrainers());
    }

    @Override
    public User authenticateUser(String username, String password) {
        log.info("Authenticating user through facade: username={}", username);
        return userService.authenticateUser(username, password);
    }

    @Override
    public void changeUserPassword(String username, String oldPassword, String newPassword) {
        log.info("Changing user password through facade: username={}", username);
        userService.changeUserPassword(username, oldPassword, newPassword);
    }

    @Override
    @Deprecated
    public Trainee getTraineeEntityByUsername(String username) {
        log.warn("Using deprecated method getTraineeEntityByUsername - controllers should use DTO methods");
        return traineeService.getTraineeByUsername(username);
    }

    @Override
    @Deprecated
    public Trainer getTrainerEntityByUsername(String username) {
        log.warn("Using deprecated method getTrainerEntityByUsername - controllers should use DTO methods");
        return trainerService.getTrainerByUsername(username);
    }
}
