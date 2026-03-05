package com.epam.springCoreTask.service;

import java.time.LocalDate;
import java.util.List;

import com.epam.springCoreTask.model.Training;
import com.epam.springCoreTask.model.TrainingType;

public interface TrainingService {

        Training createTraining(Long traineeId, Long trainerId, String trainingName,
                        TrainingType trainingType, LocalDate trainingDate, int trainingDuration);

        Training getTrainingById(Long id);

        List<Training> getAllTrainings();

        List<Training> getTraineeTrainingsWithCriteria(String traineeUsername, LocalDate fromDate,
                        LocalDate toDate, String trainerName, String trainingTypeName);

        List<Training> getTrainerTrainingsWithCriteria(String trainerUsername, LocalDate fromDate,
                        LocalDate toDate, String traineeName);
}
