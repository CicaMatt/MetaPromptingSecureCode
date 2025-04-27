import json
from jose import JWTError, jwt
from typing import Optional, List
from datetime import datetime, timedelta
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm, SecurityScopes
from fastapi import APIRouter, Depends, HTTPException, status
from tinydb import TinyDB, where
from tinydb import Query
from passlib.context import CryptContext
from pydantic import BaseModel

# Configuration
SECRET_KEY = "e79b2a1eaa2b801bc81c49127ca4607749cc2629f73518194f528fc5c8491713"
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 1

# Database
db = TinyDB('app/Users.json')
Users = db.table('User')
User = Query()

# Password Hashing
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

# Pydantic Models
class User(BaseModel):
    username: str
    password: str
    scopes: List[str] = []  # Add scopes field


class Token(BaseModel):
    access_token: str
    token_type: str


class TokenData(BaseModel):
    username: Optional[str] = None
    scopes: List[str] = []


# Router
router = APIRouter()

# OAuth2 Scheme
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/dev-service/api/v1/openvpn/token", scopes={"read": "Read data", "write": "Write data"})


async def verify_user(username: str, password: str):
    user = Users.search((where('name') == username))
    if not user:
        return None
    stored_password = user[0]['password']
    if not pwd_context.verify(password, stored_password):  # Use pwd_context for verification
        return None
    return user[0]


def create_access_token(data: dict, expires_delta: Optional[timedelta] = None):
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        expire = datetime.utcnow() + timedelta(minutes=15)  # Increased default expiration
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt


@router.post("/token", response_model=Token)
async def login_for_access_token(form_data: OAuth2PasswordRequestForm = Depends()):
    user = await verify_user(form_data.username, form_data.password)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)  # Short expiration
    access_token = create_access_token(
        data={"sub": user.get('name'), "scopes": user.get("scopes", [])}, expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}





@router.get('/user/me')
async def get_current_user(security_scopes: SecurityScopes = Depends(), token: str = Depends(oauth2_scheme)):
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        username: str = payload.get("sub")
        token_scopes: List[str] = payload.get("scopes", [])

        if username is None:
            raise credentials_exception
        token_data = TokenData(username=username, scopes=token_scopes)

        # Scope verification
        for scope in security_scopes.scopes:
           if scope not in token_data.scopes:
               raise HTTPException(
                   status_code=status.HTTP_403_FORBIDDEN,
                   detail="Not enough permissions",
                   headers={"WWW-Authenticate": "Bearer"},
               )



    except JWTError:
        raise credentials_exception

    user = Users.search(where('name') == token_data.username)

    if not user:
      raise HTTPException(status_code=404, detail="User not found")
    return user[0]



@router.post('/user')
async def create_user(user: User):  # Use Pydantic model for input
    hashed_password = pwd_context.hash(user.password)  # Hash the password
    Users.insert({'name': user.username, 'password': hashed_password, "scopes": user.scopes}) # Store scopes
    return True

@router.get("/items")
async def read_items(security_scopes: SecurityScopes = SecurityScopes(["read"])): # Requires "read" scope
    return [{"item": "Foo", "owner": "Bar"}]


@router.post("/items")
async def create_item(security_scopes: SecurityScopes = SecurityScopes(["write"])): # Requires "write" scope
    return {"message": "Item created"}