Of course. Here is the complete list of API route features in a structured Markdown format, perfect for feeding to your terminal-based AI assistant.

This specification is based on all the screens provided and includes the necessary underlying logic (like password resets and guest sessions) for a fully functional backend.

---

# CineSelect API Specification

This document outlines the required API endpoints for the CineSelect application.

## 1. Authentication (`/api/auth`)

Handles user sign-up, login, and session management.

### `POST /api/auth/register`
*   **Description:** Creates a new user account.
*   **Request Body:** `{ "fullName": "string", "email": "string", "password": "string" }`
*   **Success Response:** `{ "token": "jwt_string", "user": { ...user_object... } }`

### `POST /api/auth/login`
*   **Description:** Authenticates a user with email and password.
*   **Request Body:** `{ "email": "string", "password": "string" }`
*   **Success Response:** `{ "token": "jwt_string", "user": { ...user_object... } }`

### `POST /api/auth/social`
*   **Description:** Authenticates or registers a user via a social provider (Google, Facebook).
*   **Request Body:** `{ "provider": "google" | "facebook", "token": "provider_auth_token" }`
*   **Success Response:** `{ "token": "jwt_string", "user": { ...user_object... } }`

### `POST /api/auth/logout`
*   **Description:** Logs the user out. The backend should invalidate the token if using a blocklist.
*   **Notes:** Requires authentication.

### `POST /api/auth/password/request-reset`
*   **Description:** User provides an email to receive a password reset link.
*   **Request Body:** `{ "email": "string" }`

### `POST /api/auth/password/reset`
*   **Description:** User provides the reset token and a new password.
*   **Request Body:** `{ "token": "reset_token_string", "newPassword": "string" }`

### `POST /api/auth/guest`
*   **Description:** Creates a temporary guest session for users who select "Continue as Guest".
*   **Success Response:** `{ "guestToken": "temporary_jwt_string" }`

## 2. User Management (`/api/users`)

Handles user profiles, preferences, and activity.

### `GET /api/users/me`
*   **Description:** Retrieves the profile, stats, and preferences for the currently authenticated user. Powers the **Profile Screen**.
*   **Notes:** Requires authentication.
*   **Success Response:** A full user object including stats like `moviesRated`, `watchlistCount`, `reviewsCount`, and `favoriteGenres`.

### `PUT /api/users/me`
*   **Description:** Updates the current user's profile information (e.g., name, email, avatar).
*   **Request Body:** `{ "fullName": "string", "email": "string" }`
*   **Notes:** Requires authentication.

### `PUT /api/users/me/preferences`
*   **Description:** Updates the user's preferences, such as favorite genres, language, or theme. Used during onboarding and on the **Profile Screen**.
*   **Request Body:** `{ "favoriteGenreIds": [1, 2, 3], "language": "en", "theme": "dark" }`
*   **Notes:** Requires authentication.

## 3. Genres (`/api/genres`)

### `GET /api/genres`
*   **Description:** Gets a list of all available movie genres. Used in the **Select Genres** and **Advanced Search** screens.
*   **Success Response:** `[ { "id": int, "name": "string" }, ... ]`

## 4. Movies & Discovery (`/api/movies`)

The core of the app for finding and exploring movies.

### `GET /api/movies/search`
*   **Description:** A powerful search endpoint that handles text queries and advanced filtering. Powers the **Search Results Screen**.
*   **Query Parameters:**
    *   `q`: (string) Text query for movie titles.
    *   `genres`: (string) Comma-separated list of genre IDs (e.g., `28,12,878`).
    *   `releaseYear_gte`: (int) Minimum release year.
    *   `releaseYear_lte`: (int) Maximum release year.
    *   `rating_gte`: (float) Minimum average rating.
    *   `runtime_gte`: (int) Minimum runtime in minutes.
    *   `runtime_lte`: (int) Maximum runtime in minutes.
    *   `language`: (string) ISO 639-1 language code.
    *   `sortBy`: (string) e.g., `popularity.desc`, `release_date.desc`.
    *   `page`: (int) For pagination.
*   **Success Response:** `{ "results": [ ...movie_previews... ], "totalResults": int, "page": int, "totalPages": int }`

### `GET /api/movies/recommendations/for-you`
*   **Description:** Gets personalized movie recommendations for the user's **Home Screen**. Based on their selected favorite genres and rating history.
*   **Notes:** Requires authentication.

### `GET /api/movies/trending-mixes`
*   **Description:** Gets the "Trending Genre Mixes" for the **Home Screen**.
*   **Success Response:** `[ { "mixName": "Action + Comedy", "genres": [ ...genre_objects... ], "movies": [ ...movie_previews... ] }, ... ]`

### `GET /api/movies/{id}`
*   **Description:** Retrieves full, detailed information for a single movie. Powers the **Movie Detail Screen**.
*   **Success Response:** A full movie object including `overview`, `cast`, `crew`, `runtime`, `pgRating`, etc.

### `GET /api/movies/{id}/similar`
*   **Description:** Finds movies similar to a given movie. Powers the "You might also like" section.
*   **Success Response:** `{ "results": [ ...movie_previews... ] }`

### `GET /api/movies/{id}/watch-providers`
*   **Description:** Gets streaming, rental, and purchase options for a movie. The backend should call a 3rd party API (e.g., TMDB) to get this data, hiding the API key from the client.
*   **Success Response:** A structured object listing providers.

## 5. Shortlists / Watchlists (`/api/shortlists`)

All routes here require authentication.

### `GET /api/shortlists`
*   **Description:** Gets all shortlists created by the current user. Powers the **My Shortlists Screen**.
*   **Success Response:** `[ { "id": int, "name": "string", "movieCount": int, "updatedAt": "datetime", "coverImages": ["url1", "url2"] }, ... ]`

### `POST /api/shortlists`
*   **Description:** Creates a new, empty shortlist.
*   **Request Body:** `{ "name": "string" }`
*   **Success Response:** The newly created shortlist object.

### `DELETE /api/shortlists/{id}`
*   **Description:** Deletes an entire shortlist.

### `POST /api/shortlists/{id}/movies`
*   **Description:** Adds a movie to a specific shortlist.
*   **Request Body:** `{ "movieId": int }`

### `DELETE /api/shortlists/{id}/movies/{movieId}`
*   **Description:** Removes a movie from a specific shortlist.

## 6. User Activity & History (`/api/activity`)

All routes here require authentication.

### `POST /api/activity/reviews`
*   **Description:** A user submits a rating and/or a text review for a movie.
*   **Request Body:** `{ "movieId": int, "rating": float, "text": "string" }`

### `GET /api/activity/recent-searches`
*   **Description:** Retrieves the user's most recent search queries for the **Search Screen**.
*   **Success Response:** `[ { "id": int, "queryText": "string" }, ... ]`

### `POST /api/activity/saved-searches`
*   **Description:** Saves a complex search query and its filters for later use.
*   **Request Body:** `{ "name": "string", "filters": { ...search_filter_object... } }`

### `DELETE /api/activity/saved-searches/{id}`
*   **Description:** Deletes a saved search.