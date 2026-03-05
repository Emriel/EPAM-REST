package com.epam.springCoreTask.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.springCoreTask.exception.DuplicateAssignmentException;
import com.epam.springCoreTask.exception.EntityNotFoundException;
import com.epam.springCoreTask.model.Trainee;
import com.epam.springCoreTask.model.Trainer;
import com.epam.springCoreTask.model.User;
import com.epam.springCoreTask.repository.TraineeRepository;
import com.epam.springCoreTask.repository.TrainerRepository;
import com.epam.springCoreTask.service.TraineeService;
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
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final UserService userService;
    private final ValidationUtil validationUtil;

    @Override
    public Trainee createTrainee(String firstName, String lastName, java.time.LocalDate dateOfBirth, String address) {
        log.debug("Creating trainee: {} {}, dateOfBirth: {}, address: {}", firstName, lastName, dateOfBirth, address);

        validationUtil.validateNotBlank(firstName, "First name");
        validationUtil.validateNotBlank(lastName, "Last name");
        validationUtil.validateDateOfBirth(dateOfBirth);

        List<String> existingUsernames = new java.util.ArrayList<>(traineeRepository.findAllUsernames());
        existingUsernames.addAll(trainerRepository.findAllUsernames());

        String username = usernameGenerator.generateUsername(firstName, lastName, existingUsernames);
        String password = passwordGenerator.generatePassword();

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        Trainee createdTrainee = traineeRepository.save(trainee);
        log.info("Trainee created successfully: id={}, username={}", createdTrainee.getId(),
                createdTrainee.getUser().getUsername());

        return createdTrainee;
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        log.debug("Updating trainee: id={}, username={}", trainee.getId(), trainee.getUser().getUsername());

        validationUtil.validateNotNull(trainee, "Trainee");
        validationUtil.validateNotNull(trainee.getId(), "Trainee ID");

        if (!traineeRepository.existsById(trainee.getId())) {
            throw new EntityNotFoundException("Trainee not found with id: " + trainee.getId());
        }

        validationUtil.validateNotNull(trainee.getUser(), "User");
        validationUtil.validateNotBlank(trainee.getUser().getFirstName(), "First name");
        validationUtil.validateNotBlank(trainee.getUser().getLastName(), "Last name");

        if (trainee.getDateOfBirth() != null) {
            validationUtil.validateDateOfBirth(trainee.getDateOfBirth());
        }

        Trainee updatedTrainee = traineeRepository.save(trainee);
        log.info("Trainee updated successfully: id={}", trainee.getId());

        return updatedTrainee;
    }

    @Override
    public void deleteTrainee(Trainee trainee) {
        log.debug("Deleting trainee: id={}, username={}", trainee.getId(), trainee.getUser().getUsername());

        traineeRepository.delete(trainee);
        log.info("Trainee deleted successfully: id={}", trainee.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Trainee getTraineeById(Long id) {
        log.debug("Fetching trainee by id: {}", id);

        return traineeRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainee> getAllTrainees() {
        log.debug("Fetching all trainees");

        List<Trainee> trainees = traineeRepository.findAll();
        log.debug("Found {} trainees", trainees.size());

        return trainees;
    }

    @Override
    @Transactional(readOnly = true)
    public Trainee authenticateTrainee(String username, String password) {
        return userService.authenticate(
                username,
                password,
                traineeRepository::findByUsernameAndPassword,
                "trainee");
    }

    @Override
    @Transactional(readOnly = true)
    public Trainee getTraineeByUsername(String username) {
        log.debug("Fetching trainee by username: {}", username);

        return traineeRepository.findByUser_Username(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found with username: " + username));
    }

    @Override
    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        userService.changePassword(
                username,
                oldPassword,
                newPassword,
                traineeRepository::findByUsernameAndPassword,
                Trainee::getUser,
                traineeRepository::save,
                "trainee");
    }

    @Override
    public void activateTrainee(String username) {
        userService.activateEntity(
                username,
                this::getTraineeByUsername,
                Trainee::getUser,
                traineeRepository::save,
                "trainee");
    }

    @Override
    public void deactivateTrainee(String username) {
        userService.deactivateEntity(
                username,
                this::getTraineeByUsername,
                Trainee::getUser,
                traineeRepository::save,
                "trainee");
    }

    @Override
    public void deleteTraineeByUsername(String username) {
        log.debug("Deleting trainee by username: {}", username);

        Trainee trainee = getTraineeByUsername(username);
        traineeRepository.delete(trainee);

        log.info("Trainee deleted successfully: username={}", username);
    }

    @Override
    public void updateTraineeTrainersList(String traineeUsername, List<String> trainerUsernames) {
        log.debug("Updating trainers list for trainee: traineeUsername={}, trainerUsernames={}",
                traineeUsername, trainerUsernames);

        validationUtil.validateNotBlank(traineeUsername, "Trainee username");
        Trainee trainee = getTraineeByUsername(traineeUsername);

        // Clear existing trainers
        trainee.getTrainers().clear();

        // Add new trainers
        if (trainerUsernames != null && !trainerUsernames.isEmpty()) {
            for (String trainerUsername : trainerUsernames) {
                validationUtil.validateNotBlank(trainerUsername, "Trainer username");
                Trainer trainer = trainerRepository.findByUser_Username(trainerUsername)
                        .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + trainerUsername));

                if (trainee.getTrainers().contains(trainer)) {
                    throw new DuplicateAssignmentException(
                            "Trainer " + trainerUsername + " is already assigned to trainee " + traineeUsername);
                }

                trainee.getTrainers().add(trainer);
            }
        }

        traineeRepository.save(trainee);
        log.info("Trainers list updated successfully for trainee: username={}, trainers count={}",
                traineeUsername, trainee.getTrainers().size());
    }
}
