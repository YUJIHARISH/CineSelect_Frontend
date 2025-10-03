from app.database import Base, engine
from app import models

print("Creating database and tables...")
Base.metadata.create_all(bind=engine)
print("Database and tables created successfully.")

