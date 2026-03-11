package com.epam.springCoreTask.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.epam.springCoreTask.dto.response.TrainingResponse;
import com.epam.springCoreTask.model.Training;

@Mapper(componentModel = "spring")
public interface TrainingMapper {

    @Mapping(source = "trainingName", target = "trainingName")
    @Mapping(source = "trainingDate", target = "trainingDate")
    @Mapping(source = "trainingType.name", target = "trainingType")
    @Mapping(source = "trainingDuration", target = "trainingDuration")
    @Mapping(target = "trainerName", expression = "java(training.getTrainer().getUser().getFirstName() + \" \" + training.getTrainer().getUser().getLastName())")
    @Mapping(target = "traineeName", expression = "java(training.getTrainee().getUser().getFirstName() + \" \" + training.getTrainee().getUser().getLastName())")
    TrainingResponse toTrainingResponse(Training training);

    List<TrainingResponse> toTrainingResponseList(List<Training> trainings);
}
