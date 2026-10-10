/* Handy queries to inspect the data in SSMS after the app has started at least once. */
USE jobportal_db;
GO

-- All users and their roles
SELECT id, full_name, email, role, enabled, created_at FROM users ORDER BY created_at DESC;

-- Open jobs with the employer who posted them
SELECT j.id, j.title, j.company_name, j.location, j.job_type, j.salary, u.email AS posted_by, j.posted_at
FROM jobs j JOIN users u ON u.id = j.employer_id
WHERE j.active = 1
ORDER BY j.posted_at DESC;

-- Applications with candidate and job
SELECT a.id, c.full_name AS candidate, j.title AS job, a.status, a.applied_at
FROM job_applications a
JOIN users c ON c.id = a.candidate_id
JOIN jobs j ON j.id = a.job_id
ORDER BY a.applied_at DESC;

-- Number of applicants per job
SELECT j.title, COUNT(a.id) AS applicants
FROM jobs j LEFT JOIN job_applications a ON a.job_id = j.id
GROUP BY j.title
ORDER BY applicants DESC;

-- Reset everything (the app re-inserts demo data on next start)
-- DELETE FROM job_applications; DELETE FROM jobs; DELETE FROM users;
