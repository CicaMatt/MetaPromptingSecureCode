from sqlalchemy import create_engine, Column, Enum
from sqlalchemy.orm import declarative_base, Mapped, mapped_column, relationship, sessionmaker
import enum
import uuid
import datetime as dt

# Declare the base for declarative classes
Base = declarative_base()

# Define the Enum using Python's enum
class CampaignStatus(str, enum.Enum):
    activated = "activated"
    deactivated = "deactivated"

# Define the Campaign class using SQLAlchemy ORM
class Campaign(Base):
    __tablename__ = "campaign"

    id: Mapped[uuid.UUID] = mapped_column(primary_key=True, default=uuid.uuid4)
    created_at: Mapped[dt.datetime] = mapped_column(default=dt.datetime.utcnow)
    status: Mapped[CampaignStatus] = mapped_column(Enum(CampaignStatus), nullable=False)

# Example engine and session setup (typically configured based on your application's needs)
# Update the database URL with your actual database settings
engine = create_engine('postgresql://username:password@localhost/dbname')
Base.metadata.create_all(engine)

Session = sessionmaker(bind=engine)
session = Session()

# Example of creating a new campaign
new_campaign = Campaign(status=CampaignStatus.activated)
session.add(new_campaign)
session.commit()

# Closing the session
session.close()