package com.epam.springCoreTask.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.epam.springCoreTask.dto.request.TrainerUpdateRequest;
import com.epam.springCoreTask.dto.response.TraineeSummary;
import com.epam.springCoreTask.dto.response.TrainerProfileResponse;
import com.epam.springCoreTask.dto.response.TrainerSummary;
import com.epam.springCoreTask.model.Trainee;
import com.epam.springCoreTask.model.Trainer;

@Mapper(componentModel = "spring")
public interface TrainerMapper {

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "specialization.name", target = "specialization")
    @Mapping(source = "user.active", target = "isActive")
    @Mapping(source = "trainees", target = "trainees")
    TrainerProfileResponse toProfileResponse(Trainer trainer);

    List<TrainerProfileResponse> toProfileResponseList(List<Trainer> trainers);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "specialization.name", target = "specialization")
    TrainerSummary toTrainerSummary(Trainer trainer);

    List<TrainerSummary> toTrainerSummaryList(List<Trainer> trainers);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    TraineeSummary toTraineeSummary(Trainee trainee);

    List<TraineeSummary> toTraineeSummaryList(List<Trainee> trainees);

    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "user.active", source = "isActive")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user.id", ignore = true)
    @Mapping(target = "user.username", ignore = true)
    @Mapping(target = "user.password", ignore = true)
    @Mapping(target = "specialization", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainees", ignore = true)
    void updateTrainerFromRequest(TrainerUpdateRequest request, @MappingTarget Trainer trainer);
}
