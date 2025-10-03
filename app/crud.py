from sqlalchemy.orm import Session, joinedload
from . import models, schemas
from .security import get_password_hash
import datetime

def get_user_by_email(db: Session, email: str):
    return db.query(models.User).options(joinedload(models.User.favorite_genres)).filter(models.User.email == email).first()

def create_user(db: Session, user: schemas.UserCreate):
    hashed_password = get_password_hash(user.password)
    db_user = models.User(email=user.email, fullName=user.fullName, hashed_password=hashed_password)
    db.add(db_user)
    db.commit()
    db.refresh(db_user)
    # Also create a default watchlist for the new user
    create_user_watchlist(db=db, user=db_user)
    return db_user

def add_token_to_blocklist(db: Session, jti: str):
    db_token = models.TokenBlocklist(jti=jti)
    db.add(db_token)
    db.commit()
    db.refresh(db_token)
    return db_token

def is_token_blocklisted(db: Session, jti: str):
    return db.query(models.TokenBlocklist).filter(models.TokenBlocklist.jti == jti).first() is not None

def create_password_reset_token(db: Session, email: str, token: str, expires_at: datetime):
    db_token = models.PasswordResetToken(email=email, token=token, expires_at=expires_at)
    db.add(db_token)
    db.commit()
    db.refresh(db_token)
    return db_token

def get_password_reset_token(db: Session, token: str):
    return db.query(models.PasswordResetToken).filter(models.PasswordResetToken.token == token).first()

def delete_password_reset_token(db: Session, token: str):
    db.query(models.PasswordResetToken).filter(models.PasswordResetToken.token == token).delete()
    db.commit()

def update_user_favorite_genres(db: Session, user_id: int, genre_ids: list[int]):
    print(f"--- CRUD: Updating genres for user_id: {user_id} ---")
    user = db.query(models.User).filter(models.User.id == user_id).first()
    if not user:
        print("--- CRUD: User not found! ---")
        return None
    
    print(f"--- CRUD: Found user: {user.email} ---")
    print(f"--- CRUD: Received genre IDs: {genre_ids} ---")

    # Fetch the new genre objects in a single query
    new_favorite_genres = db.query(models.Genre).filter(models.Genre.id.in_(genre_ids)).all()
    print(f"--- CRUD: Fetched genre objects: {[g.name for g in new_favorite_genres]} ---")

    # Directly assign the new list to the relationship
    user.favorite_genres = new_favorite_genres
    print("--- CRUD: Assigned new genres to user object. ---")
            
    print("--- CRUD: Committing changes... ---")
    db.commit()
    print("--- CRUD: Commit complete. ---")

    db.refresh(user)
    print(f"--- CRUD: Refreshed user. Genres are now: {[g.name for g in user.favorite_genres]} ---")
    return user

# Watchlist CRUD functions

def get_user_watchlist(db: Session, user_id: int):
    return db.query(models.Watchlist).filter(models.Watchlist.user_id == user_id).first()

def get_user_watchlists(db: Session, user_id: int):
    return db.query(models.Watchlist).filter(models.Watchlist.user_id == user_id).all()

def create_user_watchlist(db: Session, user: models.User, name: str = "My Watchlist"):
    db_watchlist = models.Watchlist(name=name, user_id=user.id)
    db.add(db_watchlist)
    db.commit()
    db.refresh(db_watchlist)
    return db_watchlist

def add_movie_to_watchlist(db: Session, watchlist_id: int, movie_id: int):
    watchlist = db.query(models.Watchlist).filter(models.Watchlist.id == watchlist_id).first()
    if not watchlist:
        return None

    # Check if movie exists, if not, create it
    movie = db.query(models.Movie).filter(models.Movie.id == movie_id).first()
    if not movie:
        # In a real app, you might want to fetch movie details from TMDB here
        # For now, we'll just create a placeholder
        movie = models.Movie(id=movie_id, title="Movie Title") # Placeholder title
        db.add(movie)
    
    if movie not in watchlist.movies:
        watchlist.movies.append(movie)
        db.commit()
    
    return watchlist

def remove_movie_from_watchlist(db: Session, watchlist_id: int, movie_id: int):
    watchlist = db.query(models.Watchlist).filter(models.Watchlist.id == watchlist_id).first()
    if not watchlist:
        return None

    movie = db.query(models.Movie).filter(models.Movie.id == movie_id).first()
    if movie and movie in watchlist.movies:
        watchlist.movies.remove(movie)
        db.commit()
    
    return watchlist

def create_recent_search(db: Session, user_id: int, query: str):
    db_search = models.RecentSearch(query=query, user_id=user_id)
    db.add(db_search)
    db.commit()
    db.refresh(db_search)
    return db_search

def get_recent_searches(db: Session, user_id: int, limit: int = 5):
    return db.query(models.RecentSearch).filter(models.RecentSearch.user_id == user_id).order_by(models.RecentSearch.created_at.desc()).limit(limit).all()
