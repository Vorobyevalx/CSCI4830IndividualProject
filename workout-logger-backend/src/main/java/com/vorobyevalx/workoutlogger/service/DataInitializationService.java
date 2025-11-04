package com.vorobyevalx.workoutlogger.service;

import com.vorobyevalx.workoutlogger.model.Workout;
import com.vorobyevalx.workoutlogger.model.WorkoutPriority;
import com.vorobyevalx.workoutlogger.model.WorkoutStatus;
import com.vorobyevalx.workoutlogger.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private WorkoutRepository workoutRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if database is empty
        if (workoutRepository.count() == 0) {
            initializeWorkoutData();
        }
    }

    private void initializeWorkoutData() {
        List<Workout> workouts = Arrays.asList(
            // Day 1: Chest, Shoulders, & Triceps
            createWorkout("Dumbbell Bench Press", "Primary chest exercise", "Day 1", 5, "8-10", "Dumbbells", "Chest, Shoulders, Triceps"),
            createWorkout("Incline Dumbbell Bench Press", "Upper chest focus", "Day 1", 4, "8-10", "Dumbbells", "Chest, Shoulders"),
            createWorkout("Dumbbell Floor Press", "Chest and triceps", "Day 1", 3, "8-12", "Dumbbells", "Chest, Triceps"),
            createWorkout("Standing Dumbbell Press", "Shoulder press", "Day 1", 4, "8-10", "Dumbbells", "Shoulders"),
            createWorkout("Dumbbell Lateral Raise", "Shoulder isolation", "Day 1", 3, "8-12", "Dumbbells", "Shoulders"),
            createWorkout("Dumbbell Tricep Kickback", "Tricep isolation", "Day 1", 3, "8-12", "Dumbbells", "Triceps"),

            // Day 2: Legs & Core
            createWorkout("Dumbbell Goblet Squat", "Primary leg exercise", "Day 2", 4, "8-10", "Dumbbells", "Legs, Core"),
            createWorkout("Dumbbell Stiff Leg Deadlift", "Hamstring focus", "Day 2", 4, "8-10", "Dumbbells", "Legs"),
            createWorkout("Dumbbell Rear Lunge", "Leg unilateral work", "Day 2", 4, "8-10", "Dumbbells", "Legs"),
            createWorkout("Dumbbell Frog Squat", "Leg variation", "Day 2", 3, "8-12", "Dumbbells", "Legs"),
            createWorkout("Dumbbell Calf Raise", "Calf isolation", "Day 2", 4, "20", "Dumbbells", "Calves"),
            createWorkout("Weighted Crunch", "Core exercise", "Day 2", 3, "20", "Dumbbells", "Core"),
            createWorkout("Side Planks", "Core stability", "Day 2", 3, "20 Secs Each", "Bodyweight", "Core"),

            // Day 3: Back & Biceps
            createWorkout("Dumbbell Bent Over Row", "Primary back exercise", "Day 3", 4, "8-12", "Dumbbells", "Back"),
            createWorkout("Tripod Dumbbell Row", "Back unilateral", "Day 3", 4, "8-12", "Dumbbells", "Back"),
            createWorkout("Dumbbell Pullover", "Back and chest", "Day 3", 3, "8-12", "Dumbbells", "Back, Chest"),
            createWorkout("Reverse Grip Dumbbell Row", "Back variation", "Day 3", 4, "8-12", "Dumbbells", "Back"),
            createWorkout("Dumbbell Bicep Curl", "Bicep isolation", "Day 3", 3, "10-15", "Dumbbells", "Biceps"),
            createWorkout("Dumbbell Hammer Curl", "Bicep variation", "Day 3", 3, "10-15", "Dumbbells", "Biceps"),

            // Day 4: Legs & Core (Second Leg Day)
            createWorkout("Dumbbell Squat", "Primary leg exercise", "Day 4", 4, "8-10", "Dumbbells", "Legs"),
            createWorkout("Dumbbell Deadlift", "Full body compound", "Day 4", 4, "8-10", "Dumbbells", "Legs, Back"),
            createWorkout("Dumbbell Split Squat", "Leg unilateral", "Day 4", 3, "8-12", "Dumbbells", "Legs"),
            createWorkout("Dumbbell Hip Thrust", "Glute focus", "Day 4", 4, "10-15", "Dumbbells", "Legs, Glutes"),
            createWorkout("Dumbbell Calf Raise", "Calf isolation", "Day 4", 4, "20", "Dumbbells", "Calves"),
            createWorkout("Dumbbell Side Bends", "Core oblique work", "Day 4", 3, "15 Each", "Dumbbells", "Core"),
            createWorkout("Plank", "Core stability", "Day 4", 3, "20 Secs", "Bodyweight", "Core"),

            // Day 5: Complete Upper Body
            createWorkout("One Arm Dumbbell Rows", "Back unilateral", "Day 5", 4, "8-10 Each", "Dumbbells", "Back"),
            createWorkout("Dumbbell Arnold Press", "Shoulder compound", "Day 5", 4, "8-10", "Dumbbells", "Shoulders"),
            createWorkout("Incline Dumbbell Bench Press", "Upper chest", "Day 5", 4, "8-12", "Dumbbells", "Chest"),
            createWorkout("Chest Supported Dumbbell Row", "Back isolation", "Day 5", 3, "8-12", "Dumbbells", "Back"),
            createWorkout("Dumbbell Pinwheel Curl", "Bicep variation", "Day 5", 2, "8-12", "Dumbbells", "Biceps"),
            createWorkout("Overhead Dumbbell Tricep Extension", "Tricep isolation", "Day 5", 3, "8-12", "Dumbbells", "Triceps"),
            createWorkout("Dumbbell Shrug", "Trap isolation", "Day 5", 3, "12-15", "Dumbbells", "Traps")
        );

        workoutRepository.saveAll(workouts);
        System.out.println("Initialized " + workouts.size() + " workout exercises from 5-Day Dumbbell Split!");
    }

    private Workout createWorkout(String exerciseName, String description, String workoutDay, 
                                 Integer sets, String reps, String equipment, String muscleGroups) {
        Workout workout = new Workout();
        workout.setExerciseName(exerciseName);
        workout.setDescription(description);
        workout.setWorkoutDay(workoutDay);
        workout.setSets(sets);
        workout.setReps(reps);
        workout.setEquipment(equipment);
        workout.setMuscleGroups(muscleGroups);
        workout.setStatus(WorkoutStatus.PLANNED);
        workout.setPriority(WorkoutPriority.MEDIUM);
        workout.setDueDate(LocalDate.now().plusDays(1)); // Set due date to tomorrow
        return workout;
    }
}
