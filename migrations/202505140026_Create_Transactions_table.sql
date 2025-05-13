-- Create the 'transaction' table
CREATE TABLE transaction (
                             id BIGINT PRIMARY KEY,               -- Primary key column for the 'transaction' table
                             account_id BIGINT,                   -- Foreign key referencing the 'accounts' table
                             type VARCHAR(255),                   -- Column for the 'type' field (can be an enum)
                             amount DOUBLE PRECISION,             -- Column for the 'amount' field (use DOUBLE PRECISION for floating-point numbers)
                             time TIMESTAMP,                      -- Column for the 'time' field (use TIMESTAMP for date and time)
                             status VARCHAR(255),                 -- Column for the 'status' field (can be an enum)
                             details VARCHAR(255),                -- Column for the 'details' field (use VARCHAR to store transaction details)
                             recipient_account_id BIGINT,         -- Foreign key referencing the 'accounts' table for the recipient account
                             CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES accounts(id),          -- Foreign key constraint to the 'accounts' table
                             CONSTRAINT fk_recipient_account FOREIGN KEY (recipient_account_id) REFERENCES accounts(id)  -- Foreign key constraint to the 'accounts' table for the recipient account
);

-- Optional: Handling for 'type' and 'status' fields as enums
-- If you want 'type' or 'status' to be enums, you can create enums in PostgreSQL first:
-- CREATE TYPE transaction_type AS ENUM ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER');  -- Example for 'type'
-- CREATE TYPE transaction_status AS ENUM ('PENDING', 'COMPLETED', 'FAILED');    -- Example for 'status'

-- If you use enums, then modify the columns to use the enum type:
-- ALTER TABLE transaction ALTER COLUMN type TYPE transaction_type USING type::transaction_type;
-- ALTER TABLE transaction ALTER COLUMN status TYPE transaction_status USING status::transaction_status;
