from sqlalchemy import Enum, create_engine
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, sessionmaker
from uuid import UUID, uuid4
import datetime as dt
import enum

# Define the Enum class
class CampaignStatus(str, enum.Enum):
    activated = "activated"
    deactivated = "deactivated"

# Define the Base class
class Base(DeclarativeBase):
    pass

# Define the Campaign class
class Campaign(Base):
    __tablename__ = "campaign"

    id: Mapped[UUID] = mapped_column(primary_key=True, default=uuid4)
    created_at: Mapped[dt.datetime] = mapped_column(default=dt.datetime.now)
    status: Mapped[CampaignStatus] = mapped_column(Enum(CampaignStatus), nullable=False)

# Create an in-memory SQLite database for demonstration
engine = create_engine('sqlite:///:memory:', echo=True)

# Create all tables
Base.metadata.create_all(engine)

# Create a session
Session = sessionmaker(bind=engine)
session = Session()

# Add a new campaign
new_campaign = Campaign(status=CampaignStatus.activated)
session.add(new_campaign)
session.commit()

# Query the campaign
campaign = session.query(Campaign).first()
print(f"Campaign ID: {campaign.id}, Status: {campaign.status}")