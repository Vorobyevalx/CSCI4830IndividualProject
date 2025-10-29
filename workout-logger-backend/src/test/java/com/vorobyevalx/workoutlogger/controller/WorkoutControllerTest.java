package com.vorobyevalx.workoutlogger.controller;

import com.vorobyevalx.workoutlogger.model.Workout;
import com.vorobyevalx.workoutlogger.model.WorkoutPriority;
import com.vorobyevalx.workoutlogger.model.WorkoutStatus;
import com.vorobyevalx.workoutlogger.repository.WorkoutRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class WorkoutControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Test
    void testCreateWorkout() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String workoutJson = """
            {
                "exerciseName": "Test Workout",
                "description": "Test Description",
                "dueDate": "2025-10-30",
                "status": "PLANNED",
                "priority": "HIGH"
            }
            """;

        mockMvc.perform(post("/api/workouts")
                .contentType("application/json")
                .content(workoutJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exerciseName").value("Test Workout"))
                .andExpect(jsonPath("$.status").value("PLANNED"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void testGetAllWorkouts() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Create a test workout
        Workout workout = new Workout();
        workout.setExerciseName("Test Workout");
        workout.setDescription("Test Description");
        workout.setDueDate(LocalDate.now().plusDays(1));
        workout.setStatus(WorkoutStatus.PLANNED);
        workout.setPriority(WorkoutPriority.MEDIUM);
        workoutRepository.save(workout);

        mockMvc.perform(get("/api/workouts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
