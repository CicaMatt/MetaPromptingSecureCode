from sqlalchemy import Column, Enum
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.dialects.postgresql import ENUM as PgEnum
import sqlalchemy as sa
import enum
import datetime as dt
import uuid

Base = declarative_base()

class CampaignStatus(str, enum.Enum):
    activated = "activated"
    deactivated = "deactivated"

class Campaign(Base):
    __tablename__ = "campaign"

    id = Column(sa.UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    created_at = Column(sa.DateTime, default=dt.datetime.utcnow)
    status = Column(PgEnum(CampaignStatus, name="campaignstatus", create_type=False), nullable=False)

# Example to create the table
from sqlalchemy import create_engine

engine = create_engine('postgresql://user:password@localhost/mydatabase')

# Create the table
Base.metadata.create_all(engine)