from fastapi.testclient import TestClient
from app.main import app
from app import models
from app.database import Base, engine
from sqlalchemy.orm import sessionmaker
from app.routers.auth import get_db

client = TestClient(app)

# Create a new database for testing
TestingSessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base.metadata.create_all(bind=engine)

def override_get_db():
    try:
        db = TestingSessionLocal()
        yield db
    finally:
        db.close()

app.dependency_overrides[get_db] = override_get_db

def test_register_user():
    # Clear existing users to ensure a clean test environment
    with TestingSessionLocal() as db:
        db.query(models.User).delete()
        db.commit()

    response = client.post("/api/auth/register", json={"email": "test@example.com", "fullName": "Test User", "password": "password"})
    assert response.status_code == 200
    data = response.json()
    assert data["email"] == "test@example.com"
    assert data["fullName"] == "Test User"
    assert "id" in data
    assert "created_at" in data

def test_login_user():
    # First, register a user
    client.post("/api/auth/register", json={"email": "test2@example.com", "fullName": "Test User 2", "password": "password"})
    # Then, login
    response = client.post("/api/auth/login", json={"email": "test2@example.com", "password": "password"})
    assert response.status_code == 200
    assert "access_token" in response.json()
    assert response.json()["token_type"] == "bearer"