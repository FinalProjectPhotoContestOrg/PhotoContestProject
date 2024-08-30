package com.example.photocontestproject.servicetests;

import com.example.photocontestproject.TestHelper;
import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.enums.ContestType;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.repositories.ContestRepository;
import com.example.photocontestproject.repositories.EntryRepository;
import com.example.photocontestproject.services.ContestServiceImpl;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContestServiceTests {

    @Mock
    private ContestRepository contestRepository;

    @Mock
    private EntryRepository entryRepository;

    @InjectMocks
    private ContestServiceImpl contestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGetContestById() {
        Contest contest = new Contest();
        when(contestRepository.findById(anyInt())).thenReturn(Optional.of(contest));

        Contest result = contestService.getContestById(1);

        assertNotNull(result);
        verify(contestRepository, times(1)).findById(1);
    }

    @Test
    void testGetContestById_NotFound() {
        when(contestRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> contestService.getContestById(1));
    }

    @Test
    void testChangePhase() {
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
    void testChangePhase_Unauthorized() {
        User user = TestHelper.createJunkieUser();

        assertThrows(AuthorizationException.class, () -> contestService.changePhase(1, user));
    }

    @Test
    void testCreateContest() {
        User user = TestHelper.createOrganizerUser();
        Contest contest = new Contest();
        when(contestRepository.save(any(Contest.class))).thenReturn(contest);

        Contest result = contestService.createContest(contest, user);

        assertNotNull(result);
        verify(contestRepository, times(1)).save(contest);
    }

    @Test
    void testCreateContest_Unauthorized() {
        User user = TestHelper.createJunkieUser();
        Contest contest = new Contest();

        assertThrows(AuthorizationException.class, () -> contestService.createContest(contest, user));
    }

    @Test
    void testDeleteContest() {
        User user = TestHelper.createOrganizerUser();

        contestService.deleteContest(1, user);

        verify(contestRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteContest_Unauthorized() {
        User user = TestHelper.createJunkieUser();

        assertThrows(AuthorizationException.class, () -> contestService.deleteContest(1, user));
    }


    @Test
    void testGetAllContests_AllParametersNull() {
        List<Contest> mockContests = TestHelper.createListOfContests();
        when(contestRepository.findAll(any(Specification.class))).thenReturn(mockContests);

        List<Contest> contests = contestService.getAllContests(null, null, null, null);

        verify(contestRepository).findAll(any(Specification.class));
        assertEquals(2, contests.size());
    }

    @Test
    void testGetAllContests_TitleNotNull() {
        List<Contest> mockContests = TestHelper.createListOfContests();
        when(contestRepository.findAll(any(Specification.class))).thenReturn(mockContests);

        List<Contest> contests = contestService.getAllContests("Title1", null, null, null);

        verify(contestRepository).findAll(any(Specification.class));
        assertEquals(2, contests.size());
    }

    @Test
    void testGetAllContests_CategoryNotNull() {
        List<Contest> mockContests = TestHelper.createListOfContests();
        when(contestRepository.findAll(any(Specification.class))).thenReturn(mockContests);

        List<Contest> contests = contestService.getAllContests(null, "Category1", null, null);

        verify(contestRepository).findAll(any(Specification.class));
        assertEquals(2, contests.size());
    }

    @Test
    void testGetAllContests_TypeNotNull() {
        List<Contest> mockContests = TestHelper.createListOfContests();
        when(contestRepository.findAll(any(Specification.class))).thenReturn(mockContests);

        List<Contest> contests = contestService.getAllContests(null, null, ContestType.Open, null);

        verify(contestRepository).findAll(any(Specification.class));
        assertEquals(2, contests.size());
    }

    @Test
    void testGetAllContests_PhaseNotNull() {
        List<Contest> mockContests = TestHelper.createListOfContests();
        when(contestRepository.findAll(any(Specification.class))).thenReturn(mockContests);

        List<Contest> contests = contestService.getAllContests(null, null, null, ContestPhase.PhaseI);

        verify(contestRepository).findAll(any(Specification.class));
        assertEquals(2, contests.size());
    }

    @Test
    void testGetAllContests_AllParametersNotNull() {
        List<Contest> mockContests = TestHelper.createListOfContests();
        when(contestRepository.findAll(any(Specification.class))).thenReturn(mockContests);

        List<Contest> contests = contestService.getAllContests("Title1", "Category1", ContestType.Open, ContestPhase.PhaseI);

        verify(contestRepository).findAll(any(Specification.class));
        assertEquals(2, contests.size());
    }

    @Test
    void testGetAllContests_TitleIsEmpty() {
        List<Contest> mockContests = TestHelper.createListOfContests();
        when(contestRepository.findAll(any(Specification.class))).thenReturn(mockContests);

        List<Contest> contests = contestService.getAllContests("", null, null, null);

        verify(contestRepository).findAll(any(Specification.class));
        assertEquals(2, contests.size());
    }

    @Test
    void testGetAllContests_CategoryIsEmpty() {
        List<Contest> mockContests = TestHelper.createListOfContests();
        when(contestRepository.findAll(any(Specification.class))).thenReturn(mockContests);

        List<Contest> contests = contestService.getAllContests(null, "", null, null);

        verify(contestRepository).findAll(any(Specification.class));
        assertEquals(2, contests.size());
    }

    @Test
    public void testGetAllContests_AllParametersProvided() {
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
    public void testGetAllContests() {
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
}
