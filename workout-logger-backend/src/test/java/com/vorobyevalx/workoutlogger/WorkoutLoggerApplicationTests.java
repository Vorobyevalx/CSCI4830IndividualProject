package com.vorobyevalx.workoutlogger;

import com.vorobyevalx.workoutlogger.model.Workout;
import com.vorobyevalx.workoutlogger.model.WorkoutPriority;
import com.vorobyevalx.workoutlogger.model.WorkoutStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class WorkoutLoggerApplicationTests {

    @Test
    void contextLoads() {
        // Test that Spring Boot context loads successfully
    }

    @Test
    void testWorkoutEntity() {
        // Test creating a Workout entity
        Workout workout = new Workout();
        workout.setExerciseName("Morning Run");
        workout.setDescription("5K run in the park");
        workout.setDueDate(LocalDate.now().plusDays(1));
        workout.setStatus(WorkoutStatus.PLANNED);
        workout.setPriority(WorkoutPriority.HIGH);

        // Verify the workout was created correctly
        assert workout.getExerciseName().equals("Morning Run");
        assert workout.getDescription().equals("5K run in the park");
        assert workout.getStatus() == WorkoutStatus.PLANNED;
        assert workout.getPriority() == WorkoutPriority.HIGH;
        
        System.out.println("Workout created successfully: " + workout);
    }
}
