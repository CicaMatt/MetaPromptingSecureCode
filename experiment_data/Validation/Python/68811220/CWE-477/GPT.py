import json
from jose import JWTError, jwt
from typing import Optional
from datetime import datetime, timedelta
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm, SecurityScopes
from fastapi import APIRouter, Depends, HTTPException, status
from tinydb import TinyDB, where
from tinydb import Query
from passlib.hash import bcrypt
from pydantic import BaseModel
from passlib.context import CryptContext

# Define data models
class TokenData(BaseModel):
    username: Optional[str] = None
    scopes: list[str] = []

class Token(BaseModel):
    access_token: str
    token_type: str

router = APIRouter()

# Security configurations
SECRET_KEY = "e79b2a1eaa2b801bc81c49127ca4607749cc2629f73518194f528fc5c8491713"
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 1
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/dev-service/api/v1/openvpn/token")

# Database configurations
db = TinyDB('app/Users.json')
Users = db.table('User')

# Password hashing
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

class User(BaseModel):
    username: str
    password: str
    scopes: list[str] = []

def get_user(username: str):
    user = Users.search(where('name') == username)
    return user[0] if user else None

@router.post('/verif')
async def verify_user(name, password):
    user = get_user(name)
    if user and bcrypt.verify(password, user['password']):
        return user
    return None

def create_access_token(data: dict, expires_delta: Optional[timedelta] = None):
    to_encode = data.copy()
    expire = datetime.utcnow() + (expires_delta or timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES))
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt

@router.post("/token", response_model=Token)
async def token_generate(form_data: OAuth2PasswordRequestForm = Depends()):
    user = await verify_user(form_data.username, form_data.password)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    scopes = user.get('scopes', [])
    access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_access_token(
        data={"sub": user['name'], "scopes": scopes},
        expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}

@router.get('/user/me')
async def get_current_user(security_scopes: SecurityScopes, token: str = Depends(oauth2_scheme)):
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": f"Bearer scope={security_scopes.scope_str}"},
    )
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        username: str = payload.get("sub")
        token_scopes = payload.get("scopes", [])
        if username is None or not token_scopes or not set(token_scopes).issuperset(set(security_scopes.scopes)):
            raise credentials_exception
        token_data = TokenData(username=username, scopes=token_scopes)
    except JWTError:
        raise credentials_exception
    
    user = get_user(token_data.username)
    if not user:
        raise credentials_exception
    return user

@router.post('/user')
async def create_user(name: str, password: str):
    if get_user(name):
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="User already exists")
    hashed_password = bcrypt.hash(password)
    Users.insert({'name': name, 'password': hashed_password, 'scopes': []})
    return {"msg": "User created successfully"}