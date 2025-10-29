import { useState, useEffect } from 'react'
import axios from 'axios'
import './App.css'

// API base URL
const API_BASE_URL = 'http://localhost:8080/api/workouts'

function App() {
  const [workouts, setWorkouts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [editingWorkout, setEditingWorkout] = useState(null)
  const [openDays, setOpenDays] = useState(new Set()) //collapsed by default
  const [formData, setFormData] = useState({
    exerciseName: '',
    description: '',
    dueDate: '',
    status: 'PLANNED',
    priority: 'MEDIUM',
    workoutDay: '',
    sets: '',
    reps: '',
    equipment: '',
    muscleGroups: '',
    actualWeight: '',
    completedReps: 0,
    completedSets: 0
  })

  const [flippedCards, setFlippedCards] = useState(new Set())

  // Fetch workouts from API
  const fetchWorkouts = async () => {
    try {
      setLoading(true)
      const response = await axios.get(API_BASE_URL)
      setWorkouts(response.data)
      setError(null)
    } catch (err) {
      setError('Failed to fetch workouts: ' + err.message)
      console.error('Error fetching workouts:', err)
    } finally {
      setLoading(false)
    }
  }

  // Load workouts on component mount
  useEffect(() => {
    fetchWorkouts()
  }, [])

  // Handle form input changes
  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  // Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      if (editingWorkout) {
        // Update existing workout
        await axios.put(`${API_BASE_URL}/${editingWorkout.id}`, formData)
      } else {
        // Create new workout
        await axios.post(API_BASE_URL, formData)
      }
      
      // Reset form and refresh workouts
      setFormData({
        exerciseName: '',
        description: '',
        dueDate: '',
        status: 'PLANNED',
        priority: 'MEDIUM',
        workoutDay: '', 
        sets: '',
        reps: '',
        equipment: '',
        muscleGroups: ''
      })
      setShowForm(false)
      setEditingWorkout(null)
      fetchWorkouts()
    } catch (err) {
      setError('Failed to save workout: ' + err.message)
      console.error('Error saving workout:', err)
    }
  }

  // Handle edit workout
  const handleEdit = (workout) => {
    setEditingWorkout(workout)
    setFormData({
      exerciseName: workout.exerciseName,
      description: workout.description || '',
      dueDate: workout.dueDate || '',
      status: workout.status,
      priority: workout.priority,
      workoutDay: workout.workoutDay || '',
      sets: workout.sets || '',
      reps: workout.reps || '',
      equipment: workout.equipment || '',
      muscleGroups: workout.muscleGroups || ''
    })
    setShowForm(true)
  }

  // Handle delete workout
  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this workout?')) {
      try {
        await axios.delete(`${API_BASE_URL}/${id}`)
        fetchWorkouts()
      } catch (err) {
        setError('Failed to delete workout: ' + err.message)
        console.error('Error deleting workout:', err)
      }
    }
  }

  // Handle cancel form
  const handleCancel = () => {
    setShowForm(false)
    setEditingWorkout(null)
    setFormData({
      exerciseName: '',
      description: '',
      dueDate: '',
      status: 'PLANNED',
      priority: 'MEDIUM',
      workoutDay: '',
      sets: '',
      reps: '',
      equipment: '',
      muscleGroups: ''
    })
  }

  // Get priority color
  const getPriorityColor = (priority) => {
    switch (priority) {
      case 'HIGH': return '#ff4444'
      case 'MEDIUM': return '#ffaa00'
      case 'LOW': return '#44aa44'
      default: return '#666666'
    }
  }

  // Group workouts by workout day
  const groupWorkoutsByDay = (workouts) => {
    const grouped = workouts.reduce((acc, workout) => {
      const day = workout.workoutDay || 'Other';
      if (!acc[day]) {
        acc[day] = [];
      }
      acc[day].push(workout);
      return acc;
    }, {});
    
    // Sort days in order
    const dayOrder = ['Day 1', 'Day 2', 'Day 3', 'Day 4', 'Day 5', 'Other'];
    const sortedGrouped = {};
    dayOrder.forEach(day => {
      if (grouped[day]) {
        sortedGrouped[day] = grouped[day];
      }
    });
    
    return sortedGrouped;
  };

  // Get day title based on workout day
  const getDayTitle = (day) => {
    const dayTitles = {
      'Day 1': 'Day 1: Chest, Shoulders & Triceps',
      'Day 2': 'Day 2: Legs & Core',
      'Day 3': 'Day 3: Back & Biceps',
      'Day 4': 'Day 4: Legs & Core',
      'Day 5': 'Day 5: Complete Upper Body',
      'Other': 'Other Workouts'
    };
    return dayTitles[day] || day;
  };

  // Toggle Day section open/closed
  const toggleDay = (day) => {
    const next = new Set(openDays)
    if (next.has(day)) {
      next.delete(day)
    } else {
      next.add(day)
    }
    setOpenDays(next)
  }

  // Get color for status badge
  const getStatusColor = (status) => {
    const colors = {
      'PLANNED': '#6c757d',     // Gray
      'IN_PROGRESS': '#007bff',  // Blue
      'COMPLETED': '#28a745'     // Green
    };
    return colors[status] || '#6c757d'; // Default gray
  };

  // Flip card functionality
  const toggleCardFlip = (workoutId) => {
    const newFlippedCards = new Set(flippedCards);
    if (newFlippedCards.has(workoutId)) {
      newFlippedCards.delete(workoutId);
    } else {
      newFlippedCards.add(workoutId);
    }
    setFlippedCards(newFlippedCards);
  };

  // Update rep counter
  const updateReps = async (workoutId, increment) => {
    const workout = workouts.find(w => w.id === workoutId);
    if (!workout) return;

    const newCompletedReps = Math.max(0, (workout.completedReps || 0) + increment);
    
    try {
      const updatedWorkout = {
        ...workout,
        completedReps: newCompletedReps
      };
      
      const response = await axios.put(`${API_BASE_URL}/${workoutId}`, updatedWorkout);
      
      setWorkouts(workouts.map(w => 
        w.id === workoutId ? response.data : w
      ));
    } catch (error) {
      console.error('Failed to update reps:', error);
    }
  };

  // Update weight
  const updateWeight = async (workoutId, newWeight) => {
    const workout = workouts.find(w => w.id === workoutId);
    if (!workout) return;

    try {
      const updatedWorkout = {
        ...workout,
        actualWeight: parseFloat(newWeight) || 0
      };
      
      const response = await axios.put(`${API_BASE_URL}/${workoutId}`, updatedWorkout);
      
      setWorkouts(workouts.map(w => 
        w.id === workoutId ? response.data : w
      ));
    } catch (error) {
      console.error('Failed to update weight:', error);
    }
  };

  // Update completed sets
  const updateSets = async (workoutId, increment) => {
    const workout = workouts.find(w => w.id === workoutId);
    if (!workout) return;

    const newCompletedSets = Math.max(0, Math.min(workout.sets || 0, (workout.completedSets || 0) + increment));
    
    try {
      const updatedWorkout = {
        ...workout,
        completedSets: newCompletedSets,
        // Reset reps to 0 when completing a new set (increment > 0)
        completedReps: increment > 0 ? 0 : workout.completedReps
      };
      
      const response = await axios.put(`${API_BASE_URL}/${workoutId}`, updatedWorkout);
      
      setWorkouts(workouts.map(w => 
        w.id === workoutId ? response.data : w
      ));
    } catch (error) {
      console.error('Failed to update sets:', error);
    }
  };

  if (loading) {
  return (
      <div className="app">
        <div className="loading">Loading workouts...</div>
      </div>
    )
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>🏋️ Workout Logger</h1>
        <button 
          className="btn btn-primary"
          onClick={() => setShowForm(true)}
        >
          + Add Workout
        </button>
      </header>

      {error && (
        <div className="error-message">
          {error}
          <button onClick={() => setError(null)}>×</button>
        </div>
      )}

      {showForm && (
        <div className="form-overlay">
          <div className="form-container">
            <h2>{editingWorkout ? 'Edit Workout' : 'Add New Workout'}</h2>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="exerciseName">Exercise Name *</label>
                <input
                  type="text"
                  id="exerciseName"
                  name="exerciseName"
                  value={formData.exerciseName}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="description">Description</label>
                <textarea
                  id="description"
                  name="description"
                  value={formData.description}
                  onChange={handleInputChange}
                  rows="3"
                />
              </div>

              <div className="form-group">
                <label htmlFor="dueDate">Due Date</label>
                <input
                  type="date"
                  id="dueDate"
                  name="dueDate"
                  value={formData.dueDate}
                  onChange={handleInputChange}
                />
              </div>

              <div className="form-group">
                <label htmlFor="status">Status</label>
                <select
                  id="status"
                  name="status"
                  value={formData.status}
                  onChange={handleInputChange}
                >
                  <option value="PLANNED">Planned</option>
                  <option value="IN_PROGRESS">In Progress</option>
                  <option value="COMPLETED">Completed</option>
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="priority">Priority</label>
                <select
                  id="priority"
                  name="priority"
                  value={formData.priority}
                  onChange={handleInputChange}
                >
                  <option value="LOW">Low</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HIGH">High</option>
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="workoutDay">Workout Day</label>
                <select
                  id="workoutDay"
                  name="workoutDay"
                  value={formData.workoutDay}
                  onChange={handleInputChange}
                >
                  <option value="">Select Day</option>
                  <option value="Day 1">Day 1: Chest, Shoulders & Triceps</option>
                  <option value="Day 2">Day 2: Legs & Core</option>
                  <option value="Day 3">Day 3: Back & Biceps</option>
                  <option value="Day 4">Day 4: Legs & Core</option>
                  <option value="Day 5">Day 5: Complete Upper Body</option>
                  <option value="Other">Other</option>
                </select>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="sets">Sets</label>
                  <input
                    type="number"
                    id="sets"
                    name="sets"
                    value={formData.sets}
                    onChange={handleInputChange}
                    min="1"
                    max="20"
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="reps">Reps</label>
                  <input
                    type="text"
                    id="reps"
                    name="reps"
                    value={formData.reps}
                    onChange={handleInputChange}
                    placeholder="e.g., 8-10, 12-15"
                  />
                </div>
                    )}
              </div>

              <div className="form-group">
                <label htmlFor="equipment">Equipment</label>
                <input
                  type="text"
                  id="equipment"
                  name="equipment"
                  value={formData.equipment}
                  onChange={handleInputChange}
                  placeholder="e.g., Dumbbells, Bodyweight"
                />
              </div>

              <div className="form-group">
                <label htmlFor="muscleGroups">Muscle Groups</label>
                <input
                  type="text"
                  id="muscleGroups"
                  name="muscleGroups"
                  value={formData.muscleGroups}
                  onChange={handleInputChange}
                  placeholder="e.g., Chest, Shoulders, Triceps"
                />
              </div>

              <div className="form-actions">
                <button type="submit" className="btn btn-primary">
                  {editingWorkout ? 'Update' : 'Add'} Workout
                </button>
                <button type="button" className="btn btn-secondary" onClick={handleCancel}>
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <main className="workouts-container">
        {workouts.length === 0 ? (
          <div className="no-workouts">
            <p>No workouts found. Add your first workout to get started!</p>
          </div>
        ) : (
          <div className="workout-program">
            <div className="program-header">
              <h2>5-Day Dumbbell Workout Split</h2>
              <p className="program-description">
                A comprehensive 5-day dumbbell-only workout program perfect for building lean muscle mass at home or on the go. 
                Each day focuses on specific muscle groups with progressive overload principles.
        </p>
      </div>
            
                {Object.entries(groupWorkoutsByDay(workouts)).map(([day, dayWorkouts]) => (
                  <div key={day} className="workout-day-section">
                    <button
                      type="button"
                      className={`day-toggle ${openDays.has(day) ? 'open' : ''}`}
                      onClick={() => toggleDay(day)}
                      aria-expanded={openDays.has(day)}
                      aria-controls={`day-${day}`}
                    >
                      <span className="day-toggle-title">{getDayTitle(day)}</span>
                      <span className="day-toggle-icon" aria-hidden>
                        {openDays.has(day) ? '▾' : '▸'}
                      </span>
                    </button>
                    {openDays.has(day) && (
                      <div id={`day-${day}`} className="workouts-grid">
                  {dayWorkouts.map(workout => (
                    <div 
                      key={workout.id} 
                      className={`workout-card ${flippedCards.has(workout.id) ? 'flipped' : ''}`}
                      onClick={() => toggleCardFlip(workout.id)}
                    >
                      {/* Front of card */}
                      <div className="card-front">
                        <div className="workout-header">
                          <h4>{workout.exerciseName}</h4>
                          <div className="workout-badges">
                            <span 
                              className="priority-badge"
                              style={{ backgroundColor: getPriorityColor(workout.priority) }}
                            >
                              {workout.priority}
                            </span>
                            <span 
                              className="status-badge"
                              style={{ backgroundColor: getStatusColor(workout.status) }}
                            >
                              {workout.status.replace('_', ' ')}
                            </span>
                          </div>
                        </div>
                        
                        {workout.description && (
                          <p className="workout-description">{workout.description}</p>
                        )}
                        
                        <div className="workout-details">
                          {workout.sets && workout.reps && (
                            <div className="detail-row">
                              <span className="detail-label">Sets & Reps:</span>
                              <span className="detail-value">{workout.sets} x {workout.reps}</span>
                            </div>
                          )}
                          {workout.equipment && (
                            <div className="detail-row">
                              <span className="detail-label">Equipment:</span>
                              <span className="detail-value">{workout.equipment}</span>
                            </div>
                          )}
                          {workout.muscleGroups && (
                            <div className="detail-row">
                              <span className="detail-label">Muscle Groups:</span>
                              <span className="detail-value">{workout.muscleGroups}</span>
                            </div>
                          )}
                          {workout.dueDate && (
                            <div className="detail-row">
                              <span className="detail-label">Due Date:</span>
                              <span className="detail-value">{new Date(workout.dueDate).toLocaleDateString()}</span>
                            </div>
                          )}
                        </div>
                        
                        <div className="workout-actions">
                          <button 
                            className="btn btn-small btn-primary"
                            onClick={(e) => {
                              e.stopPropagation();
                              handleEdit(workout);
                            }}
                          >
                            Edit
                          </button>
                          <button 
                            className="btn btn-small btn-danger"
                            onClick={(e) => {
                              e.stopPropagation();
                              handleDelete(workout.id);
                            }}
                          >
                            Delete
                          </button>
                        </div>
                        
                        <div className="flip-hint">
                          <span>Click to flip for workout tracking</span>
                        </div>
                      </div>

                      {/* Back of card - Workout tracking */}
                      <div className="card-back">
                        <div className="tracking-header">
                          <h4>{workout.exerciseName}</h4>
                          <div className="flip-back-hint">
                            <span>Click to flip back</span>
                          </div>
                        </div>
                        
                        <div className="tracking-content">
                          {/* Weight tracking */}
                          <div className="tracking-section">
                            <label className="tracking-label">Weight (lbs)</label>
                            <input
                              type="number"
                              className="weight-input"
                              value={workout.actualWeight || ''}
                              onChange={(e) => {
                                e.stopPropagation();
                                updateWeight(workout.id, e.target.value);
                              }}
                              onClick={(e) => e.stopPropagation()}
                              onFocus={(e) => e.stopPropagation()}
                              placeholder="0"
                              step="0.5"
                              min="0"
                            />
                          </div>

                          {/* Sets tracking */}
                          <div className="tracking-section">
                            <label className="tracking-label">Sets Completed</label>
                            <div className="counter-controls">
                              <button 
                                className="counter-btn minus"
                                onClick={(e) => {
                                  e.stopPropagation();
                                  updateSets(workout.id, -1);
                                }}
                              >
                                -
                              </button>
                              <span className="counter-value">
                                {workout.completedSets || 0} / {workout.sets || 0}
                              </span>
                              <button 
                                className="counter-btn plus"
                                onClick={(e) => {
                                  e.stopPropagation();
                                  updateSets(workout.id, 1);
                                }}
                              >
                                +
                              </button>
                            </div>
                          </div>

                          {/* Reps tracking */}
                          <div className="tracking-section">
                            <label className="tracking-label">Reps Completed</label>
                            <div className="counter-controls">
                              <button 
                                className="counter-btn minus"
                                onClick={(e) => {
                                  e.stopPropagation();
                                  updateReps(workout.id, -1);
                                }}
                              >
                                -
                              </button>
                              <span className="counter-value">
                                {workout.completedReps || 0}
                              </span>
                              <button 
                                className="counter-btn plus"
                                onClick={(e) => {
                                  e.stopPropagation();
                                  updateReps(workout.id, 1);
                                }}
                              >
                                +
                              </button>
                            </div>
                          </div>

                          {/* Progress indicator */}
                          <div className="progress-section">
                            <div className="progress-bar">
                              <div 
                                className="progress-fill"
                                style={{ 
                                  width: `${workout.sets ? ((workout.completedSets || 0) / workout.sets) * 100 : 0}%` 
                                }}
                              ></div>
                            </div>
                            <span className="progress-text">
                              {workout.sets ? Math.round(((workout.completedSets || 0) / workout.sets) * 100) : 0}% Complete
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
                    )}
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  )
}

export default App