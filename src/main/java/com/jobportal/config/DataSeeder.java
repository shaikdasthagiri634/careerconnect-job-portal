package com.jobportal.config;

import com.jobportal.model.ApplicationStatus;
import com.jobportal.model.Job;
import com.jobportal.model.JobApplication;
import com.jobportal.model.JobType;
import com.jobportal.model.Role;
import com.jobportal.model.User;
import com.jobportal.repository.JobApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Inserts demo accounts and sample jobs the first time the app starts on an empty database.
 * Turn it off with app.seed-demo-data=false.
 */
@Component
@ConditionalOnProperty(name = "app.seed-demo-data", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, JobRepository jobRepository,
                      JobApplicationRepository applicationRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }
        log.info("Empty database detected - inserting demo data");

        userRepository.save(user("Site Admin", "admin@jobportal.com", "admin123", Role.ADMIN, "Platform administrator"));
        User techCorp = userRepository.save(user("Priya Sharma", "employer@techcorp.com", "employer123",
                Role.EMPLOYER, "TechCorp Solutions"));
        User cloudNine = userRepository.save(user("Rahul Verma", "hr@cloudnine.io", "employer123",
                Role.EMPLOYER, "CloudNine Systems"));
        User candidate = userRepository.save(user("Arjun Reddy", "candidate@gmail.com", "candidate123",
                Role.CANDIDATE, "Java Developer"));
        userRepository.save(user("Sneha Patel", "sneha@gmail.com", "candidate123",
                Role.CANDIDATE, "Frontend Developer"));

        Job javaDev = jobRepository.save(job(techCorp, "Java Backend Developer", "TechCorp Solutions", "Hyderabad",
                JobType.FULL_TIME, "8 - 14 LPA", "2 - 4 years", "Java, Spring Boot, REST, SQL Server, Hibernate",
                "Design and build REST APIs with Spring Boot, write clean and tested code, and work with the product "
                        + "team to ship features used by thousands of customers.", 1));
        jobRepository.save(job(techCorp, "Salesforce Developer", "TechCorp Solutions", "Bengaluru",
                JobType.FULL_TIME, "10 - 16 LPA", "3 - 5 years", "Apex, LWC, SOQL, Salesforce Integration",
                "Build custom Salesforce solutions with Apex and Lightning Web Components and integrate Salesforce "
                        + "with internal systems over REST.", 2));
        jobRepository.save(job(techCorp, "Software Engineering Intern", "TechCorp Solutions", "Vijayawada",
                JobType.INTERNSHIP, "25,000 / month", "Freshers", "Java, SQL, Git",
                "Six-month internship working alongside senior engineers on real projects. Strong basics in Java "
                        + "and databases required.", 3));
        Job react = jobRepository.save(job(cloudNine, "React Frontend Developer", "CloudNine Systems", "Pune",
                JobType.FULL_TIME, "7 - 12 LPA", "1 - 3 years", "React, JavaScript, HTML, CSS, REST",
                "Build responsive user interfaces in React and collaborate with designers and backend engineers.", 1));
        jobRepository.save(job(cloudNine, "DevOps Engineer", "CloudNine Systems", "Remote",
                JobType.REMOTE, "12 - 20 LPA", "3 - 6 years", "AWS, Docker, Kubernetes, Jenkins, Linux",
                "Own CI/CD pipelines and cloud infrastructure, improve reliability and automate deployments.", 4));
        jobRepository.save(job(cloudNine, "QA Automation Engineer", "CloudNine Systems", "Chennai",
                JobType.CONTRACT, "6 - 9 LPA", "2 - 4 years", "Selenium, Java, TestNG, Postman",
                "Write and maintain automated UI and API test suites and help the team release with confidence.", 5));
        jobRepository.save(job(techCorp, "Full Stack Developer (Part-time)", "TechCorp Solutions", "Hyderabad",
                JobType.PART_TIME, "40,000 / month", "1 - 2 years", "Java, Spring Boot, Thymeleaf, JavaScript",
                "Part-time role maintaining internal tools built with Spring Boot and Thymeleaf.", 6));

        applicationRepository.save(application(javaDev, candidate, ApplicationStatus.SHORTLISTED,
                "I have built several Spring Boot REST APIs backed by SQL Server and would love to join TechCorp."));
        applicationRepository.save(application(react, candidate, ApplicationStatus.APPLIED,
                "I enjoy frontend work as well and have used React in personal projects."));

        log.info("Demo data ready. Logins: admin@jobportal.com/admin123, employer@techcorp.com/employer123, "
                + "candidate@gmail.com/candidate123");
    }

    private User user(String name, String email, String rawPassword, Role role, String headline) {
        User u = new User(name, email, passwordEncoder.encode(rawPassword), role);
        u.setHeadline(headline);
        return u;
    }

    private Job job(User employer, String title, String company, String location, JobType type, String salary,
                    String experience, String skills, String description, int daysAgo) {
        Job j = new Job();
        j.setEmployer(employer);
        j.setTitle(title);
        j.setCompanyName(company);
        j.setLocation(location);
        j.setJobType(type);
        j.setSalary(salary);
        j.setExperience(experience);
        j.setSkills(skills);
        j.setDescription(description);
        j.setActive(true);
        j.setPostedAt(LocalDateTime.now().minusDays(daysAgo));
        return j;
    }

    private JobApplication application(Job job, User candidate, ApplicationStatus status, String coverLetter) {
        JobApplication a = new JobApplication();
        a.setJob(job);
        a.setCandidate(candidate);
        a.setStatus(status);
        a.setCoverLetter(coverLetter);
        return a;
    }
}
