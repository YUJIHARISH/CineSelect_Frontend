# Movie Overview Screen

This document describes the new Movie Overview screen that has been added to the CineSelect Android application.

## Overview

The Movie Overview screen provides detailed information about a selected movie, including:
- Hero image with movie title and basic details overlay
- Genre chips (Action, Crime, Drama, Thriller)
    - Action buttons (Add to Watchlist, Where to Watch)- Movie overview/description
- Cast & Crew information
- Related movie recommendations

## Files Created

### Layout
- `app/src/main/res/layout/activity_movie_overview.xml` - Main layout file for the movie overview screen

### Activity
- `app/src/main/java/com/saveetha/cineselect/MovieOverviewActivity.kt` - Kotlin activity class

### Drawables
- `app/src/main/res/drawable/gradient_overlay.xml` - Gradient overlay for hero image text
- `app/src/main/res/drawable/ic_share.xml` - Share icon

### Strings
- Added new string resources in `app/src/main/res/values/strings.xml`

## Features

### Navigation
- Back button to return to previous screen
- Share button to share movie information

### Movie Information Display
- Large hero image with movie title overlay
- Movie details (year, runtime, rating)
- Genre tags
- Movie description/overview

### Interactive Elements
    - Add to Watchlist button (with state management)- Where to Watch button
- Share functionality

### Cast & Crew Section
- Profile pictures for main cast members
- Actor names and character names
- Currently displays 3 main cast members

### Recommendations
- "You might also like" section
- Shows 3 related movies with posters and years

## Integration

### From Search Results
The MovieOverviewActivity is integrated with the SearchResultsActivity. When a user taps on a movie item in the search results, it navigates to the movie overview screen with the movie's information.

### Intent Extras
The activity accepts the following intent extras:
- `EXTRA_MOVIE_ID` - Unique identifier for the movie
- `EXTRA_MOVIE_TITLE` - Movie title
- `EXTRA_MOVIE_YEAR` - Release year
- `EXTRA_MOVIE_RUNTIME` - Movie duration
- `EXTRA_MOVIE_RATING` - Content rating
- `EXTRA_MOVIE_OVERVIEW` - Movie description
- `EXTRA_MOVIE_GENRES` - List of genres

### Example Usage

```kotlin
val intent = Intent(this, MovieOverviewActivity::class.java).apply {
    putExtra(MovieOverviewActivity.EXTRA_MOVIE_ID, "123")
    putExtra(MovieOverviewActivity.EXTRA_MOVIE_TITLE, "The Dark Knight")
    putExtra(MovieOverviewActivity.EXTRA_MOVIE_YEAR, "2008")
    putExtra(MovieOverviewActivity.EXTRA_MOVIE_RUNTIME, "2h 32m")
    putExtra(MovieOverviewActivity.EXTRA_MOVIE_RATING, "PG-13")
}
startActivity(intent)
```

## Design Features

### UI Components
- Status bar spacer for proper status bar handling
- Navigation bar with back and share buttons
- Hero image section with gradient overlay
- Horizontal scrolling genre chips
- Action buttons with proper spacing
- Content sections with consistent typography
- Responsive layout using ConstraintLayout

### Styling
- Uses existing color scheme from the app
- Consistent with app's design language
- Proper elevation and shadows
- Responsive button states

## Future Enhancements

### Planned Features
- Dynamic movie data loading from API
- Real movie poster images
- Cast member profile images
- Genre-based movie filtering
- User ratings and reviews
- Trailer playback
- Social sharing with movie posters

### Technical Improvements
- ViewModel architecture for data management
- LiveData for reactive UI updates
- Room database for offline movie data
- Image caching and loading
- Accessibility improvements

## Testing

To test the Movie Overview screen:

1. Run the app
2. Navigate to Search Results (from Home or Explore)
3. Tap on any movie item in the results
4. The Movie Overview screen should open with the selected movie's information

## Dependencies

The MovieOverviewActivity uses the following Android components:
- `AppCompatActivity` for the base activity
- `ConstraintLayout` for the main layout
- `NestedScrollView` for scrollable content
- `ImageView` and `TextView` for UI elements
- `Button` for interactive elements

No additional external dependencies are required.

