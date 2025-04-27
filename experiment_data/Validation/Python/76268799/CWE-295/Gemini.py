import datetime as dt
from enum import Enum

import sqlalchemy as sa
from sqlalchemy import create_engine
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column
from sqlalchemy.dialects.postgresql import ENUM
from uuid import uuid4

class Base(DeclarativeBase):
    pass

class CampaignStatus(Enum):
    activated = "activated"
    deactivated = "deactivated"

CampaignStatusType = sa.Enum(
    CampaignStatus,
    name="campaignstatus",
    create_type=True,  # Important for SQLAlchemy 2.0
    validate_strings=True,
)

class Campaign(Base):
    __tablename__ = "campaign"

    id: Mapped[uuid4] = mapped_column(primary_key=True, default=uuid4)
    created_at: Mapped[dt.datetime] = mapped_column(default=dt.datetime.now)
    status: Mapped[CampaignStatusType] = mapped_column(nullable=False)


# Example usage (requires a PostgreSQL database connection):
engine = create_engine("postgresql://user:password@host:port/database")
Base.metadata.create_all(engine)

from sqlalchemy.orm import Session
with Session(engine) as session:
    new_campaign = Campaign(status=CampaignStatus.activated)
    session.add(new_campaign)
    session.commit()

    retrieved_campaign = session.query(Campaign).first()
    print(retrieved_campaign.status) # Output: CampaignStatus.activated