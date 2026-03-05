package com.epam.springCoreTask.facade;

import java.time.LocalDate;
import java.util.List;

import com.epam.springCoreTask.model.Trainee;
import com.epam.springCoreTask.model.Trainer;
import com.epam.springCoreTask.model.Training;
import com.epam.springCoreTask.model.TrainingType;

public interface GymFacade {
        Trainee createTraineeProfile(String firstName, String lastName, LocalDate dateOfBirth, String address);

        Trainer createTrainerProfile(String firstName, String lastName, String specialization);

        Training createTrainingSession(Long traineeId, Long trainerId, String trainingName, TrainingType trainingType,
                        LocalDate trainingDate, int trainingDuration);

        List<Training> getTraineeTrainings(Long traineeId);

        List<Training> getTrainerTrainings(Long trainerId);

        Trainee updateTraineeProfile(Trainee trainee);

        Trainer updateTrainerProfile(Trainer trainer);

        void deleteTraineeProfile(Long traineeId);

        Trainee getTraineeById(Long id);

        Trainer getTrainerById(Long id);

        Training getTrainingById(Long id);

        List<Trainee> getAllTrainees();

        List<Trainer> getAllTrainers();

        List<Training> getAllTrainings();

        Trainee authenticateTrainee(String username, String password);

        Trainer authenticateTrainer(String username, String password);

        Trainee getTraineeByUsername(String username);

        Trainer getTrainerByUsername(String username);

        void changeTraineePassword(String username, String oldPassword, String newPassword);

        void changeTrainerPassword(String username, String oldPassword, String newPassword);

        void activateTrainee(String username);

        void deactivateTrainee(String username);

        void activateTrainer(String username);

        void deactivateTrainer(String username);

        void deleteTraineeByUsername(String username);

        List<Training> getTraineeTrainingsWithCriteria(String traineeUsername, LocalDate fromDate,
                        LocalDate toDate, String trainerName, String trainingTypeName);

        List<Training> getTrainerTrainingsWithCriteria(String trainerUsername, LocalDate fromDate,
                        LocalDate toDate, String traineeName);

        List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername);

        void updateTraineeTrainersList(String traineeUsername, List<String> trainerUsernames);
}
