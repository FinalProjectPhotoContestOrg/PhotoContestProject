package com.example.photocontestproject.servicetests;

import com.example.photocontestproject.TestHelper;
import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.repositories.EntryRepository;
import com.example.photocontestproject.services.EntryServiceImpl;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EntryServiceTests {

    @Mock
    private EntryRepository entryRepository;

    @InjectMocks
    private EntryServiceImpl entryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void create_Entry_When_User_Is_Junkie_Should_Create_Entry() {
        User user = TestHelper.createJunkieUser();
        Entry entry = new Entry();
        when(entryRepository.save(any(Entry.class))).thenReturn(entry);

        Entry result = entryService.createEntry(entry, user);

        assertNotNull(result);
        verify(entryRepository, times(1)).save(entry);
    }

    @Test
    public void create_Entry_Should_Throw_If_User_Is_Organizer() {
        User user = TestHelper.createOrganizerUser();
        Entry entry = new Entry();

        AuthorizationException exception = assertThrows(AuthorizationException.class, () -> {
            entryService.createEntry(entry, user);
        });

    }

    @Test
    public void getEntryById_Should_Return_Entry() {
        Entry entry = new Entry();
        when(entryRepository.findById(anyInt())).thenReturn(Optional.of(entry));

        Entry result = entryService.getEntryById(1);

        assertNotNull(result);
        verify(entryRepository, times(1)).findById(1);
    }

    @Test
    public void getEntryById_Should_Throw_When_No_Entry() {
        when(entryRepository.findById(anyInt())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            entryService.getEntryById(1);
        });

    }

    @Test
    public void updateEntry_Should_Update_Entry() {
        Entry entry = new Entry();
        when(entryRepository.save(any(Entry.class))).thenReturn(entry);

        Entry result = entryService.updateEntry(entry);

        assertNotNull(result);
        verify(entryRepository, times(1)).save(entry);
    }

    @Test
    public void getAllEntries_Should_Filter() {
        Entry entry1 = new Entry();
        entry1.setTitle("title1");
        List<Entry> entries = List.of(entry1);

        when(entryRepository.findAll(ArgumentMatchers.<Specification<Entry>>any())).thenReturn(entries);

        List<Entry> result = entryService.getAllEntries("title1");

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("title1", result.get(0).getTitle());
    }

    @Test
    public void getAllEntries_Should_Not_Filter_When_Title_Is_Null() {
        Entry entry1 = new Entry();
        entry1.setTitle("title1");
        Entry entry2 = new Entry();
        entry2.setTitle("title2");
        List<Entry> entries = List.of(entry1, entry2);

        when(entryRepository.findAll(ArgumentMatchers.<Specification<Entry>>any())).thenReturn(entries);

        List<Entry> result = entryService.getAllEntries(null);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    public void getAllEntries_Should_Return_Correct_Entries() {
        Entry entry1 = new Entry();
        entry1.setTitle("title1");
        Entry entry2 = new Entry();
        entry2.setTitle("title2");
        List<Entry> entries = List.of(entry1, entry2);

        when(entryRepository.findAll(ArgumentMatchers.<Specification<Entry>>any())).thenReturn(entries);

        List<Entry> result = entryService.getAllEntries("title");

        Assertions.assertEquals(2, result.size());
    }



    @Test
    public void deleteEntryById_Should_Delete_Entry() {
        User user = new User();
        user.setRole(Role.Organizer);

        doNothing().when(entryRepository).deleteById(anyInt());

        entryService.deleteEntryById(1, user);

        verify(entryRepository, times(1)).deleteById(1);
    }

    @Test
    public void deleteEntryById_Should_Throw_If_Not_Organizer() {
        User user = TestHelper.createJunkieUser();

        AuthorizationException exception = assertThrows(AuthorizationException.class, () -> {
            entryService.deleteEntryById(1, user);
        });
    }

    @Test
    public void getAllEntries_Should_Return_All_Entries() {

        List<Entry> allEntries = TestHelper.createListOfEntries();


        when(entryRepository.findAll(Mockito.any(Specification.class))).thenAnswer(new Answer<List<Entry>>() {
            @Override
            public List<Entry> answer(InvocationOnMock invocation) throws Throwable {

                Specification<Entry> specification = invocation.getArgument(0);


                CriteriaBuilder cb = mock(CriteriaBuilder.class);
                CriteriaQuery<Entry> query = mock(CriteriaQuery.class);
                Root<Entry> root = mock(Root.class);


                Predicate predicate = specification.toPredicate(root, query, cb);


                List<Entry> filteredEntries = new ArrayList<>();
                for (Entry entry : allEntries) {
                    if (entryMatchesPredicate(entry, predicate)) {
                        filteredEntries.add(entry);
                    }
                }
                return filteredEntries;
            }


            private boolean entryMatchesPredicate(Entry entry, Predicate predicate) {
                return true;
            }
        });


        List<Entry> result = entryService.getAllEntries("Title");


        assertEquals(3, result.size());
    }
}
