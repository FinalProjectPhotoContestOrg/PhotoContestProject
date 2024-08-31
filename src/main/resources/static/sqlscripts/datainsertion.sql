
INSERT INTO users (username, first_name, last_name, email, password_hash, role, ranking, points)
VALUES
    ('john', 'John', 'Doe', 'john.doe@example.com', 'hash1', 'Organizer', 'WiseAndBenevolentPhotoDictator', 1000),
    ('jane', 'Jane', 'Smith', 'jane.smith@example.com', 'hash2', 'Junkie', 'Junkie', 50),
    ('alice', 'Alice', 'Brown', 'alice.brown@example.com', 'hash3', 'Junkie', 'Enthusiast', 200),
    ('bobby', 'Bob', 'Johnson', 'bob.johnson@example.com', 'hash4', 'Junkie', 'Master', 500);

-- Insert sample data into the `contests` table
INSERT INTO contests (title, category, type, phase, phase_1_end, phase_2_end, organizer_id, cover_photo_url)
VALUES
    ('Nature Photography', 'Nature', 'Open', 'PhaseI', '2024-09-10 23:59:59', '2024-09-20 23:59:59', 1, 'https://example.com/cover1.jpg'),
    ('Urban Exploration', 'Urban', 'Invitational', 'PhaseI', '2024-08-15 23:59:59', '2024-08-25 23:59:59', 1, 'https://example.com/cover2.jpg');

-- Insert sample data into the `contest_participants` table
INSERT INTO contest_participants (contest_id, user_id)
VALUES
    (1, 2),  -- Jane Smith in Nature Photography
    (1, 3),  -- Alice Brown in Nature Photography
    (2, 3),  -- Alice Brown in Urban Exploration
    (2, 4);  -- Bob Johnson in Urban Exploration

-- Insert sample data into the `entries` table
INSERT INTO entries (title, story, photo_url, participant_id, contest_id)
VALUES
    ('Sunset Bliss', 'A serene sunset over the mountains.', 'iVBORw0KGgoAAAANSUhEUgAAAAUA...', 2, 1),
    ('Forest Walk', 'A quiet path through the dense forest.', 'iVBORw0KGgoAAAANSUhEUgAAAAUA...', 3, 1),
    ('City Lights', 'A vibrant cityscape at night.', 'iVBORw0KGgoAAAANSUhEUgAAAAUA...', 3, 2),
    ('Abandoned Factory', 'Exploring the remnants of industrial past.', 'iVBORw0KGgoAAAANSUhEUgAAAAUA...', 4, 2);

-- Insert sample data into the `ratings` table
INSERT INTO ratings (entry_id, juror_id, score, comment, category_mismatch)
VALUES
    (1, 1, 9, 'Beautiful composition and colors.', 0),
    (2, 1, 8, 'Great depth and atmosphere.', 0),
    (3, 1, 7, 'Captivating night shot.', 0),
    (4, 1, 6, 'Interesting subject, but lacks focus.', 0),
    (4, 3, 0, 'Does not fit the contest theme.', 1);