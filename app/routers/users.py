from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session, joinedload
from app import schemas, crud, models
from app.database import SessionLocal
from app.security import get_current_user

router = APIRouter()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


@router.get("/me", response_model=schemas.User)
def read_users_me(db: Session = Depends(get_db), current_user: models.User = Depends(get_current_user)):
    user = db.query(models.User).options(
        joinedload(models.User.favorite_genres),
        joinedload(models.User.watchlists).subqueryload(models.Watchlist.movies)
    ).filter(models.User.id == current_user.id).first()

    watchlist_count = 0
    if user.watchlists:
        # Assuming the first watchlist is the main one
        watchlist_count = len(user.watchlists[0].movies)

    return schemas.User(
        id=user.id,
        email=user.email,
        fullName=user.fullName,
        created_at=user.created_at,
        favorite_genres=user.favorite_genres,
        watchlist_count=watchlist_count
    )

@router.put("/me")
def update_user_me():
    # TODO: Implement user update
    return {"message": "User update not yet implemented"}

@router.put("/me/preferences", response_model=schemas.User)
def update_user_preferences_me(
    preferences: schemas.UserPreferences,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user)
):
    updated_user = crud.update_user_favorite_genres(
        db=db, user_id=current_user.id, genre_ids=preferences.favoriteGenreIds
    )
    if not updated_user:
        raise HTTPException(status_code=404, detail="User not found")
    return updated_user

