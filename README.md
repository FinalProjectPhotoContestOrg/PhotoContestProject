# Photo Contest Application

## Project Description

The Photo Contest Application allows aspiring photographers to manage and participate in online photo contests. The application features two main components:

- **Organizational**: For application owners to organize and manage photo contests.
- **For Photo Junkies**: Users can register, participate in contests, and be invited as jurors based on their ranking.

## Functional Requirements

### Public Part

Accessible without authentication.

- **Landing Page**: Displays the latest winning photos, featured contests and a leaderboard of top Photo Junkies.
- **Login Form**: Redirects users to the private area. Requires username and password.
- **Register Form**: Allows users to register as Photo Junkies. Requires username, first name, last name, and password.

### Private Part

Accessible only if the user is authenticated.

#### Dashboard Page

- **For Organizers**:
    - Button to set up a new contest.
    - View Contests in Phase I.
    - View Contests in Phase II.
    - View Finished Contests.

- **For Photo Junkies**:
    - View active Open contests.
    - View contests they are participating in.
    - View finished contests they participated in.
    - Display current points, ranking, and progress to the next ranking (if scoring is implemented).

#### Contest Page

- **Phase I**:
    - Display remaining time until Phase II.
    - Jury can view but not rate entries.
    - Participants can upload one entry with photo, title and story.

- **Phase II**:
    - Display remaining time until Finish phase.
    - Participants can no longer upload photos.
    - Jury can rate photos and provide comments.
    - Default score of 3 if a photo is not reviewed.

- **Finished**:
    - Jury can no longer review photos.
    - Participants can view their scores and comments, and also view other users' photos with scores and comments (should).

#### Create Contest Page

- **Create Contest Form**:
    - Title.
    - Category.
    - Contest Type: Open or Invitational.
    - Phase I and Phase II time limits.
    - Select Jury: Includes all users with Organizer role, and optionally Photo Masters.
    - Cover Photo: Upload a cover photo for your contest.

### Scoring

- Points awarded for contest participation and ranking, with detailed point allocations for different positions and ties.

### Ranking

- Defined by accumulated points, with ranks ranging from Junkie to Wise and Benevolent Photo Dictator.

### Social Sharing

- Option for top finishers to share achievements on social media platforms like Facebook.

## REST API

- **Users**: CRUD operations, search by username, first name, or last name.
- **Contests**: CRUD operations, switch phases, submit and rate photos, list and filter contests.
- **Entries**: CRUD operations, list and search by title.
- **Ratings**: CRUD operations, list and search by rating.

**Documentation**: [Swagger Documentation](http://localhost:8080/swagger-ui/index.html#/)

## Technical Information

- Follows OOP principles, KISS, SOLID, and DRY principles.
- Uses tiered project structure.
- Achieves at least 80% unit test code coverage in the service layer.
- Implements proper exception handling and propagation.
- Normalized the database to avoid data duplication and empty data.

## Database

- The Project uses a relational database for data storage.
- It includes scripts for database creation and data insertion.


## Steps to Install

1. Clone the repository:
    ```sh
    git clone https://gitlab.com/your-repo/photo-contest.git
    cd photo-contest
    ```

2. Set up the database:
    - Update `application.properties` with your database credentials.
    - Run the SQL scripts in the `resources` folder to create and populate the database.

**Example `application.properties`:**
```properties
spring.application.name=PhotoContestProject
server.error.include-stacktrace=never
server.error.include-binding-errors=always
server.error.include-message=always
database.url=jdbc:mariadb://localhost:3306/photo_contest_schema
database.username=root
database.password=root
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=photocontest619@gmail.com
spring.mail.password=qvhz fhyq ausy qhik
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
server.max-http-request-header-size= 1MB
```
## Database Relations
Database relations are shown in the following diagram:
![Database Relations](/ReadMe/DBdiagram.png)

## Contributors
https://github.com/TodorKst [Todor Kostadinov]
https://github.com/Stefcho1227 [Stefan Ivanov]
