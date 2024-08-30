package com.example.photocontestproject.servicetests;

import com.example.photocontestproject.TestHelper;
import com.example.photocontestproject.dtos.in.RatingDto;
import com.example.photocontestproject.enums.Ranking;
import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.exceptions.AuthorizationException;
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
import java.sql.Timestamp;
import java.time.Instant;
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
        Rating rating = new Rating();
        User juror = TestHelper.createOrganizerUser();
        rating.setJuror(juror);

        User participant = TestHelper.createJunkieUser();
        participant.setId(2);
        participant.setPoints(1);
        Entry entry = new Entry();
        entry.setParticipant(participant);
        rating.setEntry(entry);
        rating.setScore(7);
        rating.setComment("comment");
        rating.setCategoryMismatch(false);
        rating.setReviewedAt(Timestamp.from(Instant.now()));

        when(userService.getUserById(1)).thenReturn(juror);
        when(userService.getUserById(2)).thenReturn(participant);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);

        Rating createdRating = ratingService.createRating(rating);

        assertNotNull(createdRating);
        assertEquals(8, participant.getPoints());
        verify(userService, times(1)).updateUser(participant);
        verify(ratingRepository, times(1)).save(rating);
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
    void testGetRatingByIdNotOrganizer() {
        User user = TestHelper.createJunkieUser();
        int id = 1;

        assertThrows(AuthorizationException.class, () -> {
            ratingService.getRatingById(id, user);
        }, "Expected SecurityException to be thrown");
    }

    @Test
    public void createRating_Should_Create_Rating_And_Update_User_Ranking_To_Enthusiast() {
        Rating rating = new Rating();
        User juror = TestHelper.createOrganizerUser();
        rating.setJuror(juror);

        User participant = TestHelper.createJunkieUser();
        participant.setPoints(50);
        participant.setId(2);
        Entry entry = new Entry();
        entry.setParticipant(participant);
        rating.setEntry(entry);
        rating.setScore(7);
        rating.setComment("comment");
        rating.setCategoryMismatch(false);
        rating.setReviewedAt(Timestamp.from(Instant.now()));

        when(userService.getUserById(1)).thenReturn(juror);
        when(userService.getUserById(2)).thenReturn(participant);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);

        Rating createdRating = ratingService.createRating(rating);

        assertNotNull(createdRating);
        assertEquals(57, participant.getPoints());
        verify(userService, times(1)).updateUser(participant);
        verify(ratingRepository, times(1)).save(rating);
    }

    @Test
    public void createRating_Should_Create_Rating_And_Update_User_Ranking_To_Master() {
        Rating rating = new Rating();
        User juror = TestHelper.createOrganizerUser();
        rating.setJuror(juror);

        User participant = TestHelper.createJunkieUser();
        participant.setPoints(200);
        participant.setId(2);
        Entry entry = new Entry();
        entry.setParticipant(participant);
        rating.setEntry(entry);
        rating.setScore(7);
        rating.setComment("comment");
        rating.setCategoryMismatch(false);
        rating.setReviewedAt(Timestamp.from(Instant.now()));

        when(userService.getUserById(1)).thenReturn(juror);
        when(userService.getUserById(2)).thenReturn(participant);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);

        Rating createdRating = ratingService.createRating(rating);

        assertNotNull(createdRating);
        assertEquals(207, participant.getPoints());
        verify(userService, times(1)).updateUser(participant);
        verify(ratingRepository, times(1)).save(rating);
    }

    @Test
    public void createRating_Should_Create_Rating_And_Update_User_Ranking_To_WiseAndBenevolent() {
        Rating rating = new Rating();
        User juror = TestHelper.createOrganizerUser();
        rating.setJuror(juror);

        User participant = TestHelper.createJunkieUser();
        participant.setPoints(1000);
        participant.setId(2);
        Entry entry = new Entry();
        entry.setParticipant(participant);
        rating.setEntry(entry);
        rating.setScore(7);
        rating.setComment("comment");
        rating.setCategoryMismatch(false);
        rating.setReviewedAt(Timestamp.from(Instant.now()));

        when(userService.getUserById(1)).thenReturn(juror);
        when(userService.getUserById(2)).thenReturn(participant);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);

        Rating createdRating = ratingService.createRating(rating);

        assertNotNull(createdRating);
        assertEquals(1007, participant.getPoints());
        verify(userService, times(1)).updateUser(participant);
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
    void testUpdateRating() {

        int oldScore = 5;
        int newScore = 10;
        int userId = 1;
        int entryId = 1;
        User organizer = new User();
        organizer.setId(userId);
        organizer.setRole(Role.Organizer);

        User participant = new User();
        participant.setId(2);
        participant.setPoints(50);

        Contest contest = new Contest();
        contest.setTitle("testTitle");

        Entry entry = new Entry();
        entry.setId(entryId);
        entry.setParticipant(participant);
        entry.setContest(contest);

        Rating oldRating = new Rating();
        oldRating.setId(1);
        oldRating.setScore(oldScore);
        oldRating.setEntry(entry);
        oldRating.setJuror(organizer);

        Rating updatedRating = new Rating();
        updatedRating.setId(1);
        updatedRating.setScore(newScore);
        updatedRating.setEntry(entry);
        updatedRating.setJuror(organizer);

        when(userService.getUserById(userId)).thenReturn(organizer);
        when(ratingRepository.findById(1)).thenReturn(java.util.Optional.of(oldRating));
        when(userRepository.save(any(User.class))).thenReturn(participant);
        when(ratingRepository.save(any(Rating.class))).thenReturn(updatedRating);

        Rating result = ratingService.updateRating(oldScore, updatedRating, organizer);

        assertEquals(newScore, result.getScore());
        assertEquals(participant, result.getEntry().getParticipant());
        assertEquals(updatedRating, result);

        assertEquals(55, participant.getPoints());
        verify(userRepository).save(participant);

        assertEquals(Ranking.Enthusiast, participant.getRanking());

        verify(ratingRepository).save(updatedRating);
    }

    @Test
    void deleteRating_Should_Delete_Rating() {

        int ratingId = 1;
        User organizer = TestHelper.createOrganizerUser();

        User participant = new User();
        participant.setId(2);
        participant.setPoints(55);

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

        assertEquals(45, participant.getPoints());

        assertEquals(Ranking.Junkie, participant.getRanking());

        verify(ratingRepository).delete(ratingToDelete);
        verify(entryRepository).save(entry);
        verify(userRepository).save(participant);
    }

    @Test
    void deleteRating_Should_Delete_Rating_And_Update_Role_To_Wise_And_Benevolent() {

        int ratingId = 1;
        User organizer = TestHelper.createOrganizerUser();

        User participant = new User();
        participant.setId(2);
        participant.setPoints(2000);

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
    void deleteRating_Should_Delete_Rating_And_Update_Role() {

        int ratingId = 1;
        User organizer = TestHelper.createOrganizerUser();

        User participant = new User();
        participant.setId(2);
        participant.setPoints(300);

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

        assertEquals(290, participant.getPoints());

        assertEquals(Ranking.Master, participant.getRanking());

        verify(ratingRepository).delete(ratingToDelete);
        verify(entryRepository).save(entry);
        verify(userRepository).save(participant);
    }

    @Test
    void testDeleteRating_throwsAuthorizationException_whenNotOrganizer() {
        User nonOrganizer = new User();
        nonOrganizer.setRole(Role.Junkie);

        assertThrows(AuthorizationException.class, () -> {
            ratingService.deleteRating(1, nonOrganizer);
        });
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
    public void testGetRatingsForEntry() {
        User user = new User();
        user.setRole(Role.Organizer);
        Set<Rating> ratings = Set.of(new Rating());
        when(ratingRepository.findByEntryId(1)).thenReturn(ratings);

        Set<Rating> foundRatings = ratingService.getRatingsForEntry(1, user);

        assertNotNull(foundRatings);
        assertEquals(1, foundRatings.size());
        verify(ratingRepository, times(1)).findByEntryId(1);
    }



}
