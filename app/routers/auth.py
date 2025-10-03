from fastapi import APIRouter, Depends, HTTPException, status, Form
from sqlalchemy.orm import Session
from app import crud, schemas, security
from app.database import SessionLocal
from datetime import timedelta, datetime, UTC
from jose import jwt
import uuid

router = APIRouter()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@router.post("/register", response_model=schemas.Token)
def register(user: schemas.UserCreate, db: Session = Depends(get_db)):
    print(f"Attempting to register user: {user.email}") # Add this for debugging
    db_user = crud.get_user_by_email(db, email=user.email)
    if db_user:
        raise HTTPException(status_code=400, detail="Email already registered")
    
    created_user = crud.create_user(db=db, user=user)
    
    access_token_expires = timedelta(minutes=security.ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = security.create_access_token(
        data={"sub": created_user.email}, expires_delta=access_token_expires
    )
    
    return {"access_token": access_token, "token_type": "bearer"}

@router.post("/login", response_model=schemas.Token)
def login(db: Session = Depends(get_db), username: str = Form(...), password: str = Form(...)):
    print(f"Attempting login for user: {username}")  # Add this line for debugging
    user = crud.get_user_by_email(db, email=username)
    if not user or not security.verify_password(password, user.hashed_password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    access_token_expires = timedelta(minutes=security.ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = security.create_access_token(
        data={"sub": user.email}, expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}

@router.post("/social", response_model=schemas.Token)
def social_login(social_login: schemas.SocialLogin, db: Session = Depends(get_db)):
    # In a real application, you would verify the token with the social provider.
    # For this example, we'll just create a new user or log in an existing one.
    email = f"{social_login.provider}_{social_login.token}@example.com" # Placeholder email
    user = crud.get_user_by_email(db, email=email)
    if not user:
        # Create a dummy password for social users, as they won't use it directly
        user_create = schemas.UserCreate(fullName=email, email=email, password=str(uuid.uuid4()))
        user = crud.create_user(db, user_create)

    access_token_expires = timedelta(minutes=security.ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = security.create_access_token(
        data={"sub": user.email}, expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}

@router.post("/logout")
def logout(token: str = Depends(security.oauth2_scheme), db: Session = Depends(get_db)):
    try:
        payload = jwt.decode(token, security.SECRET_KEY, algorithms=[security.ALGORITHM])
        jti = payload.get("jti")
        if jti:
            crud.add_token_to_blocklist(db, jti)
        return {"message": "Successfully logged out"}
    except jwt.JWTError:
        raise HTTPException(status_code=401, detail="Invalid token")

@router.post("/password/request-reset")
def request_password_reset(request: schemas.PasswordResetRequest, db: Session = Depends(get_db)):
    user = crud.get_user_by_email(db, email=request.email)
    if not user:
        # For security, don't reveal if the email is not registered
        return {"message": "If an account with that email exists, a password reset link has been sent."}

    token = str(uuid.uuid4())
    expires_at = datetime.now(UTC) + timedelta(hours=1) # Token valid for 1 hour
    crud.create_password_reset_token(db, email=user.email, token=token, expires_at=expires_at)

    # In a real application, you would send an email here.
    print(f"Password reset link for {user.email}: /reset-password?token={token}")
    return {"message": "If an account with that email exists, a password reset link has been sent."}

@router.post("/password/reset")
def reset_password(request: schemas.PasswordReset, db: Session = Depends(get_db)):
    reset_token_entry = crud.get_password_reset_token(db, token=request.token)

    if not reset_token_entry or reset_token_entry.expires_at < datetime.now(UTC):
        raise HTTPException(status_code=400, detail="Invalid or expired reset token")

    user = crud.get_user_by_email(db, email=reset_token_entry.email)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    hashed_password = security.get_password_hash(request.newPassword)
    user.hashed_password = hashed_password
    db.add(user)
    db.commit()
    db.refresh(user)

    crud.delete_password_reset_token(db, token=request.token) # Invalidate token after use
    return {"message": "Password has been reset successfully."}

@router.post("/guest")
def guest_login():
    # Generate a temporary token for guest users
    access_token_expires = timedelta(minutes=security.GUEST_TOKEN_EXPIRE_MINUTES) # Define this in security.py
    guest_token = security.create_access_token(
        data={"sub": "guest"}, expires_delta=access_token_expires
    )
    return {"guestToken": guest_token}
