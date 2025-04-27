import datetime as dt
from enum import Enum
from uuid import UUID, uuid4

from sqlalchemy import Integer, String
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, DeclarativeMeta

from sqlalchemy.dialects.postgresql import ENUM


class Base(DeclarativeBase):
    pass


class CampaignStatus(str, Enum):
    activated = "activated"
    deactivated = "deactivated"


class Campaign(Base):
    __tablename__ = "campaign"

    id: Mapped[UUID] = mapped_column(primary_key=True, default=uuid4)
    created_at: Mapped[dt.datetime] = mapped_column(default=dt.datetime.now)
    status: Mapped[str] = mapped_column(ENUM(CampaignStatus), nullable=False)  # Correct Usage
    # Example of additional fields using strong cryptography (if applicable):
    # sensitive_data: Mapped[str] = mapped_column(String, nullable=True) # Store encrypted data here
    # Note:  Actual encryption and decryption logic using Fernet or similar would be handled
    #        separately, likely during pre-save/post-load events, not directly in the model.


# Example usage (ensure your database connection is configured):
from sqlalchemy import create_engine

engine = create_engine("postgresql://user:password@host:port/database")  # Replace with your DB details
Base.metadata.create_all(engine)

from sqlalchemy.orm import Session

with Session(engine) as session:
    campaign1 = Campaign(status=CampaignStatus.activated)  # Use Enum member
    session.add(campaign1)
    session.commit()

    retrieved_campaign = session.query(Campaign).first()
    print(retrieved_campaign.status) # Prints CampaignStatus.activated
    print(type(retrieved_campaign.status)) # Prints <enum 'CampaignStatus'>