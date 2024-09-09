package com.example.photocontestproject.servicetests;

import com.example.photocontestproject.TestHelper;
import com.example.photocontestproject.enums.Ranking;
import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.DuplicateEntityException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.Rating;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.models.options.RatingFilterOptions;
import com.example.photocontestproject.repositories.EntryRepository;
import com.example.photocontestproject.repositories.RatingRepository;
import com.example.photocontestproject.repositories.UserRepository;
import com.example.photocontestproject.services.RatingServiceImpl;
import com.example.photocontestproject.services.contracts.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RatingServiceTests {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private EntryRepository entryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RatingServiceImpl ratingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createRating_Should_Create_Rating() {
        User juror = new User();
        juror.setId(1);
        juror.setRole(Role.Organizer);

        Contest contest = new Contest();
        contest.setId(1);
        juror.setJurorContests(Collections.singleton(contest));

        User participant = new User();
        participant.setId(2);
        participant.setPoints(50);

        Entry entry = new Entry();
        entry.setContest(contest);
        entry.setParticipant(participant);
        entry.setEntryTotalScore(0);

        Rating rating = new Rating();
        rating.setJuror(juror);
        rating.setEntry(entry);
        rating.setScore(10);

        when(userService.getUserById(juror.getId())).thenReturn(juror);
        when(userService.getUserById(participant.getId())).thenReturn(participant);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);

        // Act
        Rating createdRating = ratingService.createRating(rating);

        // Assert
        assertNotNull(createdRating);
        assertEquals(60, participant.getPoints()); // Participant points updated
        assertEquals(Ranking.Enthusiast, participant.getRanking()); // Ranking updated
        assertEquals(10, entry.getEntryTotalScore()); // Entry score updated
        verify(userService, times(1)).updateUser(participant);
        verify(entryRepository, times(1)).save(entry);
        verify(ratingRepository, times(1)).save(rating);
    }

    @Test
    public void createRating_Should_ThrowException_When_UserIsNotAuthorized() {
        // Arrange
        User unauthorizedUser = new User();
        unauthorizedUser.setId(1);
        unauthorizedUser.setRole(Role.Junkie);
        unauthorizedUser.setJurorContests(Collections.emptySet());

        Contest contest = new Contest();
        contest.setId(1);
        contest.setJurors(null);

        User participant = new User();
        participant.setId(2);
        participant.setPoints(50);

        Entry entry = new Entry();
        entry.setContest(contest);
        entry.setParticipant(participant);
        entry.setEntryTotalScore(0);

        Rating rating = new Rating();
        rating.setJuror(unauthorizedUser);
        rating.setEntry(entry);
        rating.setScore(10);

        when(userService.getUserById(unauthorizedUser.getId())).thenReturn(unauthorizedUser);
        when(userService.getUserById(participant.getId())).thenReturn(participant);

        // Act & Assert
        assertThrows(AuthorizationException.class, () -> {
            ratingService.createRating(rating);
        });

        verify(ratingRepository, never()).save(any(Rating.class)); // Ensure save is not called
        verify(entryRepository, never()).save(any(Entry.class)); // Ensure entry save is not called
        verify(userService, never()).updateUser(any(User.class)); // Ensure user update is not called
    }

    @Test
    public void getRatingById_Should_Return_Rating_By_Id() {
        User user = TestHelper.createOrganizerUser();
        int id = 1;
        Rating mockRating = new Rating();
        when(ratingRepository.findById(id)).thenReturn(Optional.of(mockRating));

        Rating result = ratingService.getRatingById(id, user);

        assertNotNull(result, "Rating should not be null");
        assertEquals(mockRating, result, "Returned rating should match the mock rating");

    }

    @Test
    void testGetRatingByIdNotFound() {
        User user = TestHelper.createOrganizerUser();
        int id = 1;
        when(ratingRepository.findById(id)).thenReturn(Optional.empty());


        // When & Then
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            ratingService.getRatingById(id, user);
        }, "Expected getRatingById to throw, but it didn't");

        assertTrue(thrown.getMessage().contains("Rating"), "Exception message should contain 'Rating'");
    }


    @Test
    public void createRating_Should_Create_Rating_And_Update_User_Ranking_To_Enthusiast() {
        User organizer = new User();
        organizer.setId(1);
        organizer.setRole(Role.Organizer);

        Contest contest = new Contest();
        contest.setId(1);
        organizer.setJurorContests(Collections.singleton(contest));


        User participant = new User();
        participant.setId(2);
        participant.setPoints(51);

        Entry entry = new Entry();
        entry.setContest(contest);
        entry.setParticipant(participant);
        entry.setEntryTotalScore(0);

        Rating rating = new Rating();
        rating.setJuror(organizer);
        rating.setEntry(entry);
        rating.setScore(10);

        when(userService.getUserById(organizer.getId())).thenReturn(organizer);
        when(userService.getUserById(participant.getId())).thenReturn(participant);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);

        // Act
        Rating createdRating = ratingService.createRating(rating);

        // Assert
        assertNotNull(createdRating);
        assertEquals(61, participant.getPoints()); // Points updated
        assertEquals(Ranking.Enthusiast, participant.getRanking()); // Ranking updated
        assertEquals(10, entry.getEntryTotalScore()); // Entry score updated
        verify(userService, times(1)).updateUser(participant);
        verify(entryRepository, times(1)).save(entry);
        verify(ratingRepository, times(1)).save(rating);
    }

    @Test
    public void createRating_Should_Create_Rating_And_Update_User_Ranking_To_Master() {
        User organizer = new User();
        organizer.setId(1);
        organizer.setRole(Role.Organizer);

        Contest contest = new Contest();
        contest.setId(1);
        organizer.setJurorContests(Collections.singleton(contest));


        User participant = new User();
        participant.setId(2);
        participant.setPoints(151);

        Entry entry = new Entry();
        entry.setContest(contest);
        entry.setParticipant(participant);
        entry.setEntryTotalScore(0);

        Rating rating = new Rating();
        rating.setJuror(organizer);
        rating.setEntry(entry);
        rating.setScore(10);

        when(userService.getUserById(organizer.getId())).thenReturn(organizer);
        when(userService.getUserById(participant.getId())).thenReturn(participant);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);

        // Act
        Rating createdRating = ratingService.createRating(rating);

        // Assert
        assertNotNull(createdRating);
        assertEquals(161, participant.getPoints()); // Points updated
        assertEquals(Ranking.Master, participant.getRanking()); // Ranking updated
        assertEquals(10, entry.getEntryTotalScore()); // Entry score updated
        verify(userService, times(1)).updateUser(participant);
        verify(entryRepository, times(1)).save(entry);
        verify(ratingRepository, times(1)).save(rating);
    }


    @Test
    public void createRating_Should_Create_Rating_And_Update_User_Ranking_To_WiseAndBenevolent() {
        User organizer = new User();
        organizer.setId(1);
        organizer.setRole(Role.Organizer);

        Contest contest = new Contest();
        contest.setId(1);
        organizer.setJurorContests(Collections.singleton(contest));


        User participant = new User();
        participant.setId(2);
        participant.setPoints(1000);

        Entry entry = new Entry();
        entry.setContest(contest);
        entry.setParticipant(participant);
        entry.setEntryTotalScore(0);

        Rating rating = new Rating();
        rating.setJuror(organizer);
        rating.setEntry(entry);
        rating.setScore(10);

        when(userService.getUserById(organizer.getId())).thenReturn(organizer);
        when(userService.getUserById(participant.getId())).thenReturn(participant);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);

        // Act
        Rating createdRating = ratingService.createRating(rating);

        // Assert
        assertNotNull(createdRating);
        assertEquals(1010, participant.getPoints()); // Points updated
        assertEquals(Ranking.WiseAndBenevolentPhotoDictator, participant.getRanking()); // Ranking updated
        assertEquals(10, entry.getEntryTotalScore()); // Entry score updated
        verify(userService, times(1)).updateUser(participant);
        verify(entryRepository, times(1)).save(entry);
        verify(ratingRepository, times(1)).save(rating);
    }

    @Test
    public void createRating_Should_UpdateEntryTotalScore() {
        // Arrange
        User organizer = new User();
        organizer.setId(1);
        organizer.setRole(Role.Organizer);

        Contest contest = new Contest();
        contest.setId(1);
        organizer.setJurorContests(Collections.singleton(contest));

        User participant = new User();
        participant.setId(2);
        participant.setPoints(50);

        Entry entry = new Entry();
        entry.setContest(contest);
        entry.setParticipant(participant);
        entry.setEntryTotalScore(20); // Existing score

        Rating rating = new Rating();
        rating.setJuror(organizer);
        rating.setEntry(entry);
        rating.setScore(15);

        when(userService.getUserById(organizer.getId())).thenReturn(organizer);
        when(userService.getUserById(participant.getId())).thenReturn(participant);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);

        // Act
        Rating createdRating = ratingService.createRating(rating);

        // Assert
        assertNotNull(createdRating);
        assertEquals(35, entry.getEntryTotalScore()); // Entry score updated correctly
        verify(entryRepository, times(1)).save(entry);
        verify(ratingRepository, times(1)).save(rating);
    }


    @Test
    public void testGetRatingById() {
        Rating rating = new Rating();
        when(ratingRepository.findById(1)).thenReturn(Optional.of(rating));

        Rating foundRating = ratingService.getRatingById(1);

        assertNotNull(foundRating);
        verify(ratingRepository, times(1)).findById(1);
    }

    @Test
    public void testGetRatingById_NotFound() {
        when(ratingRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ratingService.getRatingById(1));
    }

    @Test
    public void testGetAllRatings() {
        RatingFilterOptions options = new RatingFilterOptions();
        Pageable pageable = mock(Pageable.class);
        Set<Rating> ratings = Set.of(new Rating());
        when(ratingRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(ratings);

        Set<Rating> foundRatings = ratingService.getAllRatings(options, pageable);

        assertNotNull(foundRatings);
        assertEquals(1, foundRatings.size());
        verify(ratingRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    public void updateRating_Should_UpdateEntryScoreAndUserPoints_When_UserIsAuthorized() {
        // Arrange
        User organizer = new User();
        organizer.setId(1);
        organizer.setRole(Role.Organizer);
        organizer.setJurorContests(Collections.emptySet());

        Contest contest = new Contest();
        contest.setId(1);

        User participant = new User();
        participant.setId(2);
        participant.setPoints(100);

        Entry entry = new Entry();
        entry.setContest(contest);
        entry.setParticipant(participant);
        entry.setEntryTotalScore(50);

        Rating oldRating = new Rating();
        oldRating.setJuror(organizer);
        oldRating.setScore(5);

        Rating newRating = new Rating();
        newRating.setJuror(organizer);
        newRating.setEntry(entry);
        newRating.setScore(8);

        when(userService.getUserById(organizer.getId())).thenReturn(organizer);
        when(entryRepository.save(any(Entry.class))).thenReturn(entry);
        when(userRepository.save(any(User.class))).thenReturn(participant);
        when(ratingRepository.save(any(Rating.class))).thenReturn(newRating);

        ratingService.updateRating(oldRating.getScore(), newRating, organizer);

        assertEquals(53, entry.getEntryTotalScore());
        assertEquals(103, participant.getPoints());
        verify(entryRepository, times(1)).save(entry);
        verify(userRepository, times(1)).save(participant);
        verify(ratingRepository, times(1)).save(newRating);
    }

    @Test
    void deleteRating_Should_Delete_Rating() {

        User organizer = new User();
        organizer.setId(1);
        organizer.setRole(Role.Organizer);
        organizer.setJurorContests(Collections.emptySet());

        User participant = new User();
        participant.setId(2);
        participant.setPoints(200);

        Contest contest = new Contest();
        contest.setId(1);

        Entry entry = new Entry();
        entry.setContest(contest);
        entry.setParticipant(participant);
        entry.setEntryTotalScore(100);
        entry.setRatings(new HashSet<>());

        Rating rating = new Rating();
        rating.setId(1);
        rating.setScore(50);
        rating.setJuror(organizer);
        rating.setEntry(entry);
        entry.getRatings().add(rating);
        organizer.setRatings(new HashSet<>());
        organizer.getRatings().add(rating);

        when(userService.getUserById(organizer.getId())).thenReturn(organizer);
        when(userService.getUserById(participant.getId())).thenReturn(participant);
        when(entryRepository.save(entry)).thenReturn(entry);
        when(userRepository.save(participant)).thenReturn(participant);

        when(ratingRepository.findById(rating.getId())).thenReturn(Optional.of(rating));

        ratingService.deleteRating(rating.getId(), organizer);

        assertEquals(50, entry.getEntryTotalScore());
        assertEquals(150, participant.getPoints());
        assertEquals(Ranking.Enthusiast, participant.getRanking());

        verify(ratingRepository).delete(rating);
        verify(entryRepository).save(entry);
        verify(userRepository).save(participant);
    }

    @Test
    void deleteRating_Should_Delete_Rating_And_Update_Role_To_Wise_And_Benevolent() {

        int ratingId = 1;
        User organizer = TestHelper.createOrganizerUser();
        organizer.setJurorContests(Collections.emptySet());

        User participant = new User();
        participant.setId(2);
        participant.setPoints(2000);

        Contest contest = new Contest();
        contest.setId(1);

        Entry entry = new Entry();
        entry.setId(1);
        entry.setParticipant(participant);

        Rating ratingToDelete = new Rating();
        ratingToDelete.setId(ratingId);
        ratingToDelete.setScore(10);
        ratingToDelete.setEntry(entry);
        ratingToDelete.setJuror(organizer);

        Set<Rating> ratingSet1 = new HashSet<>();
        ratingSet1.add(ratingToDelete);
        Set<Rating> ratingSet2 = new HashSet<>();
        ratingSet2.add(ratingToDelete);

        organizer.setRatings(ratingSet1);
        entry.setRatings(ratingSet2);

        when(userService.getUserById(organizer.getId())).thenReturn(organizer);
        when(ratingRepository.findById(ratingId)).thenReturn(java.util.Optional.of(ratingToDelete));
        when(userRepository.save(any(User.class))).thenReturn(participant);
        when(entryRepository.save(any(Entry.class))).thenReturn(entry);


        ratingService.deleteRating(ratingId, organizer);

        assertEquals(1990, participant.getPoints());

        assertEquals(Ranking.WiseAndBenevolentPhotoDictator, participant.getRanking());

        verify(ratingRepository).delete(ratingToDelete);
        verify(entryRepository).save(entry);
        verify(userRepository).save(participant);
    }

    @Test
    void testDeleteRating_throwsEntityNotFoundException_whenRatingNotFound() {
        User organizer = new User();
        organizer.setId(1);
        organizer.setRole(Role.Organizer);

        when(ratingRepository.findById(1)).thenReturn(java.util.Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            ratingService.deleteRating(1, organizer);
        });
    }

    @Test
    public void getRatingsForEntry_Should_Get_Entries() {
        User user = new User();
        user.setRole(Role.Organizer);
        Set<Rating> ratings = Set.of(new Rating());
        when(ratingRepository.findByEntryId(1)).thenReturn(ratings);

        Set<Rating> foundRatings = ratingService.getRatingsForEntry(1, user);

        assertNotNull(foundRatings);
        assertEquals(1, foundRatings.size());
        verify(ratingRepository, times(1)).findByEntryId(1);
    }

    @Test
    void Throw_If_Not_Author_Should_Throw() {
        User organizer = TestHelper.createOrganizerUser();

        Rating rating = new Rating();
        rating.setJuror(new User());
        User user = TestHelper.createJunkieUser();


        assertThrows(AuthorizationException.class, () -> ratingService.throwIfNotAuthor(user, rating));
    }

    @Test
    void Throw_If_Not_Author_Or_Organizer_Should_Throw() {
        User organizer = TestHelper.createJunkieUser();

        Rating rating = new Rating();
        rating.setJuror(new User());
        User user = TestHelper.createJunkieUser();

        assertThrows(AuthorizationException.class, () -> ratingService.throwIfNotAuthorOrOrganizer(user, rating));
    }

    @Test
    void Update_User_Ranking_Should_Update_Ranking_To_Enthusiast() {
        User user = new User();
        user.setPoints(Ranking.ENTHUSIAST_POINT_THRESHOLD + 1);
        user.setRanking(Ranking.Master);

        ratingService.updateRanking(user);

        assertEquals(Ranking.Enthusiast, user.getRanking());
    }

    @Test
    void Update_User_Ranking_Should_Update_Ranking_To_Master() {
        User user = new User();
        user.setPoints(Ranking.MASTER_POINT_THRESHOLD + 1);
        user.setRanking(Ranking.Enthusiast);

        ratingService.updateRanking(user);

        assertEquals(Ranking.Master, user.getRanking());
    }

    @Test
    void Update_User_Ranking_Should_Update_Ranking_To_WiseAndBenevolentDictator() {
        User user = new User();
        user.setPoints(Ranking.WISE_AND_BENEVOLENT_POINT_THRESHOLD + 1);
        user.setRanking(Ranking.Enthusiast);

        ratingService.updateRanking(user);

        assertEquals(Ranking.WiseAndBenevolentPhotoDictator, user.getRanking());
    }

    @Test
    void Update_User_Ranking_Should_Update_Ranking_To_Junkie() {
        User user = new User();
        user.setPoints(0);
        user.setRanking(Ranking.Enthusiast);

        ratingService.updateRanking(user);

        assertEquals(Ranking.Junkie, user.getRanking());
    }

    @Test
    void createRating_Should_Throw_If_There_Is_Duplicate() {
        User juror = new User();
        juror.setId(1);
        juror.setRole(Role.Organizer);

        Contest contest = new Contest();
        contest.setId(1);
        juror.setJurorContests(Collections.singleton(contest));

        User participant = new User();
        participant.setId(2);
        participant.setPoints(50);

        Entry entry = new Entry();
        entry.setContest(contest);
        entry.setParticipant(participant);
        entry.setEntryTotalScore(0);

        Rating rating = new Rating();
        rating.setJuror(juror);
        rating.setEntry(entry);
        rating.setScore(10);

        when(userService.getUserById(juror.getId())).thenReturn(juror);
        when(userService.getUserById(participant.getId())).thenReturn(participant);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);
        when(ratingRepository.findByJurorAndEntry(juror, entry)).thenReturn(Optional.of(rating));


        assertThrows(DuplicateEntityException.class, () -> {
            ratingService.createRating(rating);
        });
    }
}
