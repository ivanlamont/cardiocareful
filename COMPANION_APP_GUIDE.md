# Cardio Careful Companion App Guide

This guide explains how to build a companion phone app for Cardio Careful that runs on Android phones.

## Overview

A companion app allows users to:
- View live heart rate data on their phone
- Configure alert profiles from their phone
- View historical heart rate statistics
- Manage multiple alert profiles easily
- Control watch settings without touching the watch

## Architecture

```
Watch App (Cardio Careful)          Phone App (Companion)
┌─────────────────────────┐         ┌──────────────────────┐
│ Alert Profiles (DB)     │◄──────►│ Profile Manager UI   │
│ Heart Rate Data (DB)    │         │ HR Chart/Stats       │
│ Settings (DataStore)    │         │ Settings Screen      │
│ MonitorData (Monitoring)│         │ Live HR Display      │
└─────────────────────────┘         └──────────────────────┘
          ▲                                   ▲
          │                                   │
          └───────────────────────────────────┘
              Wear Data Layer / FCM / REST API
```

## Communication Methods

### 1. Wear Data Layer API (Recommended)

**Best for**: Seamless integration with Wear OS, automatic sync

```kotlin
// In companion app - Send profile to watch
val client = Wearable.getDataClient(context)
val dataMap = PutDataMapRequest.create("/alert_profile")
dataMap.dataMap.apply {
    putInt("minHR", 60)
    putInt("maxHR", 100)
    putString("pattern", "SHORT")
    putLong("time", System.currentTimeMillis())
}
client.putDataItem(dataMap.asPutDataRequest())

// In watch app - Receive and process updates
class WearableListenerService : ListenerService() {
    override fun onDataChanged(events: DataEventBuffer) {
        for (event in events) {
            if (event.dataItem.uri.path == "/alert_profile") {
                // Update local settings
            }
        }
    }
}
```

### 2. Firebase Cloud Messaging (FCM)

**Best for**: Cross-device, cloud-based synchronization

```kotlin
// In companion app - Send commands to watch via FCM
FirebaseMessaging.getInstance().send(
    RemoteMessage.Builder("watch-device-id")
        .setData(mapOf(
            "action" to "update_profile",
            "profile_id" to "1",
            "min_hr" to "80"
        ))
        .build()
)

// In watch app - Receive FCM messages
class HeartRateMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        if (message.data.containsKey("action")) {
            when (message.data["action"]) {
                "update_profile" -> updateProfileFromCompanion(message.data)
            }
        }
    }
}
```

### 3. Direct SharedPreferences (Simple)

**Best for**: Same-device testing, simple cases

```kotlin
// In companion app - Update settings
val prefs = context.getSharedPreferences("cardio_sync", Context.MODE_PRIVATE)
prefs.edit().apply {
    putInt("min_heart_rate", 60)
    putInt("max_heart_rate", 100)
    apply()
}

// In watch app - Read updates
val prefs = context.getSharedPreferences("cardio_sync", Context.MODE_PRIVATE)
val minHr = prefs.getInt("min_heart_rate", 60)
```

## Companion App UI Components

### 1. Live Heart Rate Display
```kotlin
@Composable
fun LiveHeartRateScreen(viewModel: CompanionViewModel) {
    val currentHR by viewModel.currentHeartRate.collectAsState()

    Text(
        text = "$currentHR bpm",
        fontSize = 48.sp,
        color = when {
            currentHR > 140 -> Color.Red
            currentHR > 100 -> Color.Orange
            else -> Color.Green
        }
    )
}
```

### 2. Profile Management Screen
```kotlin
@Composable
fun ProfileManagerScreen(viewModel: CompanionViewModel) {
    val profiles by viewModel.profiles.collectAsState()

    LazyColumn {
        items(profiles) { profile ->
            ProfileCard(
                profile = profile,
                onActivate = { viewModel.activateProfile(profile.id) },
                onEdit = { /* Open edit dialog */ }
            )
        }
    }
}
```

### 3. Statistics Display
```kotlin
@Composable
fun StatsScreen(viewModel: CompanionViewModel) {
    val stats by viewModel.hourlyStats.collectAsState()

    Column {
        Text("Heart Rate Stats (Last Hour)")
        Text("Min: ${stats?.minHeartRate} bpm")
        Text("Max: ${stats?.maxHeartRate} bpm")
        Text("Avg: ${String.format("%.0f", stats?.averageHeartRate)} bpm")
        Text("Alerts: ${stats?.alertCount}")
    }
}
```

## Data Models for Syncing

```kotlin
// Profile data to sync
data class SyncProfile(
    val id: Int,
    val name: String,
    val minHeartRate: Int,
    val maxHeartRate: Int,
    val hapticPattern: String,
    val cooldownSeconds: Int,
    val notificationsEnabled: Boolean
)

// Heart rate data to sync
data class SyncHeartRateReading(
    val value: Double,
    val timestamp: Long,
    val alertTriggered: Boolean,
    val alertType: String?
)

// Statistics to sync
data class SyncStats(
    val minHeartRate: Double,
    val maxHeartRate: Double,
    val averageHeartRate: Double,
    val readingCount: Int,
    val alertCount: Int
)
```

## Implementation Checklist

### Watch App (Already Complete)
- ✅ Alert profiles in database
- ✅ ProfileRepository for access
- ✅ CompanionDataSync framework
- ✅ User preferences persistence

### Companion App Implementation
- ⬜ Create companion Android app module
- ⬜ Implement chosen communication method (Wear Data Layer/FCM/Prefs)
- ⬜ Create ViewModel for companion data
- ⬜ Build UI screens (Live HR, Profiles, Stats)
- ⬜ Add profile sync functionality
- ⬜ Add HR data sync functionality
- ⬜ Test on physical watch + phone pair

## Testing

### Local Testing (Without Companion App)
1. Use watch emulator
2. Use SharedPreferences for testing
3. Verify MonitorData respects profile changes

### Full Integration Testing
1. Pair physical watch with phone
2. Deploy both apps
3. Test profile changes sync correctly
4. Verify HR data displays on phone
5. Check real-time updates

## Security Considerations

- Verify companion app identity before accepting data
- Encrypt sensitive data in transit (profile settings)
- Validate all data received from companion
- Use unique device identifiers for pairing
- Consider user authentication if cloud-based

## Performance Tips

- Batch profile updates (don't send on every change)
- Cache HR data locally on companion app
- Use efficient data serialization (Protocol Buffers)
- Limit sync frequency to preserve battery
- Implement exponential backoff for retries

## Future Enhancements

1. **Cloud Sync**: Backup profiles and history to cloud
2. **Multiple Devices**: Sync across multiple watches
3. **Voice Control**: Configure via voice commands
4. **Wear OS Complication**: Show HR in system complications
5. **Auto-Profiles**: Switch profiles based on activity type
6. **Social Features**: Share achievements with friends

## References

- [Wear Data Layer API](https://developer.android.com/training/wearables/data/index.html)
- [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging)
- [Wear OS Development](https://developer.android.com/wear)
- [Android App Pairing](https://developer.android.com/training/wearables/apps)

## Questions?

For implementation help, refer to the `CompanionDataSync` class in the watch app for the data access interfaces available to a companion app.
