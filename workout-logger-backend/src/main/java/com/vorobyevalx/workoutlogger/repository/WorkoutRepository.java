package com.vorobyevalx.workoutlogger.repository;

import com.vorobyevalx.workoutlogger.model.Workout;
import com.vorobyevalx.workoutlogger.model.WorkoutPriority;
import com.vorobyevalx.workoutlogger.model.WorkoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    
    // Find workouts by status
    List<Workout> findByStatus(WorkoutStatus status);
    
    // Find workouts by priority
    List<Workout> findByPriority(WorkoutPriority priority);
    
    // Find workouts by status and priority
    List<Workout> findByStatusAndPriority(WorkoutStatus status, WorkoutPriority priority);
    
    // Find workouts due before a specific date
    List<Workout> findByDueDateBefore(LocalDate date);
    
    // Find workouts due after a specific date
    List<Workout> findByDueDateAfter(LocalDate date);
    
    // Find workouts by exercise name (case insensitive)
    List<Workout> findByExerciseNameContainingIgnoreCase(String exerciseName);
}
