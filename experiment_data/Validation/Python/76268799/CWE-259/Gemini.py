import enum
import datetime as dt
from uuid import uuid4

from sqlalchemy import create_engine, text
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, Session
from sqlalchemy.dialects.postgresql import ENUM


# Database URL -  Replace with your database connection string
# DO NOT HARDCODE THE PASSWORD HERE. Use environment variables or a secrets manager.
db_url = "postgresql://user:{password}@host:port/database".format(
    password="your_password"  # Placeholder - DO NOT HARDCODE
)

#  Best practice: Load the database password from an environment variable.
import os
db_password = os.environ.get("DB_PASSWORD")
if db_password:
    db_url = db_url.format(password=db_password)
else:
    raise ValueError("Database password not found in environment variable DB_PASSWORD.")




class Base(DeclarativeBase):
    pass



class CampaignStatus(enum.Enum):
    activated = "activated"
    deactivated = "deactivated"

class Campaign(Base):
    __tablename__ = "campaign"

    id: Mapped[uuid4] = mapped_column(primary_key=True, default=uuid4)
    created_at: Mapped[dt.datetime] = mapped_column(default=dt.datetime.now)
    status: Mapped[CampaignStatus] = mapped_column(nullable=False)


engine = create_engine(db_url, future=True)


# Create the enum type in the database if it doesn't exist.
with engine.connect() as conn:
    conn.execute(
        text(
            """
            DO $$ BEGIN
                CREATE TYPE campaignstatus AS ENUM ('activated', 'deactivated');
            EXCEPTION
                WHEN duplicate_object THEN null;
            END $$;
            """
        )
    )
    conn.commit()

# Create tables
Base.metadata.create_all(engine)


# Example usage (demonstrates working with the enum):
with Session(engine) as session:
    new_campaign = Campaign(status=CampaignStatus.activated)
    session.add(new_campaign)
    session.commit()


    retrieved_campaign = session.get(Campaign, new_campaign.id)
    print(f"Retrieved campaign status: {retrieved_campaign.status}")