from fastapi import APIRouter, HTTPException, status, Depends
from sqlalchemy.orm import Session, joinedload
from app.services import tmdb_api
from app.security import get_current_user
from app.models import User
from app.database import SessionLocal
import json # Import the json library

router = APIRouter()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@router.get("/search")
async def search_movies(q: str = None, genres: str = None, releaseYear_gte: int = None, releaseYear_lte: int = None, rating_gte: float = None, runtime_gte: int = None, runtime_lte: int = None, language: str = None, sortBy: str = None, page: int = 1):
    try:
        results = await tmdb_api.search_movies(
            query=q, genres=genres, release_year_gte=releaseYear_gte, release_year_lte=releaseYear_lte,
            rating_gte=rating_gte, runtime_gte=runtime_gte, runtime_lte=runtime_lte, language=language,
            sort_by=sortBy, page=page
        )
        # Log the exact JSON being sent
        print("--- SEARCH RESULTS JSON ---")
        print(json.dumps(results, indent=2))
        print("---------------------------")
        return results
    except Exception as e:
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=str(e))

@router.get("/discover")
async def discover_movies(genres: str = None, releaseYear_gte: int = None, releaseYear_lte: int = None, rating_gte: float = None, runtime_gte: int = None, runtime_lte: int = None, language: str = None, sortBy: str = None, page: int = 1):
    try:
        results = await tmdb_api.discover_movies(
            genres=genres, release_year_gte=releaseYear_gte, release_year_lte=releaseYear_lte,
            rating_gte=rating_gte, runtime_gte=runtime_gte, runtime_lte=runtime_lte, language=language,
            sort_by=sortBy, page=page
        )
        # Log the exact JSON being sent
        print("--- DISCOVER RESULTS JSON ---")
        print(json.dumps(results, indent=2))
        print("---------------------------")
        return results
    except Exception as e:
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=str(e))

@router.get("/recommendations/for-you")
async def get_recommendations(db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    try:
        # Reload the user from the database to ensure relationships are loaded
        user = db.query(User).options(joinedload(User.favorite_genres)).filter(User.id == current_user.id).first()
        if not user or not user.favorite_genres:
            return {"results": []}
        
        genre_ids = [genre.id for genre in user.favorite_genres]
        
        recommended_movies = await tmdb_api.get_movies_by_genres(genre_ids)
        # Log the exact JSON being sent
        print("--- RECOMMENDATIONS JSON ---")
        print(json.dumps(recommended_movies, indent=2))
        print("----------------------------")
        return recommended_movies
    except Exception as e:
        print(f"!!! EXCEPTION in get_recommendations: {e} !!!")
        import traceback
        traceback.print_exc()
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=str(e))

@router.get("/trending-mixes")
async def get_trending_mixes():
    try:
        trending_movies = await tmdb_api.get_trending_movies()
        return [
            {
                "mixName": "Trending Now",
                "genres": [],
                "movies": trending_movies.get("results", [])[:5]
            }
        ]
    except Exception as e:
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=str(e))

@router.get("/{id}")
async def get_movie_details(id: int):
    try:
        details = await tmdb_api.fetch_movie_details(id)
        return details
    except Exception as e:
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=str(e))

@router.get("/{id}/similar")
async def get_similar_movies(id: int):
    try:
        similar = await tmdb_api.get_similar_movies(id)
        return {"results": similar.get("results", [])}
    except Exception as e:
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=str(e))

@router.get("/{id}/watch-providers")
async def get_watch_providers(id: int):
    try:
        providers = await tmdb_api.get_movie_watch_providers(id)
        return providers
    except Exception as e:
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=str(e))
