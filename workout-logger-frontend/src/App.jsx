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
  const [formData, setFormData] = useState({
    exerciseName: '',
    description: '',
    dueDate: '',
    status: 'PLANNED',
    priority: 'MEDIUM'
  })

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
        priority: 'MEDIUM'
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
      priority: workout.priority
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
      priority: 'MEDIUM'
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

  // Get status color
  const getStatusColor = (status) => {
    switch (status) {
      case 'COMPLETED': return '#44aa44'
      case 'IN_PROGRESS': return '#ffaa00'
      case 'PLANNED': return '#666666'
      default: return '#666666'
    }
  }

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
          <div className="workouts-grid">
            {workouts.map(workout => (
              <div key={workout.id} className="workout-card">
                <div className="workout-header">
                  <h3>{workout.exerciseName}</h3>
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
                
                {workout.dueDate && (
                  <p className="workout-date">
                    📅 Due: {new Date(workout.dueDate).toLocaleDateString()}
                  </p>
                )}
                
                <div className="workout-actions">
                  <button 
                    className="btn btn-small btn-primary"
                    onClick={() => handleEdit(workout)}
                  >
                    Edit
                  </button>
                  <button 
                    className="btn btn-small btn-danger"
                    onClick={() => handleDelete(workout.id)}
                  >
                    Delete
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  )
}

export default App