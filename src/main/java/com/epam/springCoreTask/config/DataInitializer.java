package com.epam.springCoreTask.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.epam.springCoreTask.model.Trainee;
import com.epam.springCoreTask.model.Trainer;
import com.epam.springCoreTask.model.Training;
import com.epam.springCoreTask.model.TrainingType;
import com.epam.springCoreTask.model.User;
import com.epam.springCoreTask.repository.TraineeRepository;
import com.epam.springCoreTask.repository.TrainerRepository;
import com.epam.springCoreTask.repository.TrainingRepository;
import com.epam.springCoreTask.repository.TrainingTypeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final TrainingTypeRepository trainingTypeRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;

    @Override
    public void run(String... args) {
        initializeTrainingTypes();
        initializeDemoData();
    }

    private void initializeTrainingTypes() {
        log.info("Initializing training types...");

        String[] trainingTypes = {
                "FITNESS",
                "YOGA",
                "ZUMBA",
                "STRETCHING",
                "RESISTANCE",
                "CARDIO",
                "PILATES",
                "CROSSFIT"
        };

        for (String typeName : trainingTypes) {
            if (!trainingTypeRepository.existsByName(typeName)) {
                TrainingType trainingType = new TrainingType();
                trainingType.setName(typeName);
                trainingTypeRepository.save(trainingType);
                log.debug("Training type created: {}", typeName);
            } else {
                log.debug("Training type already exists: {}", typeName);
            }
        }

        log.info("Training types initialization completed. Total types: {}", trainingTypeRepository.count());
    }

    private void initializeDemoData() {
        log.info("Checking if demo data initialization is needed...");

        if (traineeRepository.count() > 0) {
            log.info("Demo data already exists. Skipping initialization.");
            return;
        }

        log.info("Initializing demo data...");

        List<Trainee> trainees = createTrainees();

        List<Trainer> trainers = createTrainers();

        createTrainings(trainees, trainers);

        log.info("Demo data initialization completed.");
        log.info("Total trainees: {}", traineeRepository.count());
        log.info("Total trainers: {}", trainerRepository.count());
        log.info("Total trainings: {}", trainingRepository.count());
    }

    private List<Trainee> createTrainees() {
        List<Trainee> trainees = new ArrayList<>();

        User user1 = new User(null, "John", "Doe", "john.doe", "password123", true);
        Trainee trainee1 = new Trainee();
        trainee1.setUser(user1);
        trainee1.setDateOfBirth(LocalDate.of(1995, 5, 15));
        trainee1.setAddress("123 Main Street, New York");
        trainees.add(traineeRepository.save(trainee1));
        log.debug("Created trainee: {}", user1.getUsername());

        User user2 = new User(null, "Jane", "Smith", "jane.smith", "password123", true);
        Trainee trainee2 = new Trainee();
        trainee2.setUser(user2);
        trainee2.setDateOfBirth(LocalDate.of(1992, 8, 22));
        trainee2.setAddress("456 Oak Avenue, Los Angeles");
        trainees.add(traineeRepository.save(trainee2));
        log.debug("Created trainee: {}", user2.getUsername());

        User user3 = new User(null, "Mike", "Johnson", "mike.johnson", "password123", true);
        Trainee trainee3 = new Trainee();
        trainee3.setUser(user3);
        trainee3.setDateOfBirth(LocalDate.of(1998, 3, 10));
        trainee3.setAddress("789 Pine Road, Chicago");
        trainees.add(traineeRepository.save(trainee3));
        log.debug("Created trainee: {}", user3.getUsername());

        User user4 = new User(null, "Emily", "Davis", "emily.davis", "password123", false);
        Trainee trainee4 = new Trainee();
        trainee4.setUser(user4);
        trainee4.setDateOfBirth(LocalDate.of(2000, 11, 5));
        trainee4.setAddress("321 Elm Street, Boston");
        trainees.add(traineeRepository.save(trainee4));
        log.debug("Created trainee: {}", user4.getUsername());

        return trainees;
    }

    private List<Trainer> createTrainers() {
        List<Trainer> trainers = new ArrayList<>();

        User user1 = new User(null, "Sarah", "Williams", "sarah.williams", "trainer123", true);
        Trainer trainer1 = new Trainer();
        trainer1.setUser(user1);
        trainer1.setSpecialization(trainingTypeRepository.findByName("FITNESS").orElseThrow());
        trainers.add(trainerRepository.save(trainer1));
        log.debug("Created trainer: {} - Specialization: FITNESS", user1.getUsername());

        User user2 = new User(null, "David", "Brown", "david.brown", "trainer123", true);
        Trainer trainer2 = new Trainer();
        trainer2.setUser(user2);
        trainer2.setSpecialization(trainingTypeRepository.findByName("YOGA").orElseThrow());
        trainers.add(trainerRepository.save(trainer2));
        log.debug("Created trainer: {} - Specialization: YOGA", user2.getUsername());

        User user3 = new User(null, "Lisa", "Martinez", "lisa.martinez", "trainer123", true);
        Trainer trainer3 = new Trainer();
        trainer3.setUser(user3);
        trainer3.setSpecialization(trainingTypeRepository.findByName("CARDIO").orElseThrow());
        trainers.add(trainerRepository.save(trainer3));
        log.debug("Created trainer: {} - Specialization: CARDIO", user3.getUsername());

        User user4 = new User(null, "Robert", "Garcia", "robert.garcia", "trainer123", true);
        Trainer trainer4 = new Trainer();
        trainer4.setUser(user4);
        trainer4.setSpecialization(trainingTypeRepository.findByName("PILATES").orElseThrow());
        trainers.add(trainerRepository.save(trainer4));
        log.debug("Created trainer: {} - Specialization: PILATES", user4.getUsername());

        User user5 = new User(null, "Amanda", "Lee", "amanda.lee", "trainer123", false);
        Trainer trainer5 = new Trainer();
        trainer5.setUser(user5);
        trainer5.setSpecialization(trainingTypeRepository.findByName("CROSSFIT").orElseThrow());
        trainers.add(trainerRepository.save(trainer5));
        log.debug("Created trainer: {} - Specialization: CROSSFIT", user5.getUsername());

        return trainers;
    }

    private void createTrainings(List<Trainee> trainees, List<Trainer> trainers) {
        LocalDate today = LocalDate.now();

        Training training1 = new Training();
        training1.setTrainee(trainees.get(0));
        training1.setTrainer(trainers.get(0));
        training1.setTrainingName("Full Body Workout");
        training1.setTrainingType(trainers.get(0).getSpecialization());
        training1.setTrainingDate(today.minusDays(5));
        training1.setTrainingDuration(60);
        trainingRepository.save(training1);
        log.debug("Created training: {} - {} with {}",
                training1.getTrainingName(),
                trainees.get(0).getUser().getUsername(),
                trainers.get(0).getUser().getUsername());

        Training training2 = new Training();
        training2.setTrainee(trainees.get(0));
        training2.setTrainer(trainers.get(1));
        training2.setTrainingName("Morning Yoga Session");
        training2.setTrainingType(trainers.get(1).getSpecialization());
        training2.setTrainingDate(today.minusDays(3));
        training2.setTrainingDuration(45);
        trainingRepository.save(training2);
        log.debug("Created training: {} - {} with {}",
                training2.getTrainingName(),
                trainees.get(0).getUser().getUsername(),
                trainers.get(1).getUser().getUsername());

        Training training3 = new Training();
        training3.setTrainee(trainees.get(1));
        training3.setTrainer(trainers.get(2));
        training3.setTrainingName("Intensive Cardio Training");
        training3.setTrainingType(trainers.get(2).getSpecialization());
        training3.setTrainingDate(today.minusDays(7));
        training3.setTrainingDuration(90);
        trainingRepository.save(training3);
        log.debug("Created training: {} - {} with {}",
                training3.getTrainingName(),
                trainees.get(1).getUser().getUsername(),
                trainers.get(2).getUser().getUsername());

        Training training4 = new Training();
        training4.setTrainee(trainees.get(1));
        training4.setTrainer(trainers.get(0));
        training4.setTrainingName("Strength Training");
        training4.setTrainingType(trainers.get(0).getSpecialization());
        training4.setTrainingDate(today.minusDays(2));
        training4.setTrainingDuration(75);
        trainingRepository.save(training4);
        log.debug("Created training: {} - {} with {}",
                training4.getTrainingName(),
                trainees.get(1).getUser().getUsername(),
                trainers.get(0).getUser().getUsername());

        Training training5 = new Training();
        training5.setTrainee(trainees.get(2));
        training5.setTrainer(trainers.get(3));
        training5.setTrainingName("Core Pilates Workout");
        training5.setTrainingType(trainers.get(3).getSpecialization());
        training5.setTrainingDate(today.minusDays(10));
        training5.setTrainingDuration(50);
        trainingRepository.save(training5);
        log.debug("Created training: {} - {} with {}",
                training5.getTrainingName(),
                trainees.get(2).getUser().getUsername(),
                trainers.get(3).getUser().getUsername());

        Training training6 = new Training();
        training6.setTrainee(trainees.get(2));
        training6.setTrainer(trainers.get(1));
        training6.setTrainingName("Evening Relaxation Yoga");
        training6.setTrainingType(trainers.get(1).getSpecialization());
        training6.setTrainingDate(today.minusDays(1));
        training6.setTrainingDuration(60);
        trainingRepository.save(training6);
        log.debug("Created training: {} - {} with {}",
                training6.getTrainingName(),
                trainees.get(2).getUser().getUsername(),
                trainers.get(1).getUser().getUsername());

        Training training7 = new Training();
        training7.setTrainee(trainees.get(2));
        training7.setTrainer(trainers.get(2));
        training7.setTrainingName("HIIT Cardio Session");
        training7.setTrainingType(trainers.get(2).getSpecialization());
        training7.setTrainingDate(today);
        training7.setTrainingDuration(45);
        trainingRepository.save(training7);
        log.debug("Created training: {} - {} with {}",
                training7.getTrainingName(),
                trainees.get(2).getUser().getUsername(),
                trainers.get(2).getUser().getUsername());

        Training training8 = new Training();
        training8.setTrainee(trainees.get(0));
        training8.setTrainer(trainers.get(2));
        training8.setTrainingName("Running Endurance Training");
        training8.setTrainingType(trainers.get(2).getSpecialization());
        training8.setTrainingDate(today.plusDays(3));
        training8.setTrainingDuration(60);
        trainingRepository.save(training8);
        log.debug("Created training: {} - {} with {}",
                training8.getTrainingName(),
                trainees.get(0).getUser().getUsername(),
                trainers.get(2).getUser().getUsername());
    }
}
