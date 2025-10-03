from app.database import SessionLocal, engine, Base
from app.models import Genre

def initialize_database():
    # Create all tables
    print("Creating database and tables...")
    Base.metadata.create_all(bind=engine)
    print("Database and tables created successfully.")

    # List of standard movie genres from TMDB
    genres_to_seed = [
        {"id": 28, "name": "Action"},
        {"id": 12, "name": "Adventure"},
        {"id": 16, "name": "Animation"},
        {"id": 35, "name": "Comedy"},
        {"id": 80, "name": "Crime"},
        {"id": 99, "name": "Documentary"},
        {"id": 18, "name": "Drama"},
        {"id": 10751, "name": "Family"},
        {"id": 14, "name": "Fantasy"},
        {"id": 36, "name": "History"},
        {"id": 27, "name": "Horror"},
        {"id": 10402, "name": "Music"},
        {"id": 9648, "name": "Mystery"},
        {"id": 10749, "name": "Romance"},
        {"id": 878, "name": "Science Fiction"},
        {"id": 10770, "name": "TV Movie"},
        {"id": 53, "name": "Thriller"},
        {"id": 10752, "name": "War"},
        {"id": 37, "name": "Western"}
    ]

    db = SessionLocal()
    try:
        print("\nSeeding genres...")
        genre_count = 0
        for genre_data in genres_to_seed:
            exists = db.query(Genre).filter(Genre.id == genre_data["id"]).first()
            if not exists:
                db_genre = Genre(id=genre_data["id"], name=genre_data["name"])
                db.add(db_genre)
                genre_count += 1
                print(f"  Added: {genre_data['name']}")
        
        if genre_count > 0:
            db.commit()
            print(f"Successfully added {genre_count} new genres.")
        else:
            print("All genres already exist in the database.")

    except Exception as e:
        print(f"An error occurred during seeding: {e}")
        db.rollback()
    finally:
        db.close()

if __name__ == "__main__":
    initialize_database()
