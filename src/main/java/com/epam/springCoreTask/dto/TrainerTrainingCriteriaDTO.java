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
public class TrainerTrainingCriteriaDTO {
    private String trainerUsername;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String traineeName;
}
