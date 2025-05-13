-- Create the 'loan' table
CREATE TABLE loan (
                      id BIGINT PRIMARY KEY,               -- Primary key column for the 'loan' table
                      account_id BIGINT,                   -- Foreign key referencing the 'accounts' table (assuming 'Account' has an 'id' column of type BIGINT)
                      type VARCHAR(255),                   -- Column for the 'type' field (you can change the length based on your needs)
                      amount DOUBLE PRECISION,             -- Column for the 'amount' field (use DOUBLE PRECISION for floating-point numbers)
                      interest_rate DOUBLE PRECISION,     -- Column for the 'interestRate' field (use DOUBLE PRECISION for floating-point numbers)
                      status VARCHAR(255),                 -- Column for the 'status' field (can be an enum as well)
                      start_date DATE,                     -- Column for the 'startDate' field
                      end_date DATE,                       -- Column for the 'endDate' field
                      created_at TIMESTAMP,                -- Column for the 'createdAt' timestamp
                      updated_at TIMESTAMP,                -- Column for the 'updatedAt' timestamp
                      CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES accounts(id)  -- Foreign key constraint to the 'accounts' table
);

-- Optional: Handling for 'type' and 'status' fields as enums
-- If you want 'type' or 'status' to be enums, you can create enums in PostgreSQL first:
-- CREATE TYPE loan_type AS ENUM ('PERSONAL', 'MORTGAGE', 'BUSINESS');  -- Example for 'type'
-- CREATE TYPE loan_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED'); -- Example for 'status'

-- If you use enums, then modify the columns to use the enum type:
-- ALTER TABLE loan ALTER COLUMN type TYPE loan_type USING type::loan_type;
-- ALTER TABLE loan ALTER COLUMN status TYPE loan_status USING status::loan_status;
