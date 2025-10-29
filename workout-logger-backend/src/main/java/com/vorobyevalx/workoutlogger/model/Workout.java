package com.vorobyevalx.workoutlogger.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

@Entity
@Table(name = "workouts")
public class Workout {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "exercise_name", nullable = false)
    @JsonProperty("exerciseName")
    private String exerciseName;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @Column(name = "due_date")
    @JsonProperty("dueDate")
    private LocalDate dueDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WorkoutStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private WorkoutPriority priority;
    
    // Default constructor
    public Workout() {
        this.status = WorkoutStatus.PLANNED;
        this.priority = WorkoutPriority.MEDIUM;
    }
    
    // Constructor with required fields
    public Workout(String exerciseName, LocalDate dueDate, WorkoutStatus status, WorkoutPriority priority) {
        this.exerciseName = exerciseName;
        this.dueDate = dueDate;
        this.status = status;
        this.priority = priority;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getExerciseName() {
        return exerciseName;
    }
    
    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public WorkoutStatus getStatus() {
        return status;
    }
    
    public void setStatus(WorkoutStatus status) {
        this.status = status;
    }
    
    public WorkoutPriority getPriority() {
        return priority;
    }
    
    public void setPriority(WorkoutPriority priority) {
        this.priority = priority;
    }
    
    @Override
    public String toString() {
        return "Workout{" +
                "id=" + id +
                ", exerciseName='" + exerciseName + '\'' +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", status=" + status +
                ", priority=" + priority +
                '}';
    }
}
