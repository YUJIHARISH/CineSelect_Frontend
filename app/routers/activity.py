from fastapi import APIRouter, Depends
from app import schemas, crud, models
from sqlalchemy.orm import Session
from app.database import SessionLocal
from app.security import get_current_user

router = APIRouter()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@router.post("/reviews")
def submit_review(review: schemas.ReviewCreate):
    return {"message": "Review submitted."}

@router.get("/recent-searches", response_model=list[schemas.RecentSearch])
def get_recent_searches(db: Session = Depends(get_db), current_user: models.User = Depends(get_current_user)):
    return crud.get_recent_searches(db=db, user_id=current_user.id)

@router.post("/saved-searches")
def save_search(name: str, filters: dict):
    return {"message": "Search saved."}

@router.delete("/saved-searches/{id}")
def delete_saved_search(id: int):
    return {"message": f"Saved search with id {id} deleted."}
