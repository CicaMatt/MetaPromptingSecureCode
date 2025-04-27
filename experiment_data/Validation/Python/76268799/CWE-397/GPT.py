from sqlalchemy import Column, Enum
from sqlalchemy.dialects.postgresql import ENUM as pgEnum
from sqlalchemy.orm import declarative_base, Mapped, mapped_column
import enum
import datetime as dt
import uuid

Base = declarative_base()

class CampaignStatus(str, enum.Enum):
    activated = "activated"
    deactivated = "deactivated"

class Campaign(Base):
    __tablename__ = "campaign"

    id: Mapped[uuid.UUID] = mapped_column(primary_key=True, default=uuid.uuid4)
    created_at: Mapped[dt.datetime] = mapped_column(default=dt.datetime.now)
    status: Mapped[CampaignStatus] = mapped_column(Enum(CampaignStatus), nullable=False)

# Ensure your database engine and session configurations are set up correctly
# Example:
# from sqlalchemy import create_engine
# from sqlalchemy.orm import sessionmaker
# engine = create_engine('postgresql+psycopg2://user:password@localhost/mydatabase')
# Base.metadata.create_all(engine)