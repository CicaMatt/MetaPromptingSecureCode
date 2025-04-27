import os
from datetime import datetime, timedelta
from typing import Optional, List
from fastapi import APIRouter, Depends, HTTPException, status, Security
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm, SecurityScopes
from jose import jwt, JWTError
from pydantic import BaseModel, ValidationError
from passlib.context import CryptContext
from tinydb import TinyDB, where, Query

# Load environment variables for sensitive data (e.g., SECRET_KEY)
SECRET_KEY = os.getenv("SECRET_KEY", "e79b2a1eaa2b801bc81c49127ca4607749cc2629f73518194f528fc5c8491713")
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 1

# Initialize FastAPI router
router = APIRouter()

# Initialize TinyDB for user storage
db = TinyDB('app/Users.json')
Users = db.table('User')
User = Query

# Password hashing context
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

# OAuth2 scheme for token authentication
oauth2_scheme = OAuth2PasswordBearer(
    tokenUrl="/dev-service/api/v1/openvpn/token",
    scopes={"read": "Read access", "write": "Write access"}
)

# Pydantic models for data validation
class TokenData(BaseModel):
    username: Optional[str] = None
    scopes: List[str] = []

class Token(BaseModel):
    access_token: str
    token_type: str

class User(BaseModel):
    username: str
    password: str
    scopes: List[str] = []

# Helper functions
def get_user(username: str) -> Optional[dict]:
    """Fetch user from the database by username."""
    user = Users.search(where('username') == username)
    return user[0] if user else None

def verify_password(plain_password: str, hashed_password: str) -> bool:
    """Verify the provided password against the hashed password."""
    return pwd_context.verify(plain_password, hashed_password)

def create_access_token(data: dict, expires_delta: Optional[timedelta] = None) -> str:
    """Create a JWT token with an optional expiration time."""
    to_encode = data.copy()
    expire = datetime.utcnow() + (expires_delta if expires_delta else timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES))
    to_encode.update({"exp": expire})
    return jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)

# Authentication and token generation
@router.post("/token", response_model=Token)
async def token_generate(form_data: OAuth2PasswordRequestForm = Depends()):
    """Generate a JWT token for authenticated users."""
    user = get_user(form_data.username)
    if not user or not verify_password(form_data.password, user['password']):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_access_token(
        data={"sub": user['username'], "scopes": form_data.scopes},
        expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}

# Protected endpoint with token validation and scope checking
@router.get('/user/me')
async def get_current_user(
    security_scopes: SecurityScopes,
    token: str = Depends(oauth2_scheme)
):
    """Fetch the current user's details using the provided JWT token."""
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        username: str = payload.get("sub")
        if username is None:
            raise credentials_exception
        token_scopes = payload.get("scopes", [])
        token_data = TokenData(username=username, scopes=token_scopes)
    except (JWTError, ValidationError):
        raise credentials_exception

    # Check if the required scopes are present
    for scope in security_scopes.scopes:
        if scope not in token_data.scopes:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Not enough permissions",
                headers={"WWW-Authenticate": f"Bearer scope={security_scopes.scope_str}"},
            )

    user = get_user(token_data.username)
    if user is None:
        raise credentials_exception
    return user

# User creation endpoint
@router.post('/user')
async def create_user(username: str, password: str, scopes: List[str] = []):
    """Create a new user with the provided username, password, and scopes."""
    if get_user(username):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Username already exists",
        )
    Users.insert({
        'username': username,
        'password': pwd_context.hash(password),
        'scopes': scopes
    })
    return {"message": "User created successfully"}