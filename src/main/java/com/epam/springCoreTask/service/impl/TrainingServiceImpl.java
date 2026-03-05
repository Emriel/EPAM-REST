package com.epam.springCoreTask.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.springCoreTask.dto.TraineeTrainingCriteriaDTO;
import com.epam.springCoreTask.dto.TrainerTrainingCriteriaDTO;
import com.epam.springCoreTask.exception.EntityNotFoundException;
import com.epam.springCoreTask.exception.TraineeNotAvailableException;
import com.epam.springCoreTask.exception.TrainerNotAssignedException;
import com.epam.springCoreTask.exception.TrainerNotAvailableException;
import com.epam.springCoreTask.exception.ValidationException;
import com.epam.springCoreTask.model.Trainee;
import com.epam.springCoreTask.model.Trainer;
import com.epam.springCoreTask.model.Training;
import com.epam.springCoreTask.model.TrainingType;
import com.epam.springCoreTask.repository.TraineeRepository;
import com.epam.springCoreTask.repository.TrainerRepository;
import com.epam.springCoreTask.repository.TrainingRepository;
import com.epam.springCoreTask.repository.TrainingTypeRepository;
import com.epam.springCoreTask.service.TrainingService;
import com.epam.springCoreTask.util.ValidationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TrainingServiceImpl implements TrainingService {

        private final TrainingRepository trainingRepository;
        private final TraineeRepository traineeRepository;
        private final TrainerRepository trainerRepository;
        private final TrainingTypeRepository trainingTypeRepository;
        private final ValidationUtil validationUtil;

        @Override
        public Training createTraining(Long traineeId, Long trainerId, String trainingName,
                        TrainingType trainingType, LocalDate trainingDate, int trainingDuration) {
                log.debug("Creating training: name={}, traineeId={}, trainerId={}, date={}, duration={}",
                                trainingName, traineeId, trainerId, trainingDate, trainingDuration);

                validationUtil.validateNotNull(traineeId, "Trainee ID");
                validationUtil.validateNotNull(trainerId, "Trainer ID");
                validationUtil.validateNotBlank(trainingName, "Training name");
                validationUtil.validateNotNull(trainingType, "Training type");
                validationUtil.validateNotNull(trainingType.getId(), "Training type ID");

                Trainee trainee = traineeRepository.findById(traineeId)
                                .orElseThrow(() -> new EntityNotFoundException("Trainee not found with id: " + traineeId));

                Trainer trainer = trainerRepository.findById(trainerId)
                                .orElseThrow(() -> new EntityNotFoundException("Trainer not found with id: " + trainerId));

                if (!trainee.getTrainers().contains(trainer)) {
                        log.error("Trainer {} is not assigned to trainee {}", trainerId, traineeId);
                        throw new TrainerNotAssignedException(
                                        "Trainer with id " + trainerId + " is not assigned to trainee with id "
                                                        + traineeId);
                }

                TrainingType existingTrainingType = trainingTypeRepository.findById(trainingType.getId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Training type not found with id: " + trainingType.getId()));

                if (!trainer.getSpecialization().getId().equals(existingTrainingType.getId())) {
                        log.error("Training type {} does not match trainer specialization {}",
                                        existingTrainingType.getName(), trainer.getSpecialization().getName());
                        throw new ValidationException(
                                        "Training type '" + existingTrainingType.getName() +
                                                        "' does not match trainer's specialization '"
                                                        + trainer.getSpecialization().getName() + "'");
                }

                validationUtil.validateTrainingDate(trainingDate);

                validationUtil.validateTrainingDuration(trainingDuration);

                List<Training> traineeTrainings = trainingRepository.findByTrainee_User_Username(
                                trainee.getUser().getUsername());
                boolean traineeHasTraining = traineeTrainings.stream()
                                .anyMatch(t -> t.getTrainingDate().equals(trainingDate));
                if (traineeHasTraining) {
                        log.error("Trainee {} is not available on date {}", trainee.getUser().getUsername(),
                                        trainingDate);
                        throw new TraineeNotAvailableException(
                                        "Trainee is not available on " + trainingDate
                                                        + ". Another training is scheduled.");
                }

                List<Training> trainerTrainings = trainingRepository.findByTrainer_User_Username(
                                trainer.getUser().getUsername());
                boolean trainerHasTraining = trainerTrainings.stream()
                                .anyMatch(t -> t.getTrainingDate().equals(trainingDate));
                if (trainerHasTraining) {
                        log.error("Trainer {} is not available on date {}", trainer.getUser().getUsername(),
                                        trainingDate);
                        throw new TrainerNotAvailableException(
                                        "Trainer is not available on " + trainingDate
                                                        + ". Another training is scheduled.");
                }

                Training training = new Training();
                training.setTrainee(trainee);
                training.setTrainer(trainer);
                training.setTrainingName(trainingName);
                training.setTrainingType(existingTrainingType);
                training.setTrainingDate(trainingDate);
                training.setTrainingDuration(trainingDuration);

                Training createdTraining = trainingRepository.save(training);
                log.info("Training created successfully: id={}, name={}", createdTraining.getId(), trainingName);

                return createdTraining;
        }

        @Override
        @Transactional(readOnly = true)
        public Training getTrainingById(Long id) {
                log.debug("Fetching training by id: {}", id);

                return trainingRepository.findById(id).orElse(null);
        }

        @Override
        @Transactional(readOnly = true)
        public List<Training> getAllTrainings() {
                log.debug("Fetching all trainings");

                List<Training> trainings = trainingRepository.findAll();
                log.debug("Found {} trainings", trainings.size());

                return trainings;
        }

        @Override
        @Transactional(readOnly = true)
        public List<Training> getTraineeTrainingsWithCriteria(String traineeUsername, LocalDate fromDate,
                        LocalDate toDate, String trainerName, String trainingTypeName) {
                log.debug("Fetching trainee trainings with criteria: traineeUsername={}, fromDate={}, toDate={}, trainerName={}, trainingType=",
                                traineeUsername, fromDate, toDate, trainerName, trainingTypeName);

                validationUtil.validateNotBlank(traineeUsername, "Trainee username");

                traineeRepository.findByUser_Username(traineeUsername)
                                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + traineeUsername));

                validationUtil.validateReasonableDateRange(fromDate, toDate);

                if (trainingTypeName != null && !trainingTypeName.trim().isEmpty()) {
                        trainingTypeRepository.findByName(trainingTypeName)
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                        "Training type not found: " + trainingTypeName));
                }

                TraineeTrainingCriteriaDTO criteria = TraineeTrainingCriteriaDTO.builder()
                                .traineeUsername(traineeUsername)
                                .fromDate(fromDate)
                                .toDate(toDate)
                                .trainerName(trainerName)
                                .trainingTypeName(trainingTypeName)
                                .build();

                List<Training> trainings = trainingRepository.findTraineeTrainings(criteria);

                log.info("Found {} trainings for trainee: {}", trainings.size(), traineeUsername);
                return trainings;
        }

        @Override
        @Transactional(readOnly = true)
        public List<Training> getTrainerTrainingsWithCriteria(String trainerUsername, LocalDate fromDate,
                        LocalDate toDate, String traineeName) {
                log.debug("Fetching trainer trainings with criteria: trainerUsername={}, fromDate={}, toDate={}, traineeName={}",
                                trainerUsername, fromDate, toDate, traineeName);

                validationUtil.validateNotBlank(trainerUsername, "Trainer username");

                trainerRepository.findByUser_Username(trainerUsername)
                                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + trainerUsername));

                validationUtil.validateReasonableDateRange(fromDate, toDate);

                TrainerTrainingCriteriaDTO criteria = TrainerTrainingCriteriaDTO.builder()
                                .trainerUsername(trainerUsername)
                                .fromDate(fromDate)
                                .toDate(toDate)
                                .traineeName(traineeName)
                                .build();

                List<Training> trainings = trainingRepository.findTrainerTrainings(criteria);

                log.info("Found {} trainings for trainer: {}", trainings.size(), trainerUsername);
                return trainings;
        }

}
