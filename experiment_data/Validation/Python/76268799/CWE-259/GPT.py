from sqlalchemy import create_engine, Column, Enum
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.dialects.postgresql import ENUM as pgEnum
import sqlalchemy.types as types
import enum
import uuid
import datetime as dt
from sqlalchemy.orm import sessionmaker

Base = declarative_base()

class CampaignStatus(enum.Enum):
    activated = "activated"
    deactivated = "deactivated"

class Campaign(Base):
    __tablename__ = "campaign"

    id = Column(types.UUID, primary_key=True, default=uuid.uuid4)
    created_at = Column(dt.datetime, default=dt.datetime.now)
    status = Column(Enum(CampaignStatus, name="campaign_status"), nullable=False)

# Setup the database (example using PostgreSQL)
engine = create_engine('postgresql://username:password@localhost/mydatabase')
Base.metadata.create_all(engine)

# Create a session
Session = sessionmaker(bind=engine)
session = Session()

# Example of how to create a new campaign
new_campaign = Campaign(status=CampaignStatus.activated)
session.add(new_campaign)
session.commit()

session.close()