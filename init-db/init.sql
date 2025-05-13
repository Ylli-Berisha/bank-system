-- CREATE DATABASE authdb;
-- CREATE USER "ylli-auth" WITH PASSWORD 'secret';
-- GRANT ALL PRIVILEGES ON DATABASE authdb TO "ylli-auth";

CREATE DATABASE accountsdb;
CREATE USER "ylli-accounts" WITH PASSWORD 'secret';
GRANT ALL PRIVILEGES ON DATABASE accountsdb TO "ylli-accounts";

CREATE DATABASE transactionsdb;
CREATE USER "ylli-transactions" WITH PASSWORD 'secret';
GRANT ALL PRIVILEGES ON DATABASE transactionsdb TO "ylli-transactions";

CREATE DATABASE auditdb;
CREATE USER "ylli-audit" WITH PASSWORD 'secret';
GRANT ALL PRIVILEGES ON DATABASE auditdb TO "ylli-audit";

CREATE DATABASE admindb;
CREATE USER "ylli-admin" WITH PASSWORD 'secret';
GRANT ALL PRIVILEGES ON DATABASE admindb TO "ylli-admin";

CREATE DATABASE usersdb;
CREATE USER "ylli-users" WITH PASSWORD 'secret';
GRANT ALL PRIVILEGES ON DATABASE usersdb TO "ylli-users";
