# Cardio Careful - Testing Guide

Comprehensive testing procedures for device compatibility and edge case validation.

## Testing Phases

### Phase 1: Unit & Integration Tests

Run automated tests:
```bash
./gradlew test
```

**Coverage Areas:**
- ✅ Alert pattern validation
- ✅ Notification logic
- ✅ Haptic pattern definitions
- ✅ User preference validation
- ✅ Profile management

**Expected Results:**
- All tests pass (100%)
- Code coverage > 70% on core logic
- No warnings or errors

---

## Device Compatibility Testing

### Supported Configurations

#### Wear OS Devices

| Device | OS Version | Tested | Status |
|--------|-----------|--------|--------|
| Galaxy Watch 4/5 | 13 | ✓ | Fully supported |
| Pixel Watch | 13 | ✓ | Fully supported |
| TicWatch | 12/13 | ✓ | Fully supported |
| WearOS Emulator | 11-13 | ✓ | Fully supported |

#### Requirements Checklist

- [ ] API Level 30+ (Wear OS 11+)
- [ ] Heart rate sensor present
- [ ] Vibration capability
- [ ] 512 MB+ RAM
- [ ] 50 MB+ storage available
- [ ] Bluetooth 5.0+ (for companion connectivity)

### Device Type Testing

#### Small Round Watches (280x280)
- [ ] UI elements fit without scrolling
- [ ] Buttons are easily tappable (48dp minimum)
- [ ] Text is readable at arm's length
- [ ] Heart rate display is prominent

#### Large Round Watches (454x454)
- [ ] No excessive padding
- [ ] Good use of screen space
- [ ] Proportional spacing maintained
- [ ] All content visible without scrolling

#### Rectangular Watches
- [ ] Layout adapts to aspect ratio
- [ ] Buttons remain centered
- [ ] Text wrapping handled correctly
- [ ] No clipped UI elements

---

## Edge Case Testing

### Heart Rate Scenarios

#### Valid HR Readings
- [ ] 40 bpm (low rest rate)
- [ ] 60 bpm (normal rest)
- [ ] 100 bpm (light activity)
- [ ] 150 bpm (moderate intensity)
- [ ] 180+ bpm (high intensity)

#### Invalid/Edge HR Values
- [ ] 0 bpm (sensor not working)
- [ ] 1-10 bpm (unrealistic low)
- [ ] 220+ bpm (unrealistic high)
- [ ] Negative values (should be filtered)
- [ ] NULL/empty readings (handle gracefully)

#### HR Fluctuations
- [ ] Rapid increase (e.g., 60→140 bpm)
- [ ] Rapid decrease (e.g., 140→60 bpm)
- [ ] Bouncing (60→70→60→65 bpm)
- [ ] Plateau (stable at one value)
- [ ] Gradual increase/decrease

**Expected Behavior:**
- Smooth animations (no jumps)
- Accurate threshold detection
- Proper cooldown activation
- Correct alert triggering

### Threshold Testing

#### Min Threshold Crossings
- [ ] Approach from above (100→40 bpm)
- [ ] Cross and stay below (100→40, stays 35-45 bpm)
- [ ] Cross and return above (100→40→100 bpm)
- [ ] At exact threshold value
- [ ] Just above/just below threshold

#### Max Threshold Crossings
- [ ] Approach from below (80→160 bpm)
- [ ] Cross and stay above (80→160, stays 155-165 bpm)
- [ ] Cross and return below (80→160→80 bpm)
- [ ] At exact threshold value
- [ ] Just above/just below threshold

#### Invalid Thresholds
- [ ] Min = Max (should accept or show warning)
- [ ] Min > Max (should validate and reject)
- [ ] Min < 20 (should reject)
- [ ] Max > 220 (should reject)
- [ ] Non-numeric input (should show error)

### Cooldown Testing

#### Cooldown Behavior
- [ ] First alert triggers immediately
- [ ] Second alert blocked within cooldown period
- [ ] Alert triggers after cooldown expires
- [ ] Cooldown resets on threshold exit
- [ ] Cooldown timer updates correctly

#### Cooldown Edge Cases
- [ ] Cooldown = 1 second (minimum)
- [ ] Cooldown = 300 seconds (maximum)
- [ ] Invalid cooldown values rejected
- [ ] Cooldown state persists if app backgrounded
- [ ] Cooldown resets if device reboots

### Profile Switching

#### Profile Operations
- [ ] Create new profile
- [ ] Edit existing profile
- [ ] Switch between profiles
- [ ] Delete profile (prevent deletion of last profile)
- [ ] Duplicate profile

#### Active Profile Behavior
- [ ] Profile changes take effect immediately
- [ ] Settings are read from active profile
- [ ] Profile persists across app restart
- [ ] Profile deletion removes all associated data
- [ ] Only one profile can be active

### Sensor & Permission Handling

#### Permission Scenarios
- [ ] BODY_SENSORS granted: App works normally
- [ ] BODY_SENSORS denied: Show request again
- [ ] BODY_SENSORS revoked after grant: Show error state
- [ ] VIBRATE permission denied: Notify user
- [ ] POST_NOTIFICATIONS permission denied: Disable notifications

#### Sensor Availability
- [ ] HR sensor available: Read data normally
- [ ] HR sensor not available: Show "Not Supported"
- [ ] HR sensor temporarily unavailable: Show "Acquiring..."
- [ ] Sensor fails mid-monitoring: Graceful fallback
- [ ] Multiple sensor errors: Disable monitoring

### Data Persistence

#### User Preferences
- [ ] Settings saved correctly
- [ ] Settings persist after app restart
- [ ] Settings persist after device restart
- [ ] Invalid settings rejected
- [ ] Default values used if missing

#### Heart Rate History
- [ ] Readings logged with correct timestamp
- [ ] Alert flag set correctly
- [ ] Old data cleaned up (>7 days)
- [ ] Database handles large datasets (1000+ readings)
- [ ] Statistics calculated correctly

#### Profiles
- [ ] Profile created and saved
- [ ] Profile updated correctly
- [ ] Profile deleted completely
- [ ] Active profile indicator persists
- [ ] Created timestamp preserved

### Battery & Power Management

#### Battery States
- [ ] Normal power: App works normally
- [ ] Low battery mode: Monitoring continues
- [ ] Battery critical (<5%): Graceful degradation
- [ ] Battery saver active: Respected by WorkManager
- [ ] Battery charging: No special behavior needed

#### Power Efficiency
- [ ] No excessive wake-locks
- [ ] Background work respects constraints
- [ ] No high-frequency polling
- [ ] Proper resource cleanup
- [ ] Memory usage stable over time

### UI/Animation Edge Cases

#### Animation Performance
- [ ] Animations smooth at 60 FPS
- [ ] No jank when switching screens
- [ ] No memory leaks during animations
- [ ] Animations respect system animation settings
- [ ] Performance stable over extended use

#### Screen Interactions
- [ ] Double-tap on button works
- [ ] Long-press handling if used
- [ ] Rapid clicks don't cause issues
- [ ] Screen timeout handled correctly
- [ ] Always-on mode compatible

### Background Monitoring

#### Background Behavior
- [ ] Monitoring continues when backgrounded
- [ ] Alerts trigger with screen off
- [ ] Notifications appear in background
- [ ] No excessive wake-locks
- [ ] Battery impact acceptable

#### WorkManager Behavior
- [ ] Work scheduled on app startup
- [ ] Work survives app crash
- [ ] Work survives device restart
- [ ] Battery constraints respected
- [ ] Failed work retried appropriately

### Concurrent Operations

#### Multiple Flows
- [ ] HR reading and preference change concurrent
- [ ] Profile switch during monitoring
- [ ] Database read during write
- [ ] UI update while background work running
- [ ] Multiple coroutines without deadlock

#### Configuration Changes
- [ ] App survives rotation (if enabled)
- [ ] ViewModel state preserved
- [ ] Monitoring continues
- [ ] Settings not lost
- [ ] UI refreshes correctly

---

## Stress Testing

### High-Frequency Updates
- [ ] HR updates every second
- [ ] Handle rapid threshold crossings
- [ ] No memory leaks
- [ ] UI remains responsive
- [ ] Alerts not spammed

### Extended Operation
- [ ] Run app for 8+ hours continuously
- [ ] Monitor memory usage
- [ ] Check for resource leaks
- [ ] Verify battery impact
- [ ] Test app restart after extended use

### Large Datasets
- [ ] 1000+ heart rate readings
- [ ] 50+ profiles created
- [ ] Query performance acceptable
- [ ] No database corruption
- [ ] Statistics calculated quickly

---

## Regression Testing Checklist

### After Each Release

- [ ] **Core Functionality**
  - [ ] App launches without crash
  - [ ] Permissions flow works
  - [ ] HR reading functional
  - [ ] Thresholds configurable
  - [ ] Alerts trigger correctly

- [ ] **UI/UX**
  - [ ] All screens accessible
  - [ ] No layout issues
  - [ ] Animations smooth
  - [ ] Buttons responsive
  - [ ] Text readable

- [ ] **Data Integrity**
  - [ ] Settings persisted
  - [ ] Profiles not corrupted
  - [ ] HR history valid
  - [ ] No data loss

- [ ] **Background Operations**
  - [ ] Background monitoring works
  - [ ] WorkManager healthy
  - [ ] Notifications fire
  - [ ] Battery impact acceptable

- [ ] **Performance**
  - [ ] No ANR (Application Not Responding)
  - [ ] UI responsive
  - [ ] Memory stable
  - [ ] Battery reasonable

---

## Device Validation Matrix

| Device | OS | HR Sensor | Round | Size | Tested | Notes |
|--------|-------|-----------|-------|------|--------|-------|
| Galaxy Watch 5 | 13 | ✓ | ✓ | 330x330 | ✓ | Primary test device |
| Pixel Watch | 13 | ✓ | ✓ | 384x384 | ✓ | Supported |
| Emulator | 11-13 | Mock | Varies | 320x320 | ✓ | For development |

---

## Known Device Issues

### Samsung Galaxy Watch
- **Issue**: Haptic feedback very strong
- **Solution**: Reduce amplitude values
- **Status**: Expected behavior

### Wear OS Emulator
- **Issue**: HR sensor needs mocking
- **Solution**: Use test data
- **Status**: Known limitation

### Older Watches (API < 30)
- **Issue**: Not supported
- **Solution**: Requires Wear OS 11+
- **Status**: By design

---

## Testing Checklist Template

Print and use this checklist for manual testing:

```
Device: ________________     Date: ________________
Tester: ________________     Build: ________________

[ ] App installs without errors
[ ] Permissions dialog appears
[ ] HR reading shows values
[ ] Thresholds settable
[ ] Haptic feedback works
[ ] Alerts trigger at threshold
[ ] Profile switching works
[ ] Settings persist after restart
[ ] Background monitoring works
[ ] No crashes during extended use

Issues Found:
_____________________________________________
_____________________________________________

Passed: ☐  Failed: ☐  Issues: __
```

---

## Continuous Integration Testing

### Automated Checks
- Build succeeds
- Unit tests pass
- Code style compliant (KtLint)
- No critical issues (Android Lint)
- APK size acceptable

### Before Release
- All tests pass
- Code review approved
- Device testing on 2+ devices
- No open issues
- Documentation updated

---

## Performance Benchmarks

### Target Metrics

| Metric | Target | Current |
|--------|--------|---------|
| Launch time | < 2s | ___ |
| HR update latency | < 1s | ___ |
| Alert response | < 500ms | ___ |
| Memory usage | < 80MB | ___ |
| Battery drain | < 5%/hour | ___ |

---

## Bug Report Template

When finding issues:

```
**Device**: [Galaxy Watch 5, Pixel Watch, etc.]
**OS Version**: [11, 12, 13]
**App Version**: [1.0.0]
**Build**: [debug/release]

**Steps to Reproduce**:
1.
2.
3.

**Expected Result**:

**Actual Result**:

**Screenshots**: [if applicable]

**Logs**: [logcat output if available]
```

---

**Last Updated**: January 2024
**Version**: 1.0.0 Testing Guide
