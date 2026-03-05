package com.epam.springCoreTask.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.epam.springCoreTask.dto.TraineeTrainingCriteriaDTO;
import com.epam.springCoreTask.dto.TrainerTrainingCriteriaDTO;
import com.epam.springCoreTask.model.Training;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

       List<Training> findByTrainee_User_Username(String traineeUsername);

       List<Training> findByTrainer_User_Username(String trainerUsername);

       @Query("SELECT t FROM Training t WHERE t.trainee.user.username = :#{#criteria.traineeUsername} " +
                     "AND (:#{#criteria.fromDate} IS NULL OR t.trainingDate >= :#{#criteria.fromDate}) " +
                     "AND (:#{#criteria.toDate} IS NULL OR t.trainingDate <= :#{#criteria.toDate}) " +
                     "AND (:#{#criteria.trainerName} IS NULL OR t.trainer.user.firstName LIKE %:#{#criteria.trainerName}% OR t.trainer.user.lastName LIKE %:#{#criteria.trainerName}%) "
                     +
                     "AND (:#{#criteria.trainingTypeName} IS NULL OR t.trainingType.name = :#{#criteria.trainingTypeName})")
       List<Training> findTraineeTrainings(@Param("criteria") TraineeTrainingCriteriaDTO criteria);

       @Query("SELECT t FROM Training t WHERE t.trainer.user.username = :#{#criteria.trainerUsername} " +
                     "AND (:#{#criteria.fromDate} IS NULL OR t.trainingDate >= :#{#criteria.fromDate}) " +
                     "AND (:#{#criteria.toDate} IS NULL OR t.trainingDate <= :#{#criteria.toDate}) " +
                     "AND (:#{#criteria.traineeName} IS NULL OR t.trainee.user.firstName LIKE %:#{#criteria.traineeName}% OR t.trainee.user.lastName LIKE %:#{#criteria.traineeName}%)")
       List<Training> findTrainerTrainings(@Param("criteria") TrainerTrainingCriteriaDTO criteria);
}
