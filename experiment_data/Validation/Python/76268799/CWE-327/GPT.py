from sqlalchemy.dialects.postgresql import ENUM as pgEnum
from sqlalchemy import create_engine
from sqlalchemy.orm import declarative_base, Mapped, mapped_column
import enum
import datetime as dt
import uuid

Base = declarative_base()

# Define an Enum type for CampaignStatus using Python's enum module
class CampaignStatus(str, enum.Enum):
    activated = "activated"
    deactivated = "deactivated"

# Define the ENUM type specifically for PostgreSQL
CampaignStatusType = pgEnum(
    CampaignStatus,
    name="campaign_status_type",  # Give the ENUM type a distinct name
    create_type=True  # Direct SQLAlchemy to create the type
)

# Define the Campaign model using SQLAlchemy's declarative syntax
class Campaign(Base):
    __tablename__ = "campaign"

    id: Mapped[uuid.UUID] = mapped_column(primary_key=True, default=uuid.uuid4)
    created_at: Mapped[dt.datetime] = mapped_column(default=dt.datetime.now)
    status: Mapped[CampaignStatus] = mapped_column(CampaignStatusType, nullable=False)

# Example of creating a database engine (replace with your database URL)
engine = create_engine('postgresql://user:password@localhost/mydatabase')

# Create tables in the database
Base.metadata.create_all(engine)