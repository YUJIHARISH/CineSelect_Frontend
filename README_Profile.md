# Profile Screen

This document describes the new Profile screen that has been added to the CineSelect Android application.

## Overview

The Profile screen provides users with a comprehensive view of their account information, statistics, and app preferences. It features a clean, minimalist design with user stats, preference settings, and account management options.

## Files Created

### Layout
- `app/src/main/res/layout/activity_profile.xml` - Main layout file for the profile screen

### Activity
- `app/src/main/java/com/saveetha/cineselect/ProfileActivity.kt` - Kotlin activity class

### Drawables
- `bg_profile_picture.xml` - Circular background for profile picture
- `bg_edit_profile_button.xml` - Background for Edit Profile button
- `bg_stats_card.xml` - Background for stats card
- `bg_preferences_card.xml` - Background for preferences card
- `ic_reviews.xml` - Reviews icon (speech bubble)
- `ic_arrow_right.xml` - Right arrow icon for preferences
- `ic_language.xml` - Language settings icon
- `ic_theme.xml` - Theme settings icon (color palette)
- `ic_privacy.xml` - Privacy settings icon (padlock)
- `ic_notifications.xml` - Notifications icon (bell)
- `ic_help.xml` - Help icon (question mark in circle)
- `ic_about.xml` - About icon (i in circle)
- `ic_logout.xml` - Logout icon (arrow pointing out of box)

### Colors
- Added `red` color for logout button

### Strings
- Added comprehensive string resources for all profile screen elements

## Features

### Profile Header
- **Profile Picture**: Circular image with coral border
- **User Name**: "John Anderson" displayed in large, bold text
- **Edit Profile Button**: Rounded button with light background

### Your Stats Section
A card displaying user statistics in a 2x2 grid:
- **Favorite Genres**: Shows "Drama, Action" with movie camera icon
- **Movies Rated**: Shows "127" with star icon
- **Watchlist**: Shows "23" with bookmark icon
- **Reviews**: Shows "45" with speech bubble icon

### Preferences Section
A comprehensive list of app settings and account options:
- **Language Settings**: App language configuration
- **Theme**: App theme/color scheme settings
- **Content Filters**: Content filtering options
- **Privacy**: Privacy and security settings
- **Account**: Account management options
- **Notifications**: Notification preferences
- **Help**: Help and support access
- **About**: App information and version details
- **Log Out**: Account logout (highlighted in red)

### Navigation
- **Bottom Navigation**: Integrated with existing navigation system
- **Profile Tab Highlighted**: Shows profile as the active screen
- **Cross-Activity Navigation**: All activities navigate to ProfileActivity

## Integration

### Navigation Flow
- **From Home**: Bottom navigation profile tab → ProfileActivity
- **From Explore**: Bottom navigation profile tab → ProfileActivity
- **From Watchlist**: Bottom navigation profile tab → ProfileActivity
- **From Search Results**: Bottom navigation profile tab → ProfileActivity

### Bottom Navigation
- Integrated with existing bottom navigation system
- Uses existing `ic_profile` icon
- Properly handles navigation between all app sections

### Intent Extras
The activity is prepared to handle the following intent extras for future enhancements:
- `EXTRA_USER_ID` - Unique identifier for the user
- `EXTRA_USER_NAME` - User's display name
- `EXTRA_USER_EMAIL` - User's email address

## Design Features

### UI Components
- Status bar spacer for proper status bar handling
- Centered profile header with circular image
- Card-based stats section with icons and data
- Comprehensive preferences list with icons and arrows
- Consistent bottom navigation

### Styling
- Uses existing app color scheme (`@color/coral`, `@color/textPrimary`, etc.)
- Consistent with app's design language
- Proper elevation and shadows for depth
- Rounded corners and proper spacing
- Red highlighting for logout option

### Interactive Elements
- All preference items are clickable with ripple effects
- Edit Profile button with proper styling
- Logout confirmation dialog
- Bottom navigation with proper highlighting

## Functionality

### User Data Management
- **Load User Data**: Displays user information (currently hardcoded)
- **Edit Profile**: Placeholder for profile editing functionality
- **Logout**: Clears user data and navigates to login screen

### Preference Handling
Each preference item has a dedicated method for handling clicks:
- `openLanguageSettings()` - Language configuration
- `openThemeSettings()` - Theme customization
- `openContentFilters()` - Content filtering
- `openPrivacySettings()` - Privacy management
- `openAccountSettings()` - Account settings
- `openNotificationSettings()` - Notification preferences
- `openHelp()` - Help and support
- `openAbout()` - App information

### Logout Process
- **Confirmation Dialog**: Shows confirmation before logout
- **Data Clearing**: Clears user preferences and data
- **Navigation**: Redirects to login screen with proper flags

## Future Enhancements

### Planned Features
- Dynamic user data loading from API/database
- Real profile picture upload and display
- Live stats updates from user activity
- Theme switching functionality
- Language localization support
- Push notification management
- Account linking and social login

### Technical Improvements
- ViewModel architecture for data management
- LiveData for reactive UI updates
- Room database for offline user data
- Image caching and loading
- Accessibility improvements
- Biometric authentication

## Testing

To test the Profile screen:

1. Run the app
2. Navigate to any screen with bottom navigation
3. Tap the Profile tab (fourth icon)
4. The Profile screen should open with user information
5. Test all preference item clicks (currently show toasts)
6. Test the Edit Profile button
7. Test the Logout functionality
8. Test bottom navigation to other sections

## Dependencies

The ProfileActivity uses the following Android components:
- `AppCompatActivity` for the base activity
- `ConstraintLayout` for the main layout
- `NestedScrollView` for scrollable content
- `BottomNavigationView` for bottom navigation
- `LinearLayout` for preference items
- `ImageView` and `TextView` for UI elements
- `Button` for interactive elements
- `AlertDialog` for logout confirmation

No additional external dependencies are required.

## Navigation Integration

The screen is fully integrated with the existing navigation system:
- **HomeActivity**: Updated to navigate to ProfileActivity
- **ExploreActivity**: Updated to navigate to ProfileActivity
- **MyWatchlistActivity**: Updated to navigate to ProfileActivity
- **SearchResultsActivity**: Updated to navigate to ProfileActivity
- **Bottom Navigation**: Properly highlights profile tab
- **Back Navigation**: Handles back button presses correctly

