package com.vorobyevalx.workoutlogger.controller;

import com.vorobyevalx.workoutlogger.model.Workout;
import com.vorobyevalx.workoutlogger.model.WorkoutPriority;
import com.vorobyevalx.workoutlogger.model.WorkoutStatus;
import com.vorobyevalx.workoutlogger.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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

    // Export all workouts as JSON (download)
    @GetMapping(value = "/export/json")
    public ResponseEntity<List<Workout>> exportJson() {
        List<Workout> workouts = workoutRepository.findAll();
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=workouts.json");
        return ResponseEntity.ok().headers(headers).body(workouts);
    }

    // Export all workouts as CSV (download)
    @GetMapping(value = "/export/csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportCsv() {
        List<Workout> workouts = workoutRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("id,exerciseName,description,dueDate,status,priority,workoutDay,sets,reps,equipment,muscleGroups,actualWeight,completedReps,completedSets\n");
        for (Workout w : workouts) {
            sb.append(nullSafe(w.getId())).append(',')
              .append(csv(w.getExerciseName())).append(',')
              .append(csv(w.getDescription())).append(',')
              .append(nullSafe(w.getDueDate())).append(',')
              .append(nullSafe(w.getStatus())).append(',')
              .append(nullSafe(w.getPriority())).append(',')
              .append(csv(w.getWorkoutDay())).append(',')
              .append(nullSafe(w.getSets())).append(',')
              .append(csv(w.getReps())).append(',')
              .append(csv(w.getEquipment())).append(',')
              .append(csv(w.getMuscleGroups())).append(',')
              .append(nullSafe(w.getActualWeight())).append(',')
              .append(nullSafe(w.getCompletedReps())).append(',')
              .append(nullSafe(w.getCompletedSets()))
              .append('\n');
        }
        byte[] bytes = sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=workouts.csv");
        headers.setContentType(new MediaType("text","csv"));
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    private static String csv(String s) {
        if (s == null) return "";
        String escaped = s.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\r") || escaped.contains("\"")) {
            return '"' + escaped + '"';
        }
        return escaped;
    }

    private static String nullSafe(Object o) { return o == null ? "" : o.toString(); }

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
            workout.setWorkoutDay(workoutDetails.getWorkoutDay());
            workout.setSets(workoutDetails.getSets());
            workout.setReps(workoutDetails.getReps());
            workout.setEquipment(workoutDetails.getEquipment());
            workout.setMuscleGroups(workoutDetails.getMuscleGroups());
            workout.setActualWeight(workoutDetails.getActualWeight());
            workout.setCompletedReps(workoutDetails.getCompletedReps());
            workout.setCompletedSets(workoutDetails.getCompletedSets());
            
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
