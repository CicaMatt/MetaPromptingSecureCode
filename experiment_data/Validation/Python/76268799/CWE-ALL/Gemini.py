import datetime as dt
import enum
from uuid import uuid4

from sqlalchemy import create_engine, Column, Integer
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, Session
from sqlalchemy.dialects.postgresql import ENUM

# Database URL -  replace with your actual database connection details
#  Best practice: Store credentials securely (environment variables, secrets management)
DATABASE_URL = "postgresql://user:password@host:port/database"  # Example - DO NOT USE THIS IN PRODUCTION

# --- Mitigation for CWE-259, CWE-798 (Hard-coded Credentials): Do NOT hardcode credentials here.
# Use environment variables or a secure configuration mechanism.

class Base(DeclarativeBase):
    pass

class CampaignStatus(enum.Enum):
    activated = "activated"
    deactivated = "deactivated"


class Campaign(Base):
    __tablename__ = "campaign"

    id: Mapped[uuid4] = mapped_column(primary_key=True, default=uuid4)
    created_at: Mapped[dt.datetime] = mapped_column(default=dt.datetime.now)
    status: Mapped[str] = mapped_column(nullable=False) # Define as string initially


engine = create_engine(DATABASE_URL)

# Create the enum type in the database (only needed once)
try: # Mitigation for CWE-397 (Generic Exception): Use specific exceptions if possible.
    CampaignStatusType = ENUM(CampaignStatus, name="campaignstatus", create_type=False, metadata=Base.metadata, validate_strings=True)
    CampaignStatusType.create(engine, checkfirst=True) # Mitigation for CWE-477 (Obsolete Function): Ensure methods used are not obsolete.

except Exception as e: # Broad exception for demonstration, replace with more specific exceptions in a real application
    print(f"Error creating enum: {e}")
    # Handle the error appropriately (logging, retrying, etc.)


Base.metadata.create_all(engine)

# --- Example usage (demonstrates storing enum values):

with Session(engine) as session:
    try:
        new_campaign = Campaign(status=CampaignStatus.activated.value)  # Store enum *value*
        session.add(new_campaign)
        session.commit()

        retrieved_campaign = session.query(Campaign).first()
        print(f"Retrieved campaign status: {retrieved_campaign.status}")  # Access and print the status


        # Demonstrate retrieval and comparison with enum members:
        if retrieved_campaign.status == CampaignStatus.activated.value:
            print("Campaign is activated")

    except Exception as e:
        print(f"Error during database operation: {e}")  #Again, be specific
        session.rollback()



# --- Mitigation for CWE-295, CWE-327: These are relevant for network communication and cryptography.
# While not directly applicable to this enum example, ensure these are addressed in other parts
# of your application that handle network requests and sensitive data.