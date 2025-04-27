import datetime as dt
from uuid import uuid4

import enum
from sqlalchemy import create_engine
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column
from sqlalchemy.dialects.postgresql import ENUM as pgEnum


class Base(DeclarativeBase):
    pass


class CampaignStatus(str, enum.Enum):
    activated = "activated"
    deactivated = "deactivated"


engine = create_engine("postgresql+psycopg2://user:password@host:port/database") # Replace with your DB connection string
CampaignStatusType = pgEnum(CampaignStatus, name="campaignstatus", create_constraint=True, metadata=Base.metadata)


class Campaign(Base):
    __tablename__ = "campaign"

    id: Mapped[uuid4] = mapped_column(primary_key=True, default=uuid4)
    created_at: Mapped[dt.datetime] = mapped_column(default=dt.datetime.now)
    status: Mapped[CampaignStatus] = mapped_column(CampaignStatusType, nullable=False)


Base.metadata.create_all(engine)



# Example usage and exception handling:
from sqlalchemy.orm import Session
from sqlalchemy.exc import SQLAlchemyError

try:
    with Session(engine) as session:
        new_campaign = Campaign(status=CampaignStatus.activated) # Must use Enum member
        session.add(new_campaign)
        session.commit()
        print("Campaign created successfully.")

except ValueError as e:  # Handle specific exceptions like ValueError if enum is incorrect
    print(f"Invalid campaign status: {e}")
    # Implement specific recovery logic for ValueError
except SQLAlchemyError as e:  # Catch SQLAlchemy specific exceptions
    print(f"Database error: {e}")
    session.rollback()  # Roll back the transaction in case of database errors
    # Implement specific recovery logic for database errors
except Exception as e: # Catch any other unexpected errors
    print(f"An unexpected error occurred: {e}")
    # Implement general error handling or re-raise if necessary