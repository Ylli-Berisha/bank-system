-- Create the 'audit' table
CREATE TABLE audit (
                       id BIGINT PRIMARY KEY,  -- Primary key column for the 'audit' table
                       user_id BIGINT,         -- Foreign key referencing the 'users' table (assuming 'User' has an 'id' column of type BIGINT)
                       type VARCHAR(255),      -- Column for the 'type' field (you can change the length based on your needs)
                       details VARCHAR(255),   -- Column for the 'details' field
                       create_at TIMESTAMP,    -- Column for the 'createAt' timestamp
                       CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)  -- Foreign key constraint to the 'users' table
);

-- Make sure to include the 'type' field as an enum if you have specific types for the 'type' column
-- If you're planning to make 'type' an enum, you'll need to create that enum type in PostgreSQL first:
-- CREATE TYPE audit_type AS ENUM ('TYPE1', 'TYPE2', 'TYPE3'); -- Example

-- If you use an enum type, then modify the column like so:
-- ALTER TABLE audit ALTER COLUMN type TYPE audit_type USING type::audit_type;
