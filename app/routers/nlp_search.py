from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from app import crud, models, schemas
from app.database import SessionLocal
from app.security import get_current_user
from app.services import tmdb_api
import spacy
import re
import httpx

router = APIRouter()

nlp = spacy.load("en_core_web_sm")

genre_keywords = {
    "action": "28", "adventure": "12", "animation": "16", "comedy": "35",
    "crime": "80", "documentary": "99", "drama": "18", "family": "10751",
    "fantasy": "14", "history": "36", "horror": "27", "music": "10402",
    "mystery": "9648", "romance": "10749", "science fiction": "878", 
    "tv movie": "10770", "thriller": "53", "war": "10752", "western": "37"
}

language_keywords = {
    "english": "en", "spanish": "es", "french": "fr", "german": "de", "italian": "it",
    "japanese": "ja", "korean": "ko", "chinese": "zh", "hindi": "hi", "tamil": "ta"
}

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@router.get("/nlp")
async def search_nlp(q: str, page: int = 1, db: Session = Depends(get_db), current_user: models.User = Depends(get_current_user)):
    original_query = q
    query_lower = q.lower()
    
    genres = []
    language = None
    year = None
    rating_gte = None

    # --- Entity Extraction ---
    year_match = re.search(r'(from|of|in) (\d{4})', query_lower)
    if year_match:
        year = int(year_match.group(2))

    rating_match = re.search(r'rated more than (\d)', query_lower)
    if rating_match:
        rating_gte = float(rating_match.group(1))

    words = query_lower.split()
    for word in words:
        if word in genre_keywords:
            genres.append(genre_keywords[word])
        elif word in language_keywords:
            language = language_keywords[word]

    final_genres = ",".join(genres) if genres else None

    # --- Search Logic ---
    try:
        # If we have specific filters, use the discover endpoint
        if final_genres or year or rating_gte or language:
            results = await tmdb_api.discover_movies(
                genres=final_genres,
                release_year_gte=year,
                release_year_lte=year, # Exact year match
                rating_gte=rating_gte,
                language=language,
                page=page
            )
        # Otherwise, use the standard text search
        else:
            results = await tmdb_api.search_movies_by_text(query=original_query, page=page)

    except httpx.ConnectError as e:
        print(f"ConnectError during TMDB search: {e}")
        return {"results": [], "page": 1, "total_results": 0, "total_pages": 0}

    if results.get("results"):
        crud.create_recent_search(db=db, user_id=current_user.id, query=original_query)

    return results
