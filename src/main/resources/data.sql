-- Student
INSERT INTO student (id, name, email, password, role)
VALUES (1, 'Student One', 'student@mail.com',
        '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'STUDENT')
ON CONFLICT (id) DO NOTHING;

-- Mahi
INSERT INTO student (id, name, email, password, role)
VALUES (2, 'Mahi', 'mahi@mail.com',
        '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'STUDENT')
ON CONFLICT (id) DO NOTHING;

-- Teacher
INSERT INTO teacher (id, name, email, password, role)
VALUES (1, 'Teacher One', 'teacher@mail.com',
        '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'TEACHER')
ON CONFLICT (id) DO NOTHING;

-- Admin
INSERT INTO teacher (id, name, email, password, role)
VALUES (2, 'Admin User', 'admin@mail.com',
        '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'TEACHER')
ON CONFLICT (id) DO NOTHING;

-- Courses
INSERT INTO course (id, title, credit, teacher_id) VALUES (1, 'Introduction to Programming', 3, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO course (id, title, credit, teacher_id) VALUES (2, 'Database Management Systems', 3, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO course (id, title, credit, teacher_id) VALUES (3, 'Web Development', 4, 2) ON CONFLICT (id) DO NOTHING;
INSERT INTO course (id, title, credit, teacher_id) VALUES (4, 'Data Structures & Algorithms', 3, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO course (id, title, credit, teacher_id) VALUES (5, 'Machine Learning Basics', 4, 2) ON CONFLICT (id) DO NOTHING;

-- Student Enrollments
INSERT INTO student_courses (student_id, course_id) VALUES (1, 1) ON CONFLICT DO NOTHING;
INSERT INTO student_courses (student_id, course_id) VALUES (1, 2) ON CONFLICT DO NOTHING;
INSERT INTO student_courses (student_id, course_id) VALUES (1, 4) ON CONFLICT DO NOTHING;
INSERT INTO student_courses (student_id, course_id) VALUES (2, 1) ON CONFLICT DO NOTHING;
INSERT INTO student_courses (student_id, course_id) VALUES (2, 3) ON CONFLICT DO NOTHING;
INSERT INTO student_courses (student_id, course_id) VALUES (2, 5) ON CONFLICT DO NOTHING;

-- password for all users: password