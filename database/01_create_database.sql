/*
  CareerConnect Job Portal - SQL Server setup
  Run this in SSMS (New Query) BEFORE starting the application.

  The application creates and updates the tables itself (spring.jpa.hibernate.ddl-auto=update)
  and inserts demo data on first start, so you only need the database and a SQL login.
*/

-- 1. Create the database
IF DB_ID('jobportal_db') IS NULL
    CREATE DATABASE jobportal_db;
GO

-- 2. (Optional) a dedicated SQL login instead of 'sa'.
--    If you use this, put jobportal_user / the password below in application.properties.
IF NOT EXISTS (SELECT 1 FROM sys.server_principals WHERE name = 'jobportal_user')
    CREATE LOGIN jobportal_user WITH PASSWORD = 'JobPortal@123', CHECK_POLICY = OFF;
GO

USE jobportal_db;
GO

IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = 'jobportal_user')
BEGIN
    CREATE USER jobportal_user FOR LOGIN jobportal_user;
    ALTER ROLE db_owner ADD MEMBER jobportal_user;
END
GO
