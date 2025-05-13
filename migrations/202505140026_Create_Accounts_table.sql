-- Creating the accounts table
CREATE TABLE accounts (
    id VARCHAR(255) PRIMARY KEY,            -- Account ID (Primary Key)
    user_id VARCHAR(255) NOT NULL,          -- Foreign Key to users table
    type VARCHAR(50),                       -- Type of the account
    balance DECIMAL(15, 2),                 -- Account balance with precision
    status VARCHAR(50),                     -- Account status
    created_at TIMESTAMP,                   -- Timestamp of account creation
    updated_at TIMESTAMP,                   -- Timestamp of last update
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE  -- Foreign Key constraint
);