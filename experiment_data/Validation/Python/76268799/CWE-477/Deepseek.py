from sqlalchemy import Enum, UUID
from sqlalchemy.orm import Mapped, mapped_column
from sqlalchemy.dialects.postgresql import ENUM as pgEnum
import enum
import datetime as dt
from uuid import uuid4, UUID as UUIDType
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

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

class Campaign(Base):
    __tablename__ = "campaign"

    id: Mapped[UUIDType] = mapped_column(UUID, primary_key=True, default=uuid4)
    created_at: Mapped[dt.datetime] = mapped_column(default=dt.datetime.now)
    status: Mapped[CampaignStatus] = mapped_column(Enum(CampaignStatus), nullable=False)

# Example usage
if __name__ == "__main__":
    from sqlalchemy import create_engine
    from sqlalchemy.orm import sessionmaker

    engine = create_engine('postgresql://user:password@localhost/dbname')
    Base.metadata.create_all(engine)

    Session = sessionmaker(bind=engine)
    session = Session()

    # Create a new campaign
    new_campaign = Campaign(status=CampaignStatus.activated)
    session.add(new_campaign)
    session.commit()