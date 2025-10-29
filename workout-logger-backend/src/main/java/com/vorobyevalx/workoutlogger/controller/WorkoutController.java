package com.vorobyevalx.workoutlogger.controller;

import com.vorobyevalx.workoutlogger.model.Workout;
import com.vorobyevalx.workoutlogger.model.WorkoutPriority;
import com.vorobyevalx.workoutlogger.model.WorkoutStatus;
import com.vorobyevalx.workoutlogger.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/workouts")
@CrossOrigin(origins = "http://localhost:3000")
public class WorkoutController {

    @Autowired
    private WorkoutRepository workoutRepository;

    // GET /api/workouts - Get all workouts
    @GetMapping
    public ResponseEntity<List<Workout>> getAllWorkouts() {
        List<Workout> workouts = workoutRepository.findAll();
        return ResponseEntity.ok(workouts);
    }

    // GET /api/workouts/{id} - Get workout by ID
    @GetMapping("/{id}")
    public ResponseEntity<Workout> getWorkoutById(@PathVariable Long id) {
        Optional<Workout> workout = workoutRepository.findById(id);
        return workout.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/workouts - Create new workout
    @PostMapping
    public ResponseEntity<Workout> createWorkout(@RequestBody Workout workout) {
        // Set default values if not provided
        if (workout.getStatus() == null) {
            workout.setStatus(WorkoutStatus.PLANNED);
        }
        if (workout.getPriority() == null) {
            workout.setPriority(WorkoutPriority.MEDIUM);
        }
        
        Workout savedWorkout = workoutRepository.save(workout);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedWorkout);
    }

    // PUT /api/workouts/{id} - Update workout
    @PutMapping("/{id}")
    public ResponseEntity<Workout> updateWorkout(@PathVariable Long id, @RequestBody Workout workoutDetails) {
        Optional<Workout> optionalWorkout = workoutRepository.findById(id);
        
        if (optionalWorkout.isPresent()) {
            Workout workout = optionalWorkout.get();
            workout.setExerciseName(workoutDetails.getExerciseName());
            workout.setDescription(workoutDetails.getDescription());
            workout.setDueDate(workoutDetails.getDueDate());
            workout.setStatus(workoutDetails.getStatus());
            workout.setPriority(workoutDetails.getPriority());
            
            Workout updatedWorkout = workoutRepository.save(workout);
            return ResponseEntity.ok(updatedWorkout);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/workouts/{id} - Delete workout
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id) {
        if (workoutRepository.existsById(id)) {
            workoutRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/workouts/status/{status} - Get workouts by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Workout>> getWorkoutsByStatus(@PathVariable WorkoutStatus status) {
        List<Workout> workouts = workoutRepository.findByStatus(status);
        return ResponseEntity.ok(workouts);
    }

    // GET /api/workouts/priority/{priority} - Get workouts by priority
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Workout>> getWorkoutsByPriority(@PathVariable WorkoutPriority priority) {
        List<Workout> workouts = workoutRepository.findByPriority(priority);
        return ResponseEntity.ok(workouts);
    }

    // GET /api/workouts/search?name={name} - Search workouts by exercise name
    @GetMapping("/search")
    public ResponseEntity<List<Workout>> searchWorkoutsByName(@RequestParam String name) {
        List<Workout> workouts = workoutRepository.findByExerciseNameContainingIgnoreCase(name);
        return ResponseEntity.ok(workouts);
    }
}
