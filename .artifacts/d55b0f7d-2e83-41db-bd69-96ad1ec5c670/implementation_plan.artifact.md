# Implementation Plan - UI Redesign for SelfEvo

Design a comprehensive and polished UI for the SelfEvo app based on the provided design images, following a dark theme with gold/orange accents.

## User Review Required

> [!IMPORTANT]
> The redesign involves a significant change in the visual style and navigation structure.
> - The existing "News" and "Admin" tabs will be replaced with "Habits", "Evolutions", and "Settings".
> - A new Login/Sign-up flow will be introduced as the starting point.
> - Database schema updates will be required for `HabitEntity` and `PlayerCard`.

## Proposed Changes

### Theme and Resources

#### [NEW] [Theme.kt](file:///D:/SelfEvo/app/src/main/java/com/example/selfevo/ui/theme/Theme.kt)
Define the `SelfEvoTheme` with a custom color palette:
- `Background`: #000000 (Pure Black)
- `Primary`: #FFB300 (Gold/Orange)
- `Surface`: #121212 (Dark Grey)
- `Secondary`: #00BCD4 (Cyan/Blue for highlights)

#### [MODIFY] [strings.xml](file:///D:/SelfEvo/app/src/main/res/values/strings.xml)
Add new string resources for the new screens and UI elements.

---

### Data Models

#### [MODIFY] [PlayerCard.kt](file:///D:/SelfEvo/app/src/main/java/com/example/selfevo/data/model/PlayerCard.kt)
- Update tier logic to match the UI (Bronze: 50-64, Silver: 65-74, Gold: 75-84, Walkout: 85+).
- Rename or ensure stats match the UI (PAC, SHO, PAS, DRI/SKL, DEF, PHY).

#### [MODIFY] [HabitEntity.kt](file:///D:/SelfEvo/app/src/main/java/com/example/selfevo/data/local/entity/HabitEntity.kt)
- Add `frequency` (String) and `reminderTime` (String) fields.

---

### UI Components

#### [NEW] [CommonComponents.kt](file:///D:/SelfEvo/app/src/main/java/com/example/selfevo/ui/component/CommonComponents.kt)
- `SelfEvoLogo`: Composable for the hexagonal logo.
- `SelfEvoButton`: Gradient-styled button.
- `SelfEvoTextField`: Custom styled text input.
- `SelfEvoCard`: Custom styled container card.

#### [MODIFY] [FutPlayerCard.kt](file:///D:/SelfEvo/app/src/main/java/com/example/selfevo/ui/component/FutPlayerCard.kt)
Redesign the player card to match the new aesthetic, including the glow effect and stats layout.

#### [MODIFY] [HabitItemRow.kt](file:///D:/SelfEvo/app/src/main/java/com/example/selfevo/ui/component/HabitItemRow.kt)
Update the habit item layout to match the "Today's Habits" list in the dashboard.

---

### Screens

#### [NEW] [LoginScreen.kt](file:///D:/SelfEvo/app/src/main/java/com/example/selfevo/ui/auth/LoginScreen.kt)
Implement the Login/Sign-up screen with the logo, email/password fields, and Google sign-in.

#### [MODIFY] [DashboardScreen.kt](file:///D:/SelfEvo/app/src/main/java/com/example/selfevo/ui/dashboard/DashboardScreen.kt)
Redesign the Home screen with the user greeting, daily progress bar, player card, and habit list.

#### [NEW] [HabitsScreen.kt](file:///D:/SelfEvo/app/src/main/java/com/example/selfevo/ui/habits/HabitsScreen.kt)
Implement the "New Habit" screen with habit name, attribute selection, frequency, and reminder time.

#### [NEW] [EvolutionScreen.kt](file:///D:/SelfEvo/app/src/main/java/com/example/selfevo/ui/evolution/EvolutionScreen.kt)
Implement the "Evolution" screen showing tier progress and milestones.

#### [NEW] [SettingsScreen.kt](file:///D:/SelfEvo/app/src/main/java/com/example/selfevo/ui/settings/SettingsScreen.kt)
Implement the "Settings" screen with language preferences, appearance toggles, and notification settings.

---

### Navigation

#### [MODIFY] [SelfEvoNavigation.kt](file:///D:/SelfEvo/app/src/main/java/com/example/selfevo/ui/navigation/SelfEvoNavigation.kt)
- Update `Screen` sealed class with the new routes.
- Update `SelfEvoApp` to include the `LoginScreen` and the new bottom navigation tabs.

## Verification Plan

### Automated Tests
- Run existing unit tests for `HabitRepository` and `DashboardViewModel`.
- Create basic Compose previews for each new screen to verify layout.

### Manual Verification
- Deploy to an emulator/device.
- Navigate through all screens (Login -> Home -> Habits -> Evolution -> Settings).
- Verify the "Log Habit" functionality updates the progress bar and player card.
- Verify the "New Habit" screen correctly saves data to the local database.
