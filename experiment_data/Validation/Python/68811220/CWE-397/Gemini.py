import json
from jose import jwt, JWTError
from typing import Optional, List
from datetime import datetime, timedelta
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm, SecurityScopes
from fastapi import APIRouter, Depends, HTTPException, status, Security
from tinydb import TinyDB, where
from tinydb import Query
from passlib.hash import bcrypt
from pydantic import BaseModel
from passlib.context import CryptContext

class TokenData(BaseModel):
    username: Optional[str] = None
    scopes: List[str] = []  # Add scopes to the token data

class Token(BaseModel):
    access_token: str
    token_type: str

router = APIRouter()
SECRET_KEY = "e79b2a1eaa2b801bc81c49127ca4607749cc2629f73518194f528fc5c8491713"
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 1
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/dev-service/api/v1/openvpn/token", scopes={"admin": "Admin access", "user": "User access"}) # Add scopes to OAuth2 scheme
db = TinyDB('app/Users.json')
Users = db.table('User')
User = Query

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

class User(BaseModel):
    username: str
    password: str
    scopes: List[str] = [] # Add scopes to the User model


def get_user(username: str):
    user = Users.search((where('name') == username))
    if user:
        return user[0]

@router.post('/verif')
async def verify_user(name, password):
    user = Users.search((where('name') == name))
    if not user:
        return False
    passw = user[0]['password']
    if not bcrypt.verify(password, passw):
        return False
    return user


def create_access_token(data: dict, expires_delta: Optional[timedelta] = None):
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        expire = datetime.utcnow() + timedelta(minutes=1)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt


@router.post("/token", response_model=Token)
async def token_generate(form_data: OAuth2PasswordRequestForm = Depends()):
    user_dict = await verify_user(form_data.username, form_data.password)
    if not user_dict:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )

    user = User(**user_dict)  # Create a Pydantic model instance

    access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_access_token(
        data={"sub": user.username, "scopes": user.scopes},  # Include scopes in the token
        expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}



@router.get('/user/me')
async def get_current_user(security_scopes: SecurityScopes = Security(oauth2_scheme)):
    try:
        payload = jwt.decode(security_scopes.token, SECRET_KEY, algorithms=[ALGORITHM])
        username: str = payload.get("sub")
        token_scopes: List[str] = payload.get("scopes", []) # Extract scopes from token
        if username is None:
            raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid token")
        token_data = TokenData(username=username, scopes=token_scopes)
        
        user = Users.search(where('name') == token_data.username)
        if not user:
            raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="User not found")


        # Scope verification
        for scope in security_scopes.scopes:  # Check if required scopes are present in the token
            if scope not in token_scopes:
                raise HTTPException(
                    status_code=status.HTTP_403_FORBIDDEN,
                    detail="Not enough permissions",
                )


        return user[0]

    except (JWTError, json.JSONDecodeError) as e:  # Catch specific exceptions JWTError and JSONDecodeError
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail=f"Invalid token: {str(e)}")



@router.post('/user')
async def create_user(user: User): # Use Pydantic model for user creation
    hashed_password = bcrypt.hash(user.password)
    Users.insert({'name': user.username, 'password': hashed_password, 'scopes': user.scopes})
    return True



@router.get("/items")
async def read_items(security_scopes: SecurityScopes = Security(oauth2_scheme)):
    return [{"item": "Foo", "owner": "you"}]