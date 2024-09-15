package com.example.photocontestproject.servicetests;

import com.example.photocontestproject.TestHelper;
import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.enums.ContestType;
import com.example.photocontestproject.enums.Ranking;
import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.mappers.RatingMapper;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.Rating;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.repositories.ContestRepository;
import com.example.photocontestproject.repositories.UserRepository;
import com.example.photocontestproject.services.ContestServiceImpl;
import com.example.photocontestproject.services.contracts.EntryService;
import com.example.photocontestproject.services.contracts.RatingService;
import com.example.photocontestproject.services.contracts.UserService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContestServiceTests {

    @Mock
    private ContestRepository contestRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RatingService ratingService;
    @Mock
    private RatingMapper ratingMapper;
    @Mock
    private EntryService entryService;

    @InjectMocks
    private ContestServiceImpl contestService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void getContestById_Should_Get_Contest_By_Id() {
        Contest contest = new Contest();
        when(contestRepository.findById(anyInt())).thenReturn(Optional.of(contest));

        Contest result = contestService.getContestById(1);

        assertNotNull(result);
        verify(contestRepository, times(1)).findById(1);
    }

    @Test
    void getContestById_Should_Throw() {
        when(contestRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> contestService.getContestById(1));
    }

    @Test
    void changePhase_Should_Change_Phase() {
        User user = TestHelper.createOrganizerUser();
        Contest contest = new Contest();
        contest.setContestPhase(ContestPhase.PhaseI);
        when(contestRepository.findById(anyInt())).thenReturn(Optional.of(contest));
        when(contestRepository.save(any(Contest.class))).thenReturn(contest);

        Contest result = contestService.changePhase(1, user);

        assertEquals(ContestPhase.PhaseII, result.getContestPhase());
        verify(contestRepository, times(1)).findById(1);
        verify(contestRepository, times(1)).save(contest);
    }

    @Test
    void changePhase_Should_Change_Phase_And_Calculate_Score() {
        User user = TestHelper.createOrganizerUser();
        Contest contest = new Contest();
        contest.setEntries(new ArrayList<>());
        contest.setContestPhase(ContestPhase.PhaseII);
        when(contestRepository.findById(anyInt())).thenReturn(Optional.of(contest));
        when(contestRepository.save(any(Contest.class))).thenReturn(contest);

        Contest result = contestService.changePhase(1, user);

        assertEquals(ContestPhase.Finished, result.getContestPhase());
        verify(contestRepository, times(1)).findById(1);
        verify(contestRepository, times(1)).save(contest);
    }

    @Test
    void changePhase_Should_Throw_If_User_Is_Junkie() {
        User user = TestHelper.createJunkieUser();

        assertThrows(AuthorizationException.class, () -> contestService.changePhase(1, user));
    }

    @Test
    void createContest_Should_Create_Contest() {
        User user = TestHelper.createOrganizerUser();
        Contest contest = new Contest();
        when(contestRepository.save(any(Contest.class))).thenReturn(contest);

        Contest result = contestService.createContest(contest, user);

        assertNotNull(result);
        verify(contestRepository, times(1)).save(contest);
    }

    @Test
    void createContest_Should_Throw_If_User_Is_Junkie() {
        User user = TestHelper.createJunkieUser();
        Contest contest = new Contest();

        assertThrows(AuthorizationException.class, () -> contestService.createContest(contest, user));
    }

    @Test
    void deleteContest_Should_Delete() {
        User user = TestHelper.createOrganizerUser();

        contestService.deleteContest(1, user);

        verify(contestRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteContest_Should_Throw() {
        User user = TestHelper.createJunkieUser();

        assertThrows(AuthorizationException.class, () -> contestService.deleteContest(1, user));
    }


    @Test
    void getAllContests_Should_Get_All_Contests_With_All_Parameters_Are_Null() {
        List<Contest> mockContests = TestHelper.createListOfContests();
        when(contestRepository.findAll(any(Specification.class))).thenReturn(mockContests);

        List<Contest> contests = contestService.getAllContests(null, null, null, null);

        verify(contestRepository).findAll(any(Specification.class));
        assertEquals(2, contests.size());
    }

    @Test
    void getAllContests_Should_Get_All_Contests_With_Title_Not_Null() {
        List<Contest> mockContests = TestHelper.createListOfContests();
        when(contestRepository.findAll(any(Specification.class))).thenReturn(mockContests);

        List<Contest> contests = contestService.getAllContests("Title1", null, null, null);

        verify(contestRepository).findAll(any(Specification.class));
        assertEquals(2, contests.size());
    }

    @Test
    void getAllContests_Should_Get_All_Contests_With_Category_Not_Null() {
        List<Contest> mockContests = TestHelper.createListOfContests();
        when(contestRepository.findAll(any(Specification.class))).thenReturn(mockContests);

        List<Contest> contests = contestService.getAllContests(null, "Category1", null, null);

        verify(contestRepository).findAll(any(Specification.class));
        assertEquals(2, contests.size());
    }

    @Test
    void getAllContests_Should_Get_All_Contests_With_Type_Not_Null() {
        List<Contest> mockContests = TestHelper.createListOfContests();
        when(contestRepository.findAll(any(Specification.class))).thenReturn(mockContests);

        List<Contest> contests = contestService.getAllContests(null, null, ContestType.Open, null);

        verify(contestRepository).findAll(any(Specification.class));
        assertEquals(2, contests.size());
    }

    @Test
    void getAllContests_Should_Get_All_Contests_With_Phase_Not_Null() {
        List<Contest> mockContests = TestHelper.createListOfContests();
        when(contestRepository.findAll(any(Specification.class))).thenReturn(mockContests);

        List<Contest> contests = contestService.getAllContests(null, null, null, ContestPhase.PhaseI);

        verify(contestRepository).findAll(any(Specification.class));
        assertEquals(2, contests.size());
    }

    @Test
    void getAllContests_Should_Get_All_Contests_With_All_Parameters_Not_Null() {
        List<Contest> mockContests = TestHelper.createListOfContests();
        when(contestRepository.findAll(any(Specification.class))).thenReturn(mockContests);

        List<Contest> contests = contestService.getAllContests("Title1", "Category1", ContestType.Open, ContestPhase.PhaseI);

        verify(contestRepository).findAll(any(Specification.class));
        assertEquals(2, contests.size());
    }

    @Test
    void getAllContests_Should_Get_All_Contests_With_Title_Empty() {
        List<Contest> mockContests = TestHelper.createListOfContests();
        when(contestRepository.findAll(any(Specification.class))).thenReturn(mockContests);

        List<Contest> contests = contestService.getAllContests("", null, null, null);

        verify(contestRepository).findAll(any(Specification.class));
        assertEquals(2, contests.size());
    }

    @Test
    void getAllContests_Should_Get_All_Contests_With_Category_Empty() {
        List<Contest> mockContests = TestHelper.createListOfContests();
        when(contestRepository.findAll(any(Specification.class))).thenReturn(mockContests);

        List<Contest> contests = contestService.getAllContests(null, "", null, null);

        verify(contestRepository).findAll(any(Specification.class));
        assertEquals(2, contests.size());
    }

    @Test
    public void getAllContests_Should_Get_All_Contests_With_All_Parameters_Provided() {
        List<Contest> mockContests = TestHelper.createListOfContests();
        String title = "Title1";
        String category = "Category1";
        ContestType type = ContestType.Open;
        ContestPhase phase = ContestPhase.PhaseI;

        when(contestRepository.findAll(any(Specification.class))).thenReturn(mockContests);


        List<Contest> result = contestService.getAllContests(title, category, type, phase);


        assertEquals(mockContests, result);
        verify(contestRepository, times(1)).findAll(any(Specification.class));
    }


    @Test
    public void getAllContest_Should_Get_All_Contests() {
        List<Contest> allContests = TestHelper.createListOfContests();

        when(contestRepository.findAll(Mockito.any(Specification.class))).thenAnswer(new Answer<List<Contest>>() {
            @Override
            public List<Contest> answer(InvocationOnMock invocation) throws Throwable {

                Specification<Contest> specification = invocation.getArgument(0);


                CriteriaBuilder cb = mock(CriteriaBuilder.class);
                CriteriaQuery<Contest> query = mock(CriteriaQuery.class);
                Root<Contest> root = mock(Root.class);


                Predicate predicate = specification.toPredicate(root, query, cb);


                List<Contest> filteredContests = new ArrayList<>();
                for (Contest contest : allContests) {
                    if (contestMatchesPredicate(contest, predicate)) {
                        filteredContests.add(contest);
                    }
                }
                return filteredContests;
            }


            private boolean contestMatchesPredicate(Contest contest, Predicate predicate) {
                return true;
            }
        });

        List<Contest> result = contestService.getAllContests("Title1", "Category1", ContestType.Open, ContestPhase.PhaseI);

        assertEquals(2, result.size());
    }

    @Test
    public void addJuror_Should_Add_Juror_When_User_Is_Organizer_AndUser_Can_Be_Juror() {

        User organizer = new User();
        organizer.setId(1);
        organizer.setRole(Role.Organizer);

        User userToAdd = new User();
        userToAdd.setId(2);
        userToAdd.setRanking(Ranking.Master);

        Contest contest = new Contest();
        contest.setId(1);
        contest.setJurors(new HashSet<>());

        when(userService.getUserById(userToAdd.getId())).thenReturn(userToAdd);
        when(contestRepository.findById(contest.getId())).thenReturn(Optional.of(contest));
        when(contestRepository.save(any(Contest.class))).thenReturn(contest);
        when(userRepository.save(any(User.class))).thenReturn(userToAdd);


        Contest result = contestService.addJuror(contest.getId(), userToAdd.getId(), organizer);


        assertTrue(result.getJurors().contains(userToAdd));
        verify(contestRepository, times(1)).save(result);
        verify(userRepository, times(1)).save(userToAdd);
    }

    @Test
    public void addJuror_Should_Throw_Exception_When_User_Is_Not_Organizer() {
        User nonOrganizer = new User();
        nonOrganizer.setId(1);
        nonOrganizer.setRole(Role.Junkie);
        User userToAdd = new User();
        userToAdd.setId(2);
        userToAdd.setRanking(Ranking.Master);

        Contest contest = new Contest();
        contest.setId(1);
        contest.setJurors(new HashSet<>());


        assertThrows(AuthorizationException.class, () -> {
            contestService.addJuror(contest.getId(), userToAdd.getId(), nonOrganizer);
        });

        verify(contestRepository, never()).save(any(Contest.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void addJuror_Should_Throw_Exception_When_User_Cannot_Be_Juror() {
        // Arrange
        User organizer = new User();
        organizer.setId(1);
        organizer.setRole(Role.Organizer);

        User userToAdd = new User();
        userToAdd.setId(2);
        userToAdd.setRanking(Ranking.Enthusiast);

        Contest contest = new Contest();
        contest.setId(1);
        contest.setJurors(new HashSet<>());

        when(userService.getUserById(userToAdd.getId())).thenReturn(userToAdd);
        when(contestRepository.findById(contest.getId())).thenReturn(Optional.of(contest));

        assertThrows(AuthorizationException.class, () -> {
            contestService.addJuror(contest.getId(), userToAdd.getId(), organizer);
        });

        verify(contestRepository, never()).save(any(Contest.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void getJurors_Should_Return_List_Of_Jurors_When_User_Is_Organizer_And_Contest_Exists() {
        User organizer = new User();
        organizer.setId(1);
        organizer.setRole(Role.Organizer);

        User juror1 = new User();
        juror1.setId(2);
        User juror2 = new User();
        juror2.setId(3);

        Contest contest = new Contest();
        contest.setId(1);
        contest.setJurors(Set.of(juror1, juror2));

        when(contestRepository.findById(contest.getId())).thenReturn(Optional.of(contest));

        List<User> result = contestService.getJurors(contest.getId(), organizer);

        assertEquals(2, result.size());
        assertTrue(result.contains(juror1));
        assertTrue(result.contains(juror2));
        verify(contestRepository, times(1)).findById(contest.getId());
    }

    @Test
    public void addParticipant_Should_AddUser_To_Contest_When_User_Is_Organizer_And_Contest_Is_Valid() {
        User organizer = new User();
        organizer.setId(1);
        organizer.setRole(Role.Organizer);

        User participant = new User();
        participant.setId(2);
        participant.setPoints(10);

        Contest contest = new Contest();
        contest.setId(1);
        contest.setContestType(ContestType.Invitational);
        contest.setParticipants(new HashSet<>());

        when(userService.getUserById(participant.getId())).thenReturn(participant);
        when(contestRepository.findById(contest.getId())).thenReturn(Optional.of(contest));

        Contest result = contestService.addParticipant(contest.getId(), participant.getId(), organizer);

        // Assert
        assertTrue(result.getParticipants().contains(participant));
        assertEquals(13, participant.getPoints()); // Points should increase by 3
        verify(contestRepository, times(1)).save(contest);
        verify(userRepository, times(1)).save(participant);
    }

    @Test
    public void addParticipant_ShouldThrowException_WhenContestIsOpen() {
        User organizer = new User();
        organizer.setId(1);
        organizer.setRole(Role.Organizer);

        User participant = new User();
        participant.setId(2);

        Contest contest = new Contest();
        contest.setId(1);
        contest.setContestType(ContestType.Open);

        when(userService.getUserById(participant.getId())).thenReturn(participant);
        when(contestRepository.findById(contest.getId())).thenReturn(Optional.of(contest));

        assertThrows(AuthorizationException.class, () -> {
            contestService.addParticipant(contest.getId(), participant.getId(), organizer);
        });

        verify(contestRepository, never()).save(any(Contest.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void getParticipants_Should_Return_Participants_When_User_Is_Organizer_And_Contest_Is_Valid() {
        User organizer = new User();
        organizer.setId(1);
        organizer.setRole(Role.Organizer);

        User participant1 = new User();
        participant1.setId(2);
        User participant2 = new User();
        participant2.setId(3);

        Contest contest = new Contest();
        contest.setId(1);
        contest.setContestType(ContestType.Invitational); // Contest type is not Open
        contest.setParticipants(Set.of(participant1, participant2));

        when(contestRepository.findById(contest.getId())).thenReturn(Optional.of(contest));

        List<User> result = contestService.getParticipants(contest.getId(), organizer.getId(), organizer);

        assertEquals(2, result.size());
        assertTrue(result.contains(participant1));
        assertTrue(result.contains(participant2));
        verify(contestRepository, times(1)).findById(contest.getId());
    }


    @Test
    void CalculateAndHandleUserPointsAdding_SharedSpot_Position1() {
        Entry entry = mock(Entry.class);
        User participant = mock(User.class);
        when(entry.getParticipant()).thenReturn(participant);
        when(participant.getPoints()).thenReturn(10);

        contestService.calculateAndHandleUserPointsAdding(entry, true, 1);

        verify(participant).setPoints(50); // 10 + 40
        verify(ratingService).updateRanking(participant);
        verify(userRepository).save(participant);
    }


    @Test
    void calculateScore_Should_Calculate_Score_For_Shared_Spot_For_Position_1() {
        int result = contestService.calculateScore(true, 1);

        Assertions.assertEquals(40, result);
    }

    @Test
    void calculateScore_Should_Calculate_Score_For_Shared_Spot_For_Position_2() {
        int result = contestService.calculateScore(true, 2);

        Assertions.assertEquals(25, result);
    }

    @Test
    void calculateScore_Should_Calculate_Score_For_Shared_Spot_For_Position_3() {
        int result = contestService.calculateScore(true, 3);

        Assertions.assertEquals(10, result);
    }

    @Test
    void calculateScore_Should_Calculate_Score_For_Not_Shared_Spot_For_Position_1() {
        int result = contestService.calculateScore(false, 1);

        Assertions.assertEquals(50, result);
    }

    @Test
    void calculateScore_Should_Calculate_Score_For_Not_Shared_Spot_For_Position_2() {
        int result = contestService.calculateScore(false, 2);

        Assertions.assertEquals(35, result);
    }

    @Test
    void calculateScore_Should_Calculate_Score_For_Not_Shared_Spot_For_Position_3() {
        int result = contestService.calculateScore(false, 3);

        Assertions.assertEquals(20, result);
    }

    @Test
    void calculateScore_Should_Return_Zero_For_Position_Over_3() {
        int result = contestService.calculateScore(false, 4);

        Assertions.assertEquals(0, result);
    }

    @Test
    void handleScoringWhenContestEnds_Should_Handle_Scoring() {
        Contest contest = mock(Contest.class);
        Entry entry1 = mock(Entry.class);
        Entry entry2 = mock(Entry.class);
        Entry entry3 = mock(Entry.class);
        User user1 = mock(User.class);
        User user2 = mock(User.class);
        User user3 = mock(User.class);

        when(contest.getContestPhase()).thenReturn(ContestPhase.Finished);
        when(contest.getEntries()).thenReturn(Arrays.asList(entry1, entry2, entry3));
        when(entry1.getEntryTotalScore()).thenReturn(100);
        when(entry2.getEntryTotalScore()).thenReturn(100);
        when(entry3.getEntryTotalScore()).thenReturn(90);
        when(entry1.getParticipant()).thenReturn(user1);
        when(entry2.getParticipant()).thenReturn(user2);
        when(entry3.getParticipant()).thenReturn(user3);
        when(user1.getPoints()).thenReturn(10);
        when(user2.getPoints()).thenReturn(20);
        when(user3.getPoints()).thenReturn(30);

        contestService.handleScoringWhenContestEnds(contest);

        verify(contest, times(1)).getEntries();
        verify(entry1, times(2)).getEntryTotalScore();
        verify(entry2, times(3)).getEntryTotalScore();
        verify(entry3, times(3)).getEntryTotalScore();
    }

    @Test
    void scheduledTask_Should_Handle_Phase_Change_1() {
        Contest contest = mock(Contest.class);
        when(contest.getContestPhase()).thenReturn(ContestPhase.PhaseI);
        when(contest.getPhase1End()).thenReturn(Timestamp.from(Instant.now().minusSeconds(10)));
        when(contestRepository.findAll()).thenReturn(Arrays.asList(contest));

        contestService.scheduledTask();

        verify(contest).setContestPhase(ContestPhase.PhaseII);
        verify(contestRepository).save(contest);
    }

    @Test
    void scheduledTask_Should_Handle_Phase_Change_2() {
        Contest contest = mock(Contest.class);
        when(contest.getContestPhase()).thenReturn(ContestPhase.PhaseII);
        when(contest.getPhase2End()).thenReturn(Timestamp.from(Instant.now().minusSeconds(10)));
        when(contestRepository.findAll()).thenReturn(Arrays.asList(contest));

        contestService.scheduledTask();

        verify(contest).setContestPhase(ContestPhase.Finished);
        verify(contestRepository).save(contest);
    }

    @Test
    void scheduledTask_Should_Handle_Phase_Change_3() {
        Contest contest = mock(Contest.class);
        when(contest.getContestPhase()).thenReturn(ContestPhase.PhaseI);
        when(contest.getPhase1End()).thenReturn(Timestamp.from(Instant.now().plusSeconds(10)));
        when(contestRepository.findAll()).thenReturn(Arrays.asList(contest));

        contestService.scheduledTask();

        verify(contest, never()).setContestPhase(any());
        verify(contestRepository, never()).save(contest);
    }

    @Test
    void getOrdinalSuffix_Should_Return_Suffix_st() {
        assertEquals("st", contestService.getOrdinalSuffix(1));
        assertEquals("st", contestService.getOrdinalSuffix(21));
        assertEquals("st", contestService.getOrdinalSuffix(101));
    }

    @Test
    void getOrdinalSuffix_Should_Return_Suffix_nd() {
        assertEquals("nd", contestService.getOrdinalSuffix(2));
        assertEquals("nd", contestService.getOrdinalSuffix(22));
        assertEquals("nd", contestService.getOrdinalSuffix(102));
    }

    @Test
    void getOrdinalSuffix_Should_Return_Suffix_rd() {
        assertEquals("rd", contestService.getOrdinalSuffix(3));
        assertEquals("rd", contestService.getOrdinalSuffix(23));
        assertEquals("rd", contestService.getOrdinalSuffix(103));
    }

    @Test
    void getRanks_Test_With_EmptyList() {
        List<Entry> entries = Collections.emptyList();
        Map<Integer, String> ranks = contestService.getRanks(entries);
        assertEquals(0, ranks.size());
    }

    @Test
    void getRanks_Test_With_OneEntry() {
        List<Entry> entries = Arrays.asList(new Entry());
        Map<Integer, String> ranks = contestService.getRanks(entries);
        assertEquals(1, ranks.size());
        assertEquals("st", ranks.get(1));
    }

    @Test
    void getRanks_Test_With_MultipleEntries() {
        List<Entry> entries = Arrays.asList(new Entry(), new Entry(), new Entry(), new Entry());
        Map<Integer, String> ranks = contestService.getRanks(entries);
        assertEquals(4, ranks.size());
        assertEquals("st", ranks.get(1));
        assertEquals("nd", ranks.get(2));
        assertEquals("rd", ranks.get(3));
        assertEquals("th", ranks.get(4));
    }

    @Test
    void scheduledTask_Should_Handle_Phase_Changes() {
        Contest contest1 = mock(Contest.class);
        Contest contest2 = new Contest();
        List<Entry> entries = new ArrayList<>();
        Entry entry = new Entry();
        User user = TestHelper.createJunkieUser();
        user.setPoints(10);
        entry.setParticipant(user);
        entry.setRatings(new HashSet<>());

        User juror = TestHelper.createOrganizerUser();
        Set<User> jurors = new HashSet<>();
        jurors.add(juror);
        entry.setParticipant(user);

        contest2.setJurors(jurors);

        entry.setContest(contest2);
        entries.add(entry);
        contest2.setEntries(entries);
        contest2.setPhase2End(Timestamp.from(Instant.now().minusSeconds(10)));
        contest2.setContestPhase(ContestPhase.PhaseII);
        Contest contest3 = mock(Contest.class);

        when(contest1.getContestPhase()).thenReturn(ContestPhase.PhaseI);
        when(contest1.getPhase1End()).thenReturn(Timestamp.from(Instant.now().minusSeconds(10)));


        when(contest3.getContestPhase()).thenReturn(ContestPhase.Finished);

        List<Contest> contests = Arrays.asList(contest1, contest2, contest3);
        when(contestRepository.findAll()).thenReturn(contests);
        when(ratingMapper.fromDto(any(), any())).thenReturn(new Rating());

        contestService.scheduledTask();

        verify(contest1).setContestPhase(ContestPhase.PhaseII);
        verify(contestRepository, times(2)).save(any(Contest.class));
    }

    @Test
    void throwIfUserIsParticipantInContest_Should_Throw() {
        User user = TestHelper.createJunkieUser();
        Entry entry = new Entry();
        Contest contest = new Contest();
        contest.setParticipants(Set.of(user));
        entry.setContest(contest);

        assertThrows(AuthorizationException.class, () -> {
            contestService.throwIfUserIsParticipantInContest(user, contest);
        });
    }

    @Test
    void throwIfUserIsParticipantInContest_Should_Not_Throw() {
        User user = TestHelper.createJunkieUser();
        Entry entry = new Entry();
        Contest contest = new Contest();
        contest.setParticipants(new HashSet<>());
        entry.setContest(contest);

        contestService.throwIfUserIsParticipantInContest(user, contest);
    }

    @Test
    void throwIfUserIsJurorInContest() {
        User user = TestHelper.createJunkieUser();
        Entry entry = new Entry();
        Contest contest = new Contest();
        contest.setJurors(Set.of(user));
        entry.setContest(contest);

        assertThrows(AuthorizationException.class, () -> {
            contestService.throwIfUserIsJurorInContest(user, contest);
        });
    }

    @Test
    void throwIfUserIsJurorInContest_Should_Not_Throw() {
        User user = TestHelper.createJunkieUser();
        Entry entry = new Entry();
        Contest contest = new Contest();
        contest.setJurors(new HashSet<>());
        entry.setContest(contest);

        contestService.throwIfUserIsJurorInContest(user, contest);
    }

    @Test
    void getFeaturedContest_Should_Return_Contest_With_Max_Entries() {
        List<Contest> finishedContests = new ArrayList<>();
        finishedContests.add(TestHelper.createFinishedContest(1));
        finishedContests.add(TestHelper.createFinishedContest(2));
        Contest contest = TestHelper.createFinishedContest(3);
        contest.getEntries().add(TestHelper.createEntryWithId(1));
        finishedContests.add(contest);

        when(contestService.getAllContests(null, null, null, ContestPhase.Finished)).thenReturn(finishedContests);
        when(contestRepository.findAll(any(Specification.class))).thenReturn(finishedContests);

        Contest result = contestService.getFeaturedContest();

        assertNotNull(result);
        assertEquals(3, result.getEntries().size());
    }

    @Test
    void getContestsWithJuror_Should_Return_Contests_With_Juror_In_PhaseII() {
        User juror = new User();
        juror.setId(1);

        Contest contest1 = TestHelper.createFinishedContest(1);
        Contest contest2 = TestHelper.createFinishedContest(2);
        contest2.setContestPhase(ContestPhase.PhaseII);
        contest2.setJurors(Set.of(juror));
        List<Contest> allContests = Arrays.asList(
                contest1,
                contest2
        );

        when(contestService.getAllContests(null, null, null, null)).thenReturn(allContests);
        when(contestRepository.findAll(any(Specification.class))).thenReturn(allContests);

        List<Contest> result = contestService.getContestsWithJuror(juror);

        assertEquals(1, result.size());
        assertEquals(ContestPhase.PhaseII, result.get(0).getContestPhase());
    }

    @Test
    void getFinishedContestsForUser_Should_Return_Finished_Contests() {
        User user = new User();
        user.setId(1);
        Contest contest1 = TestHelper.createFinishedContest(1);
        Contest contest2 = TestHelper.createFinishedContest(2);
        contest2.setContestPhase(ContestPhase.PhaseI);
        List<Contest> userContests = Arrays.asList(
                contest1,
                contest2
        );

        when(entryService.findContestsByUserId(user.getId())).thenReturn(userContests);

        Set<Contest> result = contestService.getFinishedContestsForUser(user);

        assertEquals(1, result.size());
        assertEquals(ContestPhase.Finished, result.iterator().next().getContestPhase());
    }

    @Test
    void getUnFinishedContestsForUser_Should_Return_Unfinished_Contests() {
        User user = new User();
        user.setId(1);

        Contest contest1 = TestHelper.createFinishedContest(1);
        Contest contest2 = TestHelper.createFinishedContest(2);
        contest2.setContestPhase(ContestPhase.PhaseI);
        List<Contest> userContests = Arrays.asList(
                contest1,
                contest2
        );

        when(entryService.findContestsByUserId(user.getId())).thenReturn(userContests);

        List<Contest> result = contestService.getUnFinishedContestsForUser(user);

        assertEquals(1, result.size());
        assertNotEquals(ContestPhase.Finished, result.get(0).getContestPhase());
    }

    @Test
    void getContestsUserIsNotParticipatingIn_Should_Return_Contests_User_Is_Not_Participating_In() {
        User user = new User();
        user.setId(1);

        Contest contest1 = TestHelper.createFinishedContest(1);
        Contest contest2 = TestHelper.createFinishedContest(2);
        contest2.setContestPhase(ContestPhase.PhaseI);
        contest1.setContestPhase(ContestPhase.PhaseII);
        List<Contest> activeContests = Arrays.asList(
                contest1,
                contest2
        );

        List<Contest> participatingContests = Collections.singletonList(contest1);

        when(contestService.getAllContests(null, null, null, ContestPhase.PhaseI)).thenReturn(activeContests);
        when(contestRepository.findAll(any(Specification.class))).thenReturn(activeContests);
        when(contestService.getUnFinishedContestsForUser(user)).thenReturn(participatingContests);

        List<Contest> result = contestService.getContestsUserIsNotParticipatingIn(user);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getId());
    }

    @Test
    void get3RecentWinners_Should_Return_Three_Recent_Winners() {
        List<Contest> finishedContests = new ArrayList<>();
        Contest contest1 = TestHelper.createFinishedContest(1);
        Contest contest2 = TestHelper.createFinishedContest(2);
        Contest contest3 = TestHelper.createFinishedContest(3);
        Contest contest4 = TestHelper.createFinishedContest(4);

        finishedContests.add(contest1);
        finishedContests.add(contest2);
        finishedContests.add(contest3);
        finishedContests.add(contest4);

        when(contestService.getAllContests(null, null, null, ContestPhase.Finished)).thenReturn(finishedContests);
        when(contestRepository.findAll(any(Specification.class))).thenReturn(finishedContests);

        List<Entry> result = contestService.get3RecentWinners();

        assertEquals(3, result.size());
    }

    @Test
    void get3RecentWinners_Should_Return_Three_Recent_Winners_And_Add_Second_Place_If_Not_Enough_People() {
        List<Contest> finishedContests = new ArrayList<>();
        Contest contest1 = TestHelper.createFinishedContest(1);
        Contest contest2 = TestHelper.createFinishedContest(2);


        finishedContests.add(contest1);
        finishedContests.add(contest2);

        when(contestService.getAllContests(null, null, null, ContestPhase.Finished)).thenReturn(finishedContests);
        when(contestRepository.findAll(any(Specification.class))).thenReturn(finishedContests);

        List<Entry> result = contestService.get3RecentWinners();

        assertEquals(3, result.size());
    }

    @Test
    void get3RecentWinners_Should_Return_Three_Recent_Winners_And_Add_Second_Place_If_Not_Enough_People_With_Only_3_Entries() {
        List<Contest> finishedContests = new ArrayList<>();
        Contest contest1 = TestHelper.createFinishedContest(1);
        Contest contest2 = TestHelper.createFinishedContest(2);
        contest1.getEntries().remove(0);

        finishedContests.add(contest1);
        finishedContests.add(contest2);

        when(contestService.getAllContests(null, null, null, ContestPhase.Finished)).thenReturn(finishedContests);
        when(contestRepository.findAll(any(Specification.class))).thenReturn(finishedContests);

        List<Entry> result = contestService.get3RecentWinners();

        assertEquals(3, result.size());
    }

    @Test
    void get3RecentWinners_Should_Return_Three_Recent_Winners_And_Add_Second_Place_If_Not_Enough_People_With_ManyEntries() {
        List<Contest> finishedContests = new ArrayList<>();
        Contest contest1 = TestHelper.createFinishedContest(1);
        contest1.getEntries().add(TestHelper.createEntryWithId(1));
        contest1.getEntries().add(TestHelper.createEntryWithId(2));
        contest1.getEntries().add(TestHelper.createEntryWithId(3));
        Contest contest2 = TestHelper.createFinishedContest(2);

        finishedContests.add(contest1);
        finishedContests.add(contest2);


        when(contestService.getAllContests(null, null, null, ContestPhase.Finished)).thenReturn(finishedContests);
        when(contestRepository.findAll(any(Specification.class))).thenReturn(finishedContests);

        List<Entry> result = contestService.get3RecentWinners();

        assertEquals(3, result.size());
    }
}
