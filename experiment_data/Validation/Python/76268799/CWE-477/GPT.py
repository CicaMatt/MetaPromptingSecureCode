from sqlalchemy import Enum, create_engine, Column, String
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import Mapped, mapped_column, sessionmaker
from sqlalchemy.dialects.postgresql import ENUM as pgEnum
import enum
import datetime as dt
from uuid import uuid4
import uuid

# Define the declarative base for ORM modeling
Base = declarative_base()

# Define the enumeration for the campaign status
class CampaignStatus(enum.Enum):
    activated = "activated"
    deactivated = "deactivated"

# Define the Campaign model with correct mapped_column usage in SQLAlchemy 2.0
class Campaign(Base):
    __tablename__ = "campaign"

    id: Mapped[uuid.UUID] = mapped_column(default=uuid4, primary_key=True, nullable=False)
    created_at: Mapped[dt.datetime] = mapped_column(default=dt.datetime.now, nullable=False)
    status: Mapped[CampaignStatus] = mapped_column(Enum(CampaignStatus), nullable=False)

# Example configuration for a PostgreSQL database URL
DATABASE_URL = "postgresql://user:password@localhost/testdb"

# Set up the engine and session
engine = create_engine(DATABASE_URL)
Session = sessionmaker(bind=engine)

# Create the database tables
Base.metadata.create_all(engine)

# Now you can use the session to add and query Campaign objects from the database