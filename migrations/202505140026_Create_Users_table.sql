-- Create the 'user' table
CREATE TABLE "user" (
                        id VARCHAR(255) PRIMARY KEY,          -- Primary key column for the 'user' table
                        first_name VARCHAR(255),              -- Column for the 'firstName' field
                        last_name VARCHAR(255),               -- Column for the 'lastName' field
                        email VARCHAR(255),                   -- Column for the 'email' field
                        password VARCHAR(255),                -- Column for the 'password' field
                        phone_number VARCHAR(20),             -- Column for the 'phoneNumber' field (adjust size if needed)
                        address TEXT,                         -- Column for the 'address' field (use TEXT to store longer addresses)
                        birth_date DATE,                      -- Column for the 'birthDate' field (use DATE for the date of birth)
                        created_at TIMESTAMP,                 -- Column for the 'createdAt' field (use TIMESTAMP for date and time)
                        updated_at TIMESTAMP                  -- Column for the 'updatedAt' field (use TIMESTAMP for date and time)
);

-- Optional: If you are using enums for other fields (such as 'status'), you can add enum types like this:
-- CREATE TYPE user_status AS ENUM ('ACTIVE', 'INACTIVE', 'SUSPENDED');
-- ALTER TABLE "user" ADD COLUMN status user_status;
