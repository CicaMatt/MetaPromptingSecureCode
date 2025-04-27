import os
import datetime as dt
from uuid import uuid4

import enum
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, DeclarativeBase
from sqlalchemy.dialects.postgresql import ENUM, UUID
from sqlalchemy import Column
from sqlalchemy.orm import Mapped, mapped_column

# Securely retrieve credentials from environment variables
DB_USER = os.environ.get("DB_USER")
DB_PASSWORD = os.environ.get("DB_PASSWORD")
DB_HOST = os.environ.get("DB_HOST")
DB_NAME = os.environ.get("DB_NAME")

if not all([DB_USER, DB_PASSWORD, DB_HOST, DB_NAME]):
    raise ValueError("Database credentials not set in environment variables.")


class Base(DeclarativeBase):
    pass


class CampaignStatus(str, enum.Enum):
    activated = "activated"
    deactivated = "deactivated"


CampaignStatusType = ENUM(
    CampaignStatus,
    name="campaignstatus",
    create_constraint=True,
    metadata=Base.metadata,
    validate_strings=True,
)


class Campaign(Base):
    __tablename__ = "campaign"

    id: Mapped[UUID] = mapped_column(primary_key=True, default=uuid4)
    created_at: Mapped[dt.datetime] = mapped_column(
        default=dt.datetime.now
    )
    status = Column("status", CampaignStatusType, nullable=False) # Use Column for enums


# Construct database URL using environment variables
DATABASE_URL = f"postgresql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}/{DB_NAME}"

engine = create_engine(DATABASE_URL)

# Create tables
Base.metadata.create_all(engine)

# Example usage with session:
Session = sessionmaker(bind=engine)
session = Session()

new_campaign = Campaign(status=CampaignStatus.activated)
session.add(new_campaign)
session.commit()

retrieved_campaign = session.query(Campaign).first()
print(f"Retrieved Campaign Status: {retrieved_campaign.status}")

session.close()
status = Column("status", CampaignStatusType, nullable=False)