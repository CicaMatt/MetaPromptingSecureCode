from sqlalchemy import create_engine, Column, Enum
from sqlalchemy.orm import declarative_base, Mapped, mapped_column
from sqlalchemy.dialects.postgresql import UUID
import enum
import datetime as dt
from uuid import uuid4

# Define the Base for SQLAlchemy ORM
Base = declarative_base()

# Define the Enum class
class CampaignStatus(str, enum.Enum):
    activated = "activated"
    deactivated = "deactivated"

# Define the CampaignStatusType using SQLAlchemy's Enum
CampaignStatusType = Enum(
    CampaignStatus,
    name="campaignstatus",
    create_constraint=True,
    validate_strings=True,
)

# Define the Campaign model
class Campaign(Base):
    __tablename__ = "campaign"

    id: Mapped[UUID] = mapped_column(UUID(as_uuid=True), primary_key=True, default=uuid4)
    created_at: Mapped[dt.datetime] = mapped_column(default=dt.datetime.now)
    status: Mapped[CampaignStatus] = mapped_column(CampaignStatusType, nullable=False)

# Create an in-memory SQLite database for demonstration
engine = create_engine('sqlite:///:memory:', echo=True)

# Create all tables
Base.metadata.create_all(engine)

# Example usage
from sqlalchemy.orm import Session

with Session(engine) as session:
    new_campaign = Campaign(status=CampaignStatus.activated)
    session.add(new_campaign)
    session.commit()

    # Query the database
    campaign = session.query(Campaign).first()
    print(f"Campaign ID: {campaign.id}, Status: {campaign.status}")