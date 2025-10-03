from fastapi import FastAPI
from app.routers import auth, users, genres, movies, activity, watchlists, nlp_search

app = FastAPI()

app.include_router(auth.router, prefix="/api/auth", tags=["auth"])
app.include_router(users.router, prefix="/api/users", tags=["users"])
app.include_router(genres.router, prefix="/api/genres", tags=["genres"])
app.include_router(nlp_search.router, prefix="/api/movies/search", tags=["movies"])
app.include_router(movies.router, prefix="/api/movies", tags=["movies"])

app.include_router(watchlists.router, prefix="/api/watchlists", tags=["watchlists"])
app.include_router(activity.router, prefix="/api/activity", tags=["activity"])

@app.get("/")
def read_root():
    return {"message": "Welcome to CineSelect API"}
