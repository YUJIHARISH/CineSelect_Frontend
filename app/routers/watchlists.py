from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from app import schemas, crud, models
from app.database import SessionLocal
from app.security import get_current_user
from app.services import tmdb_api

router = APIRouter()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@router.get("/", response_model=list[schemas.Watchlist])
async def get_user_watchlists(db: Session = Depends(get_db), current_user: models.User = Depends(get_current_user)):
    watchlists = crud.get_user_watchlists(db=db, user_id=current_user.id)
    # We need to fetch full details for each movie in the watchlist
    detailed_watchlists = []
    for watchlist in watchlists:
        detailed_movies = []
        for movie in watchlist.movies:
            try:
                movie_details = await tmdb_api.fetch_movie_details(movie.id)
                detailed_movies.append(movie_details)
            except Exception as e:
                # If a movie fails to fetch, we can skip it or handle it as needed
                print(f"Could not fetch details for movie {movie.id}: {e}")
        detailed_watchlists.append(schemas.Watchlist(
            id=watchlist.id,
            name=watchlist.name,
            movies=detailed_movies
        ))
    return detailed_watchlists

@router.post("/add", response_model=schemas.Watchlist)
def add_movie_to_user_watchlist(movie: schemas.MovieId, db: Session = Depends(get_db), current_user: models.User = Depends(get_current_user)):
    # For simplicity, we assume the user has one watchlist.
    watchlist = crud.get_user_watchlist(db=db, user_id=current_user.id)
    if not watchlist:
        raise HTTPException(status_code=404, detail="Watchlist not found")
    return crud.add_movie_to_watchlist(db=db, watchlist_id=watchlist.id, movie_id=movie.movieId)

@router.post("/remove", response_model=schemas.Watchlist)
def remove_movie_from_user_watchlist(movie: schemas.MovieId, db: Session = Depends(get_db), current_user: models.User = Depends(get_current_user)):
    # For simplicity, we assume the user has one watchlist.
    watchlist = crud.get_user_watchlist(db=db, user_id=current_user.id)
    if not watchlist:
        raise HTTPException(status_code=404, detail="Watchlist not found")
    return crud.remove_movie_from_watchlist(db=db, watchlist_id=watchlist.id, movie_id=movie.movieId)
