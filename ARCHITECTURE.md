# Cardio Careful - Architecture Guide

This document describes the architectural design and implementation of the Cardio Careful heart rate monitoring application.

## Table of Contents

1. [Overview](#overview)
2. [Layer Architecture](#layer-architecture)
3. [Core Components](#core-components)
4. [Data Flow](#data-flow)
5. [State Management](#state-management)
6. [Database Schema](#database-schema)
7. [Background Monitoring](#background-monitoring)
8. [Design Patterns](#design-patterns)

## Overview

Cardio Careful follows a **layered MVVM (Model-View-ViewModel) architecture** with clear separation of concerns:

```
┌─────────────────────────┐
│   Presentation Layer    │ (UI - Compose)
│  CardioDataViewModel    │
│  Screens, Animations    │
└────────────┬────────────┘
             │
┌────────────▼────────────┐
│   Business Logic Layer  │ (Engine)
│  MonitorData            │
│  Notification Logic     │
│  Alert Processing      │
└────────────┬────────────┘
             │
┌────────────▼────────────┐
│    Repository Layer     │ (Data Access)
│  HeartRateRepository    │
│  ProfileRepository      │
│  UserPreferences        │
└────────────┬────────────┘
             │
┌────────────▼────────────┐
│     Data Layer          │ (Database)
│  Room Database          │
│  DataStore              │
│  Health Services API    │
└─────────────────────────┘
```

## Layer Architecture

### 1. **Presentation Layer** (`presentation/`)

Responsible for UI rendering and user interaction.

**Components:**
- `CardioDataViewModel` - Manages UI state and communicates with repositories
- `*Screen.kt` - Composable screens (MainScreen, SettingsScreen, etc.)
- `Animations.kt` - UI animation utilities
- `HapticFeedback.kt` - User interaction feedback
- `Haptics.kt` - Vibration control

**Key Responsibilities:**
- Display heart rate data
- Show alert status
- Collect user input
- Manage UI state
- Provide visual feedback

**Data Flow:**
```
User Interaction → ViewModel → Repository → UI Update
```

### 2. **Business Logic Layer** (`engine/`)

Core monitoring and alert processing logic.

**Components:**
- `MonitorData` - Main monitoring engine
- `Notification` - Alert condition definitions
- `AlertPattern` - Vibration pattern specifications
- `HapticPatterns` - Pre-defined patterns library

**Key Responsibilities:**
- Listen to heart rate updates
- Check alert conditions
- Trigger haptic feedback
- Manage cooldown periods
- Log readings to database

**Alert Decision Tree:**
```
Heart Rate Reading
    ├─ Check Max Threshold
    │  └─ If exceeded → Alert
    ├─ Check Min Threshold
    │  └─ If below → Alert
    ├─ Check Cooldown
    │  └─ If in cooldown → Skip
    └─ Trigger Haptics & Notifications
```

### 3. **Repository Layer** (`data/`)

Abstracts data access and provides clean interfaces.

**Components:**
- `HeartRateRepository` - Accesses historical heart rate data
- `ProfileRepository` - Manages alert profiles
- `UserPreferences` - User settings management
- `HealthServicesRepository` - Heart rate sensor bridge

**Repositories Implement:**
- CRUD operations
- Query operations
- Data validation
- Error handling
- Logging

### 4. **Data Layer** (`data/`)

Direct database and sensor access.

**Components:**
- `CardioDatabase` - Room database instance
- `HeartRateReadingDao` - Database queries for readings
- `AlertProfileDao` - Database queries for profiles
- `HealthServicesRepository` - Health Services API integration

## Core Components

### MonitorData - The Heart of the App

```kotlin
class MonitorData(
    healthServicesRepository: HealthServicesRepository,
    context: Context,
    userPreferences: UserPreferences? = null,
    heartRateRepository: HeartRateRepository? = null
)
```

**Lifecycle:**
1. Initialize with repositories
2. Subscribe to heart rate flow
3. Watch for preference changes
4. On each reading:
   - Log to database
   - Check alert conditions
   - Trigger notifications if active

**Key Methods:**
- `loadNotifications()` - Load active alert rules
- `checkData(notifications, currentHeartRate)` - Process readings
- Alert preference watching
- Preference change handling

### Notification - Alert Rules Engine

```kotlin
class Notification(
    name: String,
    alertPattern: AlertPattern,
    minRate: Int? = null,
    maxRate: Int? = null,
    repeatIntervalSeconds: Int? = null
)
```

**Purpose:**
- Define alert conditions (thresholds)
- Track alert state
- Manage cooldown timing
- Return vibration patterns

**State Machine:**
```
Cold (No Alert)
    ↓
    └─→ checkConditions(HR) → True
        ↓
        └─→ hot() [Set timestamp]
            ↓
            └─→ Check Cooldown
                ├─ Too Soon? → Return False (Skip)
                └─ OK? → Return True (Alert)

    └─→ checkConditions(HR) → False
        ↓
        └─→ cold() [Clear timestamp] → Return False
```

### CardioDataViewModel - UI State Manager

```kotlin
class CardioDataViewModel(
    healthServicesRepository: HealthServicesRepository,
    userPreferences: UserPreferences? = null
) : ViewModel()
```

**State Properties:**
- `enabled: MutableStateFlow<Boolean>` - Monitoring active?
- `hr: MutableState<Double>` - Current heart rate
- `availability: MutableState<DataTypeAvailability>` - Sensor status
- `minHeartRate, maxHeartRate, hapticPattern, alertCooldown` - Settings

**Responsibilities:**
- Load preferences on init
- Manage monitoring start/stop
- Collect sensor data
- Update UI state
- Provide settings to UI

## Data Flow

### Heart Rate Reading Flow

```
┌──────────────────────────────────────────┐
│  Android Health Services API             │
│  (Sensor Data)                           │
└──────────────────┬───────────────────────┘
                   │
                   ▼
        ┌──────────────────────┐
        │ HealthServicesRepo   │
        │ (callbackFlow)       │
        └──────────┬───────────┘
                   │
          ┌────────▼─────────┐
          │ CardioDataViewModel
          │ (Collect data)   │
          └────────┬─────────┘
                   │
          ┌────────▼──────────────┐
          │  MonitorData          │
          │  checkData()          │
          │  [Core Logic]         │
          └────────┬──────────────┘
                   │
        ┌──────────┼──────────┐
        ▼          ▼          ▼
    Database   Haptics   Notifications
```

### Settings Update Flow

```
User Changes Setting
         │
         ▼
    SettingsScreen
         │
         ▼
    ViewModel.saveSettings()
         │
         ▼
    UserPreferences.set*()
         │
         ▼
    DataStore (Persisted)
         │
         ▼
    MonitorData watches preferences
         │
         ▼
    Updates Notification objects
         │
         ▼
    Next HR reading uses new settings
```

## State Management

### Global State
- **User Settings** (DataStore)
  - Min/Max HR thresholds
  - Haptic pattern selection
  - Cooldown duration
  - Notification preferences

- **Active Profile** (Database + Memory)
  - Currently active alert profile
  - Profile settings loaded into MonitorData

### UI State (ViewModel)
- **Monitoring Status**
  - Enabled/disabled
  - Current heart rate
  - Sensor availability

- **Settings Loaded**
  - All user preferences
  - Displayed in settings screens

### Runtime State (MonitorData)
- **Notifications List**
  - Active alert rules
  - Updated when preferences change

- **Notification State**
  - Last alert timestamp
  - Cooldown tracking
  - Alert conditions evaluation

## Database Schema

### HeartRateReading Table

```sql
CREATE TABLE heart_rate_readings (
    id: Int (PK auto),
    heartRate: Double,
    timestamp: LocalDateTime,
    alertTriggered: Boolean,
    alertType: String?
)
```

**Indexes:**
- timestamp (for range queries)
- alertTriggered (for alert history)

### AlertProfile Table

```sql
CREATE TABLE alert_profiles (
    id: Int (PK auto),
    name: String (unique),
    minHeartRate: Int,
    maxHeartRate: Int,
    hapticPattern: String,
    alertCooldownSeconds: Int,
    notificationsEnabled: Boolean,
    isActive: Boolean,
    createdAt: Long
)
```

**Queries:**
- Get all profiles (ordered by creation)
- Get active profile
- Activate/deactivate profiles
- Statistics queries (min/max/avg HR)

## Background Monitoring

### WorkManager Integration

**Periodic Work Setup:**
```kotlin
val work = PeriodicWorkRequestBuilder<HeartRateMonitorWorker>(
    15, TimeUnit.MINUTES
).setConstraints(
    Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .build()
).build()

WorkManager.getInstance(context).enqueueUniquePeriodicWork(
    "heart_rate_monitor_work",
    ExistingPeriodicWorkPolicy.KEEP,
    work
)
```

**Lifecycle:**
1. Scheduled on app startup
2. Runs every 15 minutes (configurable)
3. Respects battery constraints
4. Auto-stops when battery low
5. Survives app restart

## Design Patterns

### 1. **Repository Pattern**
- Abstracts data sources
- Provides clean interfaces
- Enables testing with mock data

### 2. **MVVM (Model-View-ViewModel)**
- Clear separation of concerns
- ViewModel survives configuration changes
- UI reacts to state changes

### 3. **Observer Pattern (Flows)**
- Reactive data updates
- Automatic UI refresh
- Reduces boilerplate

### 4. **Dependency Injection**
- Constructor injection
- Factory patterns for ViewModels
- Loosely coupled components

### 5. **State Machine**
- Notification alert states (cold/hot)
- Cooldown management
- Predictable state transitions

### 6. **Strategy Pattern**
- AlertPattern switching
- Profile-based configuration
- Haptic pattern selection

## Thread Safety

### Main Thread Operations
- UI updates (Compose)
- ViewModel state changes
- Database queries (via Coroutines)

### Coroutine Usage
```kotlin
viewModelScope.launch {
    // Suspending operations
    userPreferences.setMinHeartRate(value)
}

scope.launch {
    // Background monitoring
    monitoringFlow.collect { reading ->
        checkData(notifications, reading)
    }
}
```

### Database Access
- Room handles thread safety
- Query methods are suspend functions
- Dao methods run on background threads

## Error Handling

### Try-Catch Strategy
- Health Services API failures → Log and continue
- Database errors → Log and skip logging
- Permission errors → Show UI message
- Sensor errors → Display "Unavailable" status

### Graceful Degradation
- Missing HR sensor → Show "Not Supported" screen
- Database failure → Continue with in-memory state
- Preference load failure → Use defaults
- Notification permission denied → Disable notifications

## Performance Considerations

### Optimization Techniques
1. **Lazy initialization** - Repositories created on demand
2. **Flow throttling** - Heart rate updates throttled
3. **Database indexing** - Queries optimized
4. **Cooldown throttling** - Prevents excessive alerts
5. **Vibration buffering** - Combines rapid vibrations

### Memory Management
- ViewModel lifecycle respects app lifecycle
- Flows properly cancelled
- Database connections pooled
- Bitmap/drawable resources optimized

## Testing Strategy

### Unit Tests
- `AlertPatternTest` - Vibration validation
- `NotificationTest` - Alert condition checking
- `HapticPatternsTest` - Pattern definitions
- `UserPreferencesValidationTest` - Input validation

### Integration Tests
- `NotificationIntegrationTest` - Alert workflow

### Manual Testing
- Device testing with real sensors
- Emulator testing with mock data
- Preference persistence testing
- Background monitoring testing

---

**Last Updated**: January 2024
**Version**: 1.0.0
