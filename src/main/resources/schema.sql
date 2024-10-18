# -- Table for users
# CREATE TABLE IF NOT EXISTS user (
#     id BIGINT AUTO_INCREMENT PRIMARY KEY,
#     username VARCHAR(255) NOT NULL,
#     password VARCHAR(255) NOT NULL
# );
#
# -- Table for events
# CREATE TABLE IF NOT EXISTS event (
#      id BIGINT AUTO_INCREMENT PRIMARY KEY,
#      name VARCHAR(255),
#      description TEXT,
#      location VARCHAR(255),
#      date VARCHAR(255),
#      time VARCHAR(255),
#      capacity INT,
#      budget INT,
#      host_id BIGINT,
#      FOREIGN KEY (host_id) REFERENCES user(id) ON DELETE CASCADE
# );
#
# -- Join table for event participants
# CREATE TABLE IF NOT EXISTS event_participants (
#     event_id BIGINT,
#     user_id BIGINT,
#     PRIMARY KEY (event_id, user_id),
#     FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE,
#     FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
# );

# -- Table for tasks
# CREATE TABLE IF NOT EXISTS task (
#     id BIGINT AUTO_INCREMENT PRIMARY KEY,
#     name VARCHAR(255) NOT NULL,
#     description TEXT,
#     status ENUM('PENDING', 'COMPLETED', 'IN_PROGRESS', 'CANCELLED') DEFAULT 'PENDING',
#     due_date TIMESTAMP,
#     assigned_user_id BIGINT,
#     event_id BIGINT NOT NULL,
#     FOREIGN KEY (assigned_user_id) REFERENCES user(id) ON DELETE CASCADE,
#     FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE
# );
