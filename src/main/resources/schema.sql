-- Drop the Users table if it exists
DROP TABLE IF EXISTS Users CASCADE;

-- Create the Users table
CREATE TABLE Users (
       id SERIAL PRIMARY KEY,
       company_name VARCHAR(255),
       email VARCHAR(255) NOT NULL,
       password VARCHAR(255) NOT NULL,
       role VARCHAR(50) NOT NULL,
       departement VARCHAR(255),
       full_name VARCHAR(255) NOT NULL,
       matricule VARCHAR(50),
       record_type VARCHAR(50) NOT NULL
);