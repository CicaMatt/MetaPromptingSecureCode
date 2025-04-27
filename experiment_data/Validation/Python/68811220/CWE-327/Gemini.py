import json
from datetime import datetime, timedelta
from typing import Optional, List

from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm, SecurityScopes
from jose import JWTError, jwt
from passlib.context import CryptContext
from pydantic import BaseModel
from tinydb import TinyDB, Query, where

# Use strong, vetted algorithms (SHA256 for hashing, HS256 for JWT - suitable for this example)
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto") # bcrypt for password hashing
SECRET_KEY = "e79b2a1eaa2b801bc81c49127ca4607749cc2629f73518194f528fc5c8491713" # Example - Replace with strong, randomly generated key in production
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 1


class TokenData(BaseModel):
    username: Optional[str] = None
    scopes: List[str] = []  # Add scopes


class Token(BaseModel):
    access_token: str
    token_type: str


class User(BaseModel):
    username: str
    password: str
    scopes: List[str] = [] # Add scopes to the User model


oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/dev-service/api/v1/openvpn/token", scopes={"admin": "Admin actions", "user": "User actions"})  # Define scopes
db = TinyDB('app/Users.json')
Users = db.table('User')
User_query = Query()

router = APIRouter()



async def verify_user(username: str, password: str):
    user = Users.search(User_query.username == username)
    if user and pwd_context.verify(password, user[0]["password"]):
        return user[0]
    return None




def create_access_token(data: dict, expires_delta: Optional[timedelta] = None):
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        expire = datetime.utcnow() + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    to_encode.update({"exp": expire.timestamp()}) # Use timestamp for exp
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt



@router.post("/token", response_model=Token)
async def token_generate(form_data: OAuth2PasswordRequestForm = Depends()):
    user = await verify_user(form_data.username, form_data.password)
    if not user:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Incorrect username or password", headers={"WWW-Authenticate": "Bearer"})

    access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_access_token(data={"sub": user["username"], "scopes": user.get("scopes", [])}, expires_delta=access_token_expires) # Include scopes
    return {"access_token": access_token, "token_type": "bearer"}




@router.get('/user/me')
async def get_current_user(security_scopes: SecurityScopes = Depends(), token: str = Depends(oauth2_scheme)):
    credentials_exception = HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Could not validate credentials", headers={"WWW-Authenticate": "Bearer"})
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        username: str = payload.get("sub")
        token_scopes: List[str] = payload.get("scopes", [])  # Get scopes from token
        if username is None:
            raise credentials_exception
        
        # Check scopes
        for scope in security_scopes.scopes:
            if scope not in token_scopes:
                raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Not enough permissions")  


        user = Users.get(User_query.username == username)
        if user is None:
            raise credentials_exception
        return user
    except (JWTError, Exception) as e: # Catch other potential exceptions during decoding
        print(f"Error during token decoding: {e}")
        raise credentials_exception


@router.post('/user')
async def create_user(user: User): #Use the Pydantic model for input
    hashed_password = pwd_context.hash(user.password)
    Users.insert({'username': user.username, 'password': hashed_password, 'scopes': user.scopes}) # Store hashed password and scopes
    return True


# Example endpoint requiring the 'admin' scope
@router.post('/admin_action')
async def admin_action(current_user=Depends(get_current_user)):
    return {"message": "Admin action performed", "user": current_user}


# Example endpoint requiring the 'user' scope
@router.get('/user_action')
async def user_action(current_user=Depends(get_current_user)):
    return {"message": "User action performed", "user": current_user}