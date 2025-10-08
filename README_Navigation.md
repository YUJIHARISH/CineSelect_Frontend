# Navigation System

This document describes the navigation system implemented in the CineSelect Android application that provides proper back navigation between screens.

## Overview

The navigation system ensures that when users navigate between different screens using the bottom navigation bar, the back button (both system back button and UI back buttons) takes them to the previous screen they were on, just like in regular mobile applications.

## Components

### NavigationHelper
- **File**: `app/src/main/java/com/saveetha/cineselect/NavigationHelper.kt`
- **Type**: Kotlin Object (Singleton)
- **Purpose**: Manages the navigation stack and handles navigation between activities

## How It Works

### Navigation Stack
The `NavigationHelper` maintains a stack of activities that the user has visited. When navigating to a new screen:

1. The current activity is added to the stack (if not already present)
2. The target activity is started
3. When the back button is pressed, the previous activity is retrieved from the stack and started

### Navigation Flow
```
Home → Explore → Profile → Watchlist
```

If the user is on Watchlist and presses back:
- They go back to Profile
- Pressing back again takes them to Explore
- Pressing back again takes them to Home

## Implementation Details

### Key Methods

#### `navigateTo(context, targetActivity)`
- Adds current activity to navigation stack
- Starts the target activity
- Prevents duplicate entries in the stack

#### `navigateBack(context)`
- Retrieves the previous activity from the stack
- Starts the previous activity with proper flags
- Finishes the current activity

#### `clearStack()`
- Clears the entire navigation stack
- Useful for logout or app reset scenarios

### Activity Integration

All main activities have been updated to use the NavigationHelper:

#### Bottom Navigation
```kotlin
bottomNavigation.setOnNavigationItemSelectedListener { item ->
    when (item.itemId) {
        R.id.navigation_home -> {
            NavigationHelper.navigateTo(this, HomeActivity::class.java)
            true
        }
        R.id.navigation_explore -> {
            NavigationHelper.navigateTo(this, ExploreActivity::class.java)
            true
        }
        // ... other navigation items
    }
}
```

#### Back Button Handling
```kotlin
override fun onBackPressed() {
    NavigationHelper.navigateBack(this)
}
```

#### UI Back Buttons
```kotlin
ivBack.setOnClickListener {
    NavigationHelper.navigateBack(this)
}
```

## Updated Activities

### HomeActivity
- **Bottom Navigation**: Uses NavigationHelper for all navigation
- **Back Button**: Overrides `onBackPressed()` to use NavigationHelper
- **No UI Back Button**: Correctly doesn't have a back button

### ExploreActivity
- **Bottom Navigation**: Uses NavigationHelper for all navigation
- **Back Button**: Overrides `onBackPressed()` to use NavigationHelper
- **UI Back Button**: Uses NavigationHelper for back navigation

### MyWatchlistActivity
- **Bottom Navigation**: Uses NavigationHelper for all navigation
- **Back Button**: Overrides `onBackPressed()` to use NavigationHelper
- **UI Back Button**: Uses NavigationHelper for back navigation

### ProfileActivity
- **Bottom Navigation**: Uses NavigationHelper for all navigation
- **Back Button**: Overrides `onBackPressed()` to use NavigationHelper
- **No UI Back Button**: Correctly doesn't have a back button

### SearchResultsActivity
- **Bottom Navigation**: Uses NavigationHelper for profile navigation
- **Back Button**: Overrides `onBackPressed()` to use NavigationHelper
- **UI Back Button**: Uses NavigationHelper for back navigation

## Navigation Scenarios

### Scenario 1: Sequential Navigation
1. User starts on Home
2. Taps Explore → Goes to Explore (Home added to stack)
3. Taps Profile → Goes to Profile (Explore added to stack)
4. Taps Watchlist → Goes to Watchlist (Profile added to stack)
5. Presses back → Goes to Profile
6. Presses back → Goes to Explore
7. Presses back → Goes to Home

### Scenario 2: Cross-Navigation
1. User starts on Home
2. Taps Explore → Goes to Explore (Home added to stack)
3. Taps Profile → Goes to Profile (Explore added to stack)
4. Taps Home → Goes to Home (Profile added to stack)
5. Presses back → Goes to Profile
6. Presses back → Goes to Explore
7. Presses back → Goes to Home

### Scenario 3: Back Button from Any Screen
- **From Home**: Exits app (no previous screen)
- **From Explore**: Goes to Home
- **From Profile**: Goes to previous screen in stack
- **From Watchlist**: Goes to previous screen in stack
- **From Search Results**: Goes to previous screen in stack

## Debug Features

The NavigationHelper includes debug logging to help track navigation flow:

```kotlin
Log.d(TAG, "Navigating to ${targetActivity.simpleName}")
Log.d(TAG, "Current stack: ${navigationStack.map { it.simpleName }}")
```

To view navigation logs:
1. Open Android Studio
2. Go to Logcat
3. Filter by tag: "NavigationHelper"

## Benefits

### User Experience
- **Intuitive Navigation**: Back button works as expected
- **Consistent Behavior**: Same navigation pattern across all screens
- **No Lost Context**: Users can always return to previous screens

### Developer Experience
- **Centralized Logic**: All navigation handled in one place
- **Easy Maintenance**: Changes to navigation logic only need to be made in NavigationHelper
- **Debug Support**: Built-in logging for troubleshooting

### Technical Benefits
- **Memory Efficient**: Proper activity lifecycle management
- **Stack Management**: Prevents activity stack overflow
- **Flag Optimization**: Uses appropriate Intent flags for smooth transitions

## Future Enhancements

### Planned Features
- **Deep Linking**: Support for direct navigation to specific screens
- **Navigation History**: Track and display recent screens
- **Custom Transitions**: Smooth animations between screens
- **State Preservation**: Maintain screen state when navigating back

### Technical Improvements
- **ViewModel Integration**: Better state management during navigation
- **Fragment Support**: Extend to work with fragments
- **Navigation Component**: Migrate to Android Navigation Component
- **Biometric Integration**: Secure navigation for sensitive screens

## Testing

To test the navigation system:

1. **Start the app** from HomeActivity
2. **Navigate between screens** using bottom navigation
3. **Test back button** from each screen
4. **Test UI back buttons** where available
5. **Verify navigation stack** using debug logs
6. **Test edge cases** like rapid navigation

### Test Cases
- [ ] Home → Explore → Back → Home
- [ ] Home → Profile → Watchlist → Back → Profile → Back → Home
- [ ] Explore → Profile → Home → Back → Profile → Back → Explore
- [ ] Search Results → Profile → Back → Search Results
- [ ] Rapid navigation between screens
- [ ] Back button from Home (should exit app)

## Troubleshooting

### Common Issues

#### Activity Not in Stack
- **Symptom**: Back button doesn't work as expected
- **Solution**: Check if activity is being added to stack in `navigateTo()`

#### Duplicate Activities
- **Symptom**: Same activity appears multiple times in stack
- **Solution**: Verify duplicate prevention logic in `navigateTo()`

#### Stack Overflow
- **Symptom**: App crashes or behaves unexpectedly
- **Solution**: Check for circular navigation or infinite loops

### Debug Steps
1. Check NavigationHelper logs in Logcat
2. Verify activity stack contents
3. Test navigation flow step by step
4. Check for missing `onBackPressed()` overrides

