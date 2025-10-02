package com.crm.gym.api.repositories.interfaces;

import java.util.List;
import com.crm.gym.api.entities.Trainer;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrainerRepository extends UserRepository<Trainer>,
        MassiveUpdateRepository, TrainerRefsManager
{
    @Override
    @Transactional
    default Trainer updateByUsername(String username, Trainer trainer)
    {
        resolveReferencesByAltKeys(trainer);
        nullifyInvalidReferences(trainer);
        return UserRepository.super.updateByUsername(username, trainer);
    }

    String FIND_UNASSIGNED_TRAINERS_FOR_TRAINEE =
            "SELECT DISTINCT t1 FROM Trainer t1 " + // Fetch all active trainers
            "LEFT JOIN Training t2 " +
            "ON t2.trainer = t1 " +
            "LEFT JOIN Trainee t3 " +
            "ON t2.trainee = t3 " +
            "WHERE t1.isActive " +
            "AND (t2 IS NULL " + // with no assigned trainees
            "OR t3.username != :username)"; // or not assigned to the trainee with such username

    @Query(FIND_UNASSIGNED_TRAINERS_FOR_TRAINEE)
    List<Trainer> findAllUnassignedForTraineeByUsername(@Param("username") String username);

    @Query(FIND_UNASSIGNED_TRAINERS_FOR_TRAINEE)
    Page<Trainer> findAllUnassignedForTraineeByUsername(@Param("username") String username, Pageable pageable);
}
