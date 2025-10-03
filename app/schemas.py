from pydantic import BaseModel, ConfigDict
from typing import List, Optional
import datetime

# New Genre Schema
class Genre(BaseModel):
    id: int
    name: str

    model_config = ConfigDict(from_attributes=True)

class UserBase(BaseModel):
    email: str
    fullName: str

class UserCreate(UserBase):
    password: str

class User(UserBase):
    id: int
    created_at: datetime.datetime
    favorite_genres: List[Genre] = []
    watchlist_count: int = 0

    model_config = ConfigDict(from_attributes=True)

class UserOut(UserBase):
    id: int
    created_at: datetime.datetime

class RegisterResponse(BaseModel):
    token: str
    user: UserOut

class Token(BaseModel):
    access_token: str
    token_type: str

class TokenData(BaseModel):
    email: Optional[str] = None

class WatchlistCreate(BaseModel):
    name: str

class MovieId(BaseModel):
    movieId: int

class ReviewCreate(BaseModel):
    movieId: int
    rating: float
    text: str

class PasswordResetRequest(BaseModel):
    email: str

class PasswordReset(BaseModel):
    token: str
    newPassword: str

class SocialLogin(BaseModel):
    provider: str
    token: str

class UserPreferences(BaseModel):
    favoriteGenreIds: List[int]

class Message(BaseModel):
    message: str

class RecentSearch(BaseModel):
    id: int
    query: str
    created_at: datetime.datetime

    model_config = ConfigDict(from_attributes=True)

class MovieDetails(BaseModel):
    id: int
    title: str
    poster_path: Optional[str] = None
    release_date: Optional[str] = None
    overview: Optional[str] = None

    model_config = ConfigDict(from_attributes=True)

class Watchlist(BaseModel):
    id: int
    name: str
    movies: List[MovieDetails] = []

    model_config = ConfigDict(from_attributes=True)
