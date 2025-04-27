from sqlalchemy import create_engine, Column, DateTime
from sqlalchemy.dialects.postgresql import ENUM as pgEnum
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Mapped, mapped_column
from sqlalchemy.types import Enum
import enum
import datetime as dt
from uuid import uuid4, UUID

# Define the database URL and create engine
DATABASE_URL = "postgresql://user:password@localhost/mydatabase"
engine = create_engine(DATABASE_URL)

# Define a base class for declarative class definitions
Base = declarative_base()

# Enum definition
class CampaignStatus(str, enum.Enum):
    ACTIVATED = "activated"
    DEACTIVATED = "deactivated"

# Properly define the PostgreSQL ENUM type
CampaignStatusType = pgEnum(
    CampaignStatus,
    name='campaignstatus',
    create_type=True,
)

# Define the Campaign model
class Campaign(Base):
    __tablename__ = 'campaign'

    id: Mapped[UUID] = mapped_column(primary_key=True, default=uuid4)
    created_at: Mapped[dt.datetime] = mapped_column(default=dt.datetime.utcnow)  # Use utcnow for time consistency
    status: Mapped[CampaignStatus] = mapped_column(CampaignStatusType, nullable=False)

# Create tables in the database
Base.metadata.create_all(engine)

# Create a session
Session = sessionmaker(bind=engine)
session = Session()

# Example of usage: create a new campaign
new_campaign = Campaign(status=CampaignStatus.ACTIVATED)
session.add(new_campaign)
session.commit()

# Close the session
session.close()