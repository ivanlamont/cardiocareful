# Cardio Careful

A professional-grade heart rate monitoring app for Wear OS smartwatches that provides haptic feedback alerts when your heart rate enters specific zones.

## Features

### Core Monitoring
- **Real-time Heart Rate Tracking**: Reads heart rate data from your watch's sensor
- **Customizable Alerts**: Set min/max heart rate thresholds
- **Haptic Feedback**: Feel vibration patterns when alert thresholds are crossed
- **Background Monitoring**: Continues monitoring even with the app backgrounded

### User Customization
- **Configurable Thresholds**: Set personalized min/max heart rate ranges
- **Multiple Haptic Patterns**: 6 different vibration patterns to choose from
- **Alert Cooldown**: Prevent alert fatigue with customizable cooldown periods
- **Notification Support**: Optional Android notifications alongside haptic feedback

### Profile Management
- **Quick Profiles**: Pre-built profiles for Cardio, Recovery, and Warm-up
- **Custom Profiles**: Create your own profiles for different activities
- **One-Click Switching**: Instantly switch between profiles
- **Persistent Storage**: Profiles saved automatically

### Data & History
- **Heart Rate Logging**: All readings stored locally with timestamps
- **Statistical Analysis**: View min/max/average HR statistics
- **Alert Tracking**: See which readings triggered alerts
- **Data Retention**: Automatic cleanup of old data

### Advanced Features
- **Battery Aware**: Respects battery saver mode
- **Smart Cooldown**: Prevents alert spam with configurable intervals
- **Wear Data Layer Ready**: Framework for companion app integration
- **Smooth Animations**: Polished UI with smooth transitions
- **Haptic Feedback**: Multiple feedback patterns for user interactions

## Quick Start

### Prerequisites
- Wear OS 11+ smartwatch (API 30+)
- Watch with heart rate sensor
- Android phone with Android 12+ (for companion features)

### Installation

1. **Build the app**:
   ```bash
   ./gradlew assembleDebug
   ```

2. **Install on watch**:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

3. **Launch the app**:
   - Find "Cardio Careful" on your watch
   - Grant BODY_SENSORS permission when prompted
   - Start monitoring

### First Use

1. **Set Alert Thresholds**:
   - Open Settings on the app
   - Set Min and Max heart rate values (20-220 bpm)
   - Save settings

2. **Choose Haptic Pattern**:
   - Go to Haptic Patterns
   - Select your preferred vibration pattern
   - Use Preview to test

3. **Start Monitoring**:
   - Press START to begin monitoring
   - You'll see real-time heart rate
   - Haptic feedback when thresholds are crossed

## Architecture

### Project Structure

```
app/src/main/java/com/explorova/cardiocareful/
├── MainActivity.kt                    # Entry point
├── MainApplication.kt                 # App initialization
├── data/                             # Data layer
│   ├── HealthServicesRepository      # HR sensor interface
│   ├── UserPreferences               # User settings (DataStore)
│   ├── HeartRateRepository           # Historical data access
│   ├── ProfileRepository             # Profile management
│   ├── CardioDatabase                # Room database
│   └── HeartRateReading*             # Data models
├── engine/                           # Business logic
│   ├── MonitorData                   # Core monitoring engine
│   ├── Notification                  # Alert conditions
│   ├── AlertPattern                  # Vibration patterns
│   └── HapticPatterns                # Pattern definitions
├── presentation/                     # UI layer
│   ├── CardioDataViewModel           # UI state management
│   ├── Haptics                       # Vibration control
│   ├── *Screen.kt                    # Compose UI screens
│   ├── Animations.kt                 # UI animations
│   └── HapticFeedback.kt            # User feedback
├── background/                       # Background tasks
│   └── HeartRateMonitorWorker        # WorkManager integration
└── companion/                        # Companion app support
    └── CompanionDataSync             # Phone-watch sync
```

### Data Flow

```
Health Services API
        ↓
HealthServicesRepository
        ↓
CardioDataViewModel ↔ HeartRateRepository (Room DB)
        ↓
MonitorData (Monitoring Engine)
        ├→ Alert Checking
        ├→ Haptics.sendVibration()
        ├→ NotificationManager
        └→ UserPreferences
```

### Key Classes

- **MonitorData**: Core monitoring logic, checks heart rate against alert conditions
- **Notification**: Defines alert rules (min/max HR, cooldown)
- **AlertPattern**: Vibration timing and amplitude configuration
- **CardioDataViewModel**: MVVM view state management
- **HeartRateRepository**: Database access for historical data
- **ProfileRepository**: Alert profile management

## Configuration

### Settings Available

- **Min Heart Rate**: Alert if HR falls below this (20-220 bpm)
- **Max Heart Rate**: Alert if HR exceeds this (20-220 bpm)
- **Haptic Pattern**: Choose from 6 predefined patterns
- **Alert Cooldown**: Seconds between consecutive alerts (5-300)
- **Notifications**: Enable/disable system notifications
- **Status Notifications**: Show continuous HR status
- **Alert Profiles**: Multiple pre-configured scenarios

### Haptic Patterns

1. **Short Tap** - Quick feedback for normal conditions
2. **Long Vibration** - Sustained feedback for alerts
3. **Pulse** - Rhythmic pattern for cadence
4. **Double Tap** - Two-part feedback
5. **Wave** - Gradual intensity increase
6. **Emergency** - Urgent alert pattern

## Testing

### Unit Tests

```bash
./gradlew test
```

Tests included for:
- Alert pattern validation
- Notification condition checking
- Haptic pattern generation
- User preference validation
- Profile management

### Manual Testing

**On-Device Testing**:
1. Install app on watch
2. Monitor heart rate in real conditions
3. Verify haptic feedback at thresholds
4. Test profile switching mid-workout
5. Check background monitoring with screen off

**Emulator Testing**:
1. Use Android Wear OS emulator
2. Use mock heart rate values
3. Verify UI and animations
4. Test preference persistence

## Troubleshooting

### App Won't Start
- Check permissions (BODY_SENSORS required)
- Ensure Wear OS 11+
- Try reinstalling: `adb install -r app-debug.apk`

### No Heart Rate Detected
- Confirm watch has HR sensor
- Check if HR feature is enabled in watch settings
- Ensure proper watch fit and contact
- Try "Acquiring..." screen for 30 seconds

### Haptic Feedback Not Working
- Verify vibration is enabled on watch
- Check Haptics settings in app
- Ensure VIBRATE permission is granted
- Test with different haptic patterns

### Battery Drains Quickly
- Reduce status notification frequency
- Increase alert cooldown period
- Disable background monitoring if not needed
- Check if watch is in high power mode

## Development

### Building from Source

**Requirements**:
- Android Studio Giraffe+
- Gradle 8.0+
- Java 17+
- Android SDK API 34

**Setup**:
```bash
git clone https://github.com/explorova/cardiocareful.git
cd cardiocareful
./gradlew build
```

**Code Style**:
- Follows Kotlin official style guide
- Uses KtLint for formatting
- Auto-formatted on build

### Dependencies

- **Jetpack Compose**: UI framework
- **Room**: Local database
- **DataStore**: User preferences
- **WorkManager**: Background tasks
- **Health Services API**: Heart rate sensor access
- **Wear OS Material**: Wear-specific UI components

## Documentation

- [Architecture Guide](ARCHITECTURE.md) - Detailed system design
- [Companion App Guide](COMPANION_APP_GUIDE.md) - Building phone app companion

## License

MIT License

## Version

- **Version**: 1.0.0
- **Build**: Android Wear OS
- **Minimum SDK**: 30 (Android 11)
- **Target SDK**: 33 (Android 13)

---

**Status**: Production Ready
