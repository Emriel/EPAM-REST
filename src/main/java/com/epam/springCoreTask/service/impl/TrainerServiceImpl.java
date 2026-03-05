package com.epam.springCoreTask.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.springCoreTask.exception.EntityNotFoundException;
import com.epam.springCoreTask.model.Trainer;
import com.epam.springCoreTask.model.TrainingType;
import com.epam.springCoreTask.model.User;
import com.epam.springCoreTask.repository.TraineeRepository;
import com.epam.springCoreTask.repository.TrainerRepository;
import com.epam.springCoreTask.repository.TrainingTypeRepository;
import com.epam.springCoreTask.service.TrainerService;
import com.epam.springCoreTask.service.UserService;
import com.epam.springCoreTask.util.PasswordGenerator;
import com.epam.springCoreTask.util.UsernameGenerator;
import com.epam.springCoreTask.util.ValidationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TrainerServiceImpl implements TrainerService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final UserService userService;
    private final ValidationUtil validationUtil;

    @Override
    public Trainer createTrainer(String firstName, String lastName, String specialization) {
        log.debug("Creating trainer: {} {}, specialization: {}", firstName, lastName, specialization);

        validationUtil.validateNotBlank(firstName, "First name");
        validationUtil.validateNotBlank(lastName, "Last name");
        validationUtil.validateNotBlank(specialization, "Specialization");

        List<String> existingUsernames = new java.util.ArrayList<>(trainerRepository.findAllUsernames());
        existingUsernames.addAll(traineeRepository.findAllUsernames());

        String username = usernameGenerator.generateUsername(firstName, lastName, existingUsernames);
        String password = passwordGenerator.generatePassword();

        TrainingType trainingType = trainingTypeRepository.findByName(specialization)
                .orElseThrow(() -> new EntityNotFoundException("Training type not found: " + specialization));

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(trainingType);

        Trainer createdTrainer = trainerRepository.save(trainer);
        log.info("Trainer created successfully: id={}, username={}", createdTrainer.getId(),
                createdTrainer.getUser().getUsername());

        return createdTrainer;
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        log.debug("Updating trainer: id={}, username={}", trainer.getId(), trainer.getUser().getUsername());

        validationUtil.validateNotNull(trainer, "Trainer");
        validationUtil.validateNotNull(trainer.getId(), "Trainer ID");

        if (!trainerRepository.existsById(trainer.getId())) {
            throw new EntityNotFoundException("Trainer not found with id: " + trainer.getId());
        }

        validationUtil.validateNotNull(trainer.getUser(), "User");
        validationUtil.validateNotBlank(trainer.getUser().getFirstName(), "First name");
        validationUtil.validateNotBlank(trainer.getUser().getLastName(), "Last name");

        Trainer updatedTrainer = trainerRepository.save(trainer);
        log.info("Trainer updated successfully: id={}", trainer.getId());

        return updatedTrainer;
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer getTrainerById(Long id) {
        log.debug("Fetching trainer by id: {}", id);

        return trainerRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> getAllTrainers() {
        log.debug("Fetching all trainers");

        List<Trainer> trainers = trainerRepository.findAll();
        log.debug("Found {} trainers", trainers.size());

        return trainers;
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer authenticateTrainer(String username, String password) {
        return userService.authenticate(
                username,
                password,
                trainerRepository::findByUsernameAndPassword,
                "trainer");
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer getTrainerByUsername(String username) {
        log.debug("Fetching trainer by username: {}", username);

        return trainerRepository.findByUser_Username(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with username: " + username));
    }

    @Override
    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        userService.changePassword(
                username,
                oldPassword,
                newPassword,
                trainerRepository::findByUsernameAndPassword,
                Trainer::getUser,
                trainerRepository::save,
                "trainer");
    }

    @Override
    public void activateTrainer(String username) {
        userService.activateEntity(
                username,
                this::getTrainerByUsername,
                Trainer::getUser,
                trainerRepository::save,
                "trainer");
    }

    @Override
    public void deactivateTrainer(String username) {
        userService.deactivateEntity(
                username,
                this::getTrainerByUsername,
                Trainer::getUser,
                trainerRepository::save,
                "trainer");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername) {
        log.debug("Fetching trainers not assigned to trainee: traineeUsername={}", traineeUsername);

        validationUtil.validateNotBlank(traineeUsername, "Trainee username");

        List<Trainer> trainers = trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername);
        log.info("Found {} unassigned trainers for trainee: {}", trainers.size(), traineeUsername);

        return trainers;
    }
}
