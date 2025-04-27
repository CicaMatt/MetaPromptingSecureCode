import json
from jose import jwt, JWTError
from typing import Optional, List
from datetime import datetime, timedelta
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm, SecurityScopes
from fastapi import APIRouter, Depends, HTTPException, status, Security
from tinydb import TinyDB, where
from tinydb import Query
from passlib.context import CryptContext
from pydantic import BaseModel
import os
from dotenv import load_dotenv

load_dotenv()  # Load environment variables from .env file

# --- Security Configuration ---
SECRET_KEY = os.getenv("SECRET_KEY")  # Get SECRET_KEY from environment
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 30  # Increased expiration time for demonstration

# --- Database Setup (using TinyDB for simplicity) ---
db = TinyDB('app/Users.json')
Users = db.table('User')
User = Query()

# --- Password Hashing ---
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

# --- Pydantic Models ---
class TokenData(BaseModel):
    username: Optional[str] = None
    scopes: List[str] = []

class Token(BaseModel):
    access_token: str
    token_type: str


class User(BaseModel):
    username: str
    password: str
    scopes: List[str] = [] # Add scopes to the User model


# --- OAuth2 Setup ---
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/dev-service/api/v1/openvpn/token", scopes={"reader": "Read access", "writer": "Write Access"})
router = APIRouter()

# --- Helper Functions ---
def get_user(username: str):
    user = Users.search((where('name') == username))
    if user:
        return user[0]

async def verify_user(username, password):
    user = get_user(username)
    if not user:
        return False
    if not pwd_context.verify(password, user['password']):  # Use pwd_context for verification
        return False
    return user


def create_access_token(data: dict, expires_delta: Optional[timedelta] = None):
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        expire = datetime.utcnow() + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt


# --- API Endpoints ---
@router.post("/token", response_model=Token)
async def token_generate(form_data: OAuth2PasswordRequestForm = Depends()):
    user = await verify_user(form_data.username, form_data.password)
    if not user:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Incorrect username or password", headers={"WWW-Authenticate": "Bearer"})

    access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_access_token(
        data={"sub": user['name'], "scopes": user.get("scopes",[])},  # Include scopes in token data
        expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}



@router.get('/user/me')
async def get_current_user(security_scopes: SecurityScopes = Security(oauth2_scheme), token: str = Depends(oauth2_scheme)):
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        username: str = payload.get("sub")
        token_scopes = payload.get("scopes", [])
        if username is None:
            raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid token", headers={"WWW-Authenticate": "Bearer"})
        
        if security_scopes.scopes and not all(scope in token_scopes for scope in security_scopes.scopes):
            raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Not enough permissions") # Check scopes

        user = get_user(username)

        if not user:
           raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="User not found", headers={"WWW-Authenticate":"Bearer"})

        return user
    except JWTError:  # Catch token expiry and other JWT errors
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid token", headers={"WWW-Authenticate": "Bearer"})



@router.post('/user')
async def create_user(user: User): # Use the Pydantic model for input
    hashed_password = pwd_context.hash(user.password)
    Users.insert({'name': user.username, 'password': hashed_password, 'scopes': user.scopes})
    return True


# --- Example of a protected route with scope requirements ---
@router.get("/items")
async def read_items(security_scopes: SecurityScopes = Security(oauth2_scheme)):
    return [{"item": "Foo", "owner": "Bar"}]