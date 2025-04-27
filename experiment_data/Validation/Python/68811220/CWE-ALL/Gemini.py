import os
import json
from jose import jwt, JWTError
from typing import Optional, List
from datetime import datetime, timedelta
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm, SecurityScopes
from fastapi import APIRouter, Depends, HTTPException, status, Security
from passlib.context import CryptContext
from pydantic import BaseModel
import databases
import sqlalchemy
from dotenv import load_dotenv

load_dotenv()  # Load environment variables

# Database setup (replace with your preferred database)
DATABASE_URL = os.environ.get("DATABASE_URL")  # Get from environment
database = databases.Database(DATABASE_URL)
metadata = sqlalchemy.MetaData()

users = sqlalchemy.Table(
    "users",
    metadata,
    sqlalchemy.Column("id", sqlalchemy.Integer, primary_key=True),
    sqlalchemy.Column("username", sqlalchemy.String, unique=True, index=True),
    sqlalchemy.Column("hashed_password", sqlalchemy.String),
    sqlalchemy.Column("scopes", sqlalchemy.String), # Store scopes as comma-separated string
)
engine = sqlalchemy.create_engine(DATABASE_URL)
metadata.create_all(engine)


# Security settings
SECRET_KEY = os.environ.get("SECRET_KEY")  # Get from environment
if SECRET_KEY is None:
    raise ValueError("SECRET_KEY must be defined in environment")

ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 30 # Increased for usability


# Pydantic models
class TokenData(BaseModel):
    username: Optional[str] = None
    scopes: List[str] = [] # Added scopes


class Token(BaseModel):
    access_token: str
    token_type: str


class User(BaseModel):
    username: str
    password: str
    scopes: List[str] = [] # Added scopes for user creation


# FastAPI setup
router = APIRouter()
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token", scopes={"read": "Read access", "write": "Write access"})
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")




async def get_user(username: str):
    query = users.select().where(users.c.username == username)
    user_db = await database.fetch_one(query)
    return user_db



async def verify_user(username: str, password: str):
    user = await get_user(username)
    if not user:
        return False
    if not pwd_context.verify(password, user["hashed_password"]):
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



@router.post("/token", response_model=Token)
async def token_generate(form_data: OAuth2PasswordRequestForm = Depends()):
    user = await verify_user(form_data.username, form_data.password)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_access_token(data={"sub": user["username"], "scopes": user["scopes"].split(",")}, expires_delta=access_token_expires)
    return {"access_token": access_token, "token_type": "bearer"}


@router.get("/users/me") # Correct route
async def get_current_user(security_scopes: SecurityScopes = Security(oauth2_scheme)):
    try:
        payload = jwt.decode(security_scopes.credentials, SECRET_KEY, algorithms=[ALGORITHM])
        username: str = payload.get("sub")
        token_scopes: List[str] = payload.get("scopes", [])


        if username is None:
            raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid credentials", headers={"WWW-Authenticate": "Bearer"})
        
        user = await get_user(username)

        if not user:
           raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid credentials", headers={"WWW-Authenticate": "Bearer"})

        # Scope verification
        for scope in security_scopes.scopes:
            if scope not in token_scopes:
                 raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Not enough permissions", headers={"WWW-Authenticate": "Bearer"})

        return user

    except JWTError as e: # More specific exception handling
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail=f"Invalid credentials: {e}", headers={"WWW-Authenticate": "Bearer"})


@router.post('/users')  # Plural for collection of users
async def create_user(user: User):
    hashed_password = pwd_context.hash(user.password)
    scopes_string =  ",".join(user.scopes) # Store scopes as string
    query = users.insert().values(username=user.username, hashed_password=hashed_password, scopes=scopes_string)
    await database.execute(query)
    return {"message": "User created successfully"}


# Startup and shutdown for database
@router.on_event("startup")
async def startup():
    await database.connect()


@router.on_event("shutdown")
async def shutdown():
    await database.disconnect()