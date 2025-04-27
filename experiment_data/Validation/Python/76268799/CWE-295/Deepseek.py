from sqlalchemy import create_engine, Column, Enum, UUID
from sqlalchemy.orm import declarative_base, Mapped, mapped_column
from sqlalchemy.dialects.postgresql import ENUM as pgEnum
import enum
import datetime as dt
from uuid import uuid4

# Define the Base class
Base = declarative_base()

# Define the Enum class
class CampaignStatus(str, enum.Enum):
    activated = "activated"
    deactivated = "deactivated"

# Define the ENUM type for PostgreSQL
CampaignStatusType = pgEnum(
    CampaignStatus,
    name="campaignstatus",
    create_constraint=True,
    validate_strings=True,
)

# Define the Campaign class
class Campaign(Base):
    __tablename__ = "campaign"

    id: Mapped[UUID] = mapped_column(UUID(as_uuid=True), primary_key=True, default=uuid4)
    created_at: Mapped[dt.datetime] = mapped_column(default=dt.datetime.now)
    status: Mapped[CampaignStatus] = mapped_column(Enum(CampaignStatus), nullable=False)

# Create an engine and bind it to the Base
engine = create_engine('postgresql://user:password@localhost/dbname')
Base.metadata.create_all(engine)