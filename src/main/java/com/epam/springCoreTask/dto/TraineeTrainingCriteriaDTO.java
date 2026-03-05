package com.epam.springCoreTask.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraineeTrainingCriteriaDTO {
    private String traineeUsername;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String trainerName;
    private String trainingTypeName;
}
