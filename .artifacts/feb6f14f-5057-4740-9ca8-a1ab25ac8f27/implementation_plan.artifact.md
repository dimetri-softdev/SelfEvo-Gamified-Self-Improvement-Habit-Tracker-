# Refine Habit Data Layer and Remove Dummy Data

The goal is to move towards a "Live Data" state by removing hardcoded dummy inserts and refining the habit visibility logic in the repository.

## User Review Required

> [!IMPORTANT]
> I am removing the automatic insertion of dummy habits from `MainActivity`. This means the app will start with an empty state until the user adds habits or they are synced from the API.

## Proposed Changes

### Data Layer

#### [MODIFY] [HabitRepository.kt](file:///C:/Users/devos/StudioProjects/SelfEvo-Gamified-Self-Improvement-Habit-Tracker-/app/src/main/java/com/example/selfevo/data/repository/HabitRepository.kt)
- Refine `getHabitsForTodayStream`: Move the `todayName` calculation inside the `map` block to ensure it's always fresh when the database updates.
- Ensure that the filtering logic is robust and doesn't accidentally hide habits that should be visible.

### Application Lifecycle

#### [MODIFY] [MainActivity.kt](file:///C:/Users/devos/StudioProjects/SelfEvo-Gamified-Self-Improvement-Habit-Tracker-/app/src/main/java/com/example/selfevo/MainActivity.kt)
- Remove the `CoroutineScope(Dispatchers.IO).launch` block that performs dummy inserts into `playerCardDao` and `habitDao`.
- This ensures a clean "Live Data" state where only user-created or server-synced data exists.

## Verification Plan

### Automated Tests
- I will run `HabitRepositoryTest` to ensure that the repository logic still functions correctly without initial data.

### Manual Verification
- Open the app: verify it starts with an empty dashboard (or synced data if available).
- Add a new habit: verify it appears immediately on the dashboard.
- Verify that restarting the app doesn't re-insert the old dummy habits.
