import httpx
from app.config import settings
import random
import itertools

TMDB_API_KEY = settings.tmdb_api_key
BASE_URL = "https://api.themoviedb.org/3"

# Create a single, reusable client with the necessary configurations
client = httpx.AsyncClient(timeout=45.0, verify=False)

def _process_movie_results(data):
    """Helper function to process movie results consistently."""
    results = data.get("results", [])
    for movie in results:
        # Create the full poster path
        if movie.get("poster_path"):
            movie["poster_path"] = f"https://image.tmdb.org/t/p/w500{movie.get('poster_path')}"
        # Ensure vote_average is present, defaulting to 0.0
        movie["vote_average"] = movie.get("vote_average", 0.0)
    data["results"] = results
    return data

def _process_single_movie_details(movie_data):
    """Helper function to process a single movie's details."""
    # Format poster path
    if movie_data.get("poster_path"):
        movie_data["poster_path"] = f"https://image.tmdb.org/t/p/w500{movie_data.get('poster_path')}"

    # Extract US certification (PG rating)
    certification = "N/A"
    if "release_dates" in movie_data and "results" in movie_data["release_dates"]:
        for country in movie_data["release_dates"]["results"]:
            if country["iso_3166_1"] == "US":
                # Find the first non-empty certification
                for release in country['release_dates']:
                    if release["certification"]:
                        certification = release["certification"]
                        break
                break
    movie_data["certification"] = certification
    return movie_data

async def fetch_movie_details(movie_id: int):
    if not TMDB_API_KEY:
        raise ValueError("TMDB_API_KEY environment variable not set.")
    url = f"{BASE_URL}/movie/{movie_id}?api_key={TMDB_API_KEY}&append_to_response=credits,release_dates"
    response = await client.get(url)
    response.raise_for_status()
    movie_data = response.json()
    return _process_single_movie_details(movie_data)

async def discover_movies(genres: str = None, release_year_gte: int = None, 
                        release_year_lte: int = None, rating_gte: float = None, 
                        runtime_gte: int = None, runtime_lte: int = None, 
                        language: str = None, sort_by: str = None, page: int = 1):
    if not TMDB_API_KEY:
        raise ValueError("TMDB_API_KEY environment variable not set.")

    params = {"api_key": TMDB_API_KEY, "page": page}
    url = f"{BASE_URL}/discover/movie"

    if genres:
        params["with_genres"] = genres
    if release_year_gte:
        params["primary_release_date.gte"] = f"{release_year_gte}-01-01"
    if release_year_lte:
        params["primary_release_date.lte"] = f"{release_year_lte}-12-31"
    if rating_gte:
        params["vote_average.gte"] = rating_gte
    if runtime_gte:
        params["with_runtime.gte"] = runtime_gte
    if runtime_lte:
        params["with_runtime.lte"] = runtime_lte
    if language:
        params["language"] = language
    if sort_by:
        params["sort_by"] = sort_by

    response = await client.get(url, params=params)
    response.raise_for_status()
    return _process_movie_results(response.json())

async def search_movies_by_text(query: str, page: int = 1):
    if not TMDB_API_KEY:
        raise ValueError("TMDB_API_KEY environment variable not set.")

    params = {"api_key": TMDB_API_KEY, "page": page, "query": query}
    url = f"{BASE_URL}/search/movie"

    response = await client.get(url, params=params)
    response.raise_for_status()
    return _process_movie_results(response.json())

async def get_movie_watch_providers(movie_id: int):
    if not TMDB_API_KEY:
        raise ValueError("TMDB_API_KEY environment variable not set.")
    url = f"{BASE_URL}/movie/{movie_id}/watch/providers?api_key={TMDB_API_KEY}"
    response = await client.get(url)
    response.raise_for_status()
    return response.json()

async def get_trending_movies():
    if not TMDB_API_KEY:
        raise ValueError("TMDB_API_KEY environment variable not set.")
    url = f"{BASE_URL}/trending/movie/week?api_key={TMDB_API_KEY}"
    response = await client.get(url)
    response.raise_for_status()
    return _process_movie_results(response.json())

async def get_similar_movies(movie_id: int):
    if not TMDB_API_KEY:
        raise ValueError("TMDB_API_KEY environment variable not set.")
    url = f"{BASE_URL}/movie/{movie_id}/similar?api_key={TMDB_API_KEY}"
    response = await client.get(url)
    response.raise_for_status()
    return _process_movie_results(response.json())

import random
import itertools

async def get_movies_by_genres(genre_ids: list[int]):
    if not TMDB_API_KEY:
        raise ValueError("TMDB_API_KEY environment variable not set.")

    all_movies = []
    movie_ids = set()

    # Create pairs of genres to find interesting mixes
    if len(genre_ids) >= 2:
        genre_combinations = list(itertools.combinations(genre_ids, 2))
        random.shuffle(genre_combinations)
        # Limit to a reasonable number of combinations to avoid too many API calls
        for combo in genre_combinations[:5]:
            params = {
                "api_key": TMDB_API_KEY,
                "with_genres": ",".join(map(str, combo)), # Using comma for AND logic in TMDB API
                "sort_by": "popularity.desc",
                "page": 1
            }
            url = f"{BASE_URL}/discover/movie"
            try:
                response = await client.get(url, params=params)
                response.raise_for_status()
                data = _process_movie_results(response.json())
                
                # Add top movies from this combo, avoiding duplicates
                for movie in data.get("results", [])[:3]: # Get top 3 from each combo
                    if movie["id"] not in movie_ids:
                        all_movies.append(movie)
                        movie_ids.add(movie["id"])
            except Exception as e:
                print(f"Error fetching genre combo {combo}: {e}")

    # Shuffle the final list to mix up the genres
    random.shuffle(all_movies)

    return {
        "page": 1,
        "results": all_movies,
        "total_pages": 1, # Pagination is handled client-side for this combined view
        "total_results": len(all_movies)
    }
