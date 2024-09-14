package com.example.photocontestproject.servicetests;

import com.example.photocontestproject.TestHelper;
import com.example.photocontestproject.enums.Ranking;
import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.exceptions.DuplicateEntityException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.external.service.EmailService;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.repositories.UserRepository;
import com.example.photocontestproject.services.UserServiceImpl;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    EmailService emailService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findById_Should_Find_By_Id() {
        User user = TestHelper.createJunkieUser();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(1);

        assertEquals(user.getId(), foundUser.getId());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testGetNextRankPoints() {
        assertEquals(21, userService.getNextRankPoints(30));  // Assuming 100 points needed for next rank from 50
        assertEquals(51, userService.getNextRankPoints(100));  // Assuming 50 points needed for next rank from 150
        assertEquals(801, userService.getNextRankPoints(200));   // Assuming 200 is the max rank points
    }

    @Test
    public void getUsersByRole_Should_Get_Users_By_Role() {

        Role role = Role.Junkie;
        User user1 = TestHelper.createJunkieUser();
        User user2 = TestHelper.createJunkieUser();
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findByRole(role)).thenReturn(users);


        List<User> result = userService.getUsersByRole(role);


        assertEquals(users, result);
        verify(userRepository).findByRole(role);
    }

    @Test
    public void testGetMasters() {

        User user1 = TestHelper.createJunkieUser();
        User user2 = TestHelper.createJunkieUser();
        List<User> masters = Arrays.asList(user1, user2);
        when(userRepository.findByRanking(Ranking.Master)).thenReturn(masters);


        List<User> result = userService.getMasters();


        assertEquals(masters, result);
        verify(userRepository).findByRanking(Ranking.Master);
    }

    @Test
    public void findById_Should_Throw_When_No_User() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.getUserById(1);
        });
    }

    @Test
    public void createUser_Should_Create_User() {
        User user = TestHelper.createJunkieUser();

        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(emailService).sendEmailForRegister(user.getEmail(), user.getUsername());
        User savedUser = userService.createUser(user);

        assertEquals(user.getId(), savedUser.getId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void updateUser_Should_Update_User() {
        User user = TestHelper.createJunkieUser();

        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUser(user);

        assertEquals(user.getId(), updatedUser.getId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void deleteUserById_Should_Delete_User() {
        User user = TestHelper.createJunkieUser();

        userService.deleteUserById(user.getId());

        verify(userRepository, times(1)).deleteById(user.getId());
    }

    @Test
    public void getByUsername_Should_Find_By_Username() {
        User user = TestHelper.createJunkieUser();

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        User foundUser = userService.getByUsername("username");

        assertEquals(user.getId(), foundUser.getId());
        verify(userRepository, times(1)).findByUsername("username");
    }

    @Test
    public void getByUsername_Should_Throw_When_No_User() {
        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.getByUsername("username");
        });
    }

    @Test
    public void getAllUsers_Should_Get_All_Users() {
        List<User> allUsers = TestHelper.createListOfUsers();

        when(userRepository.findAll(Mockito.any(Specification.class))).thenAnswer(new Answer<List<User>>() {
            @Override
            public List<User> answer(InvocationOnMock invocation) throws Throwable {

                Specification<User> specification = invocation.getArgument(0);


                CriteriaBuilder cb = mock(CriteriaBuilder.class);
                CriteriaQuery<User> query = mock(CriteriaQuery.class);
                Root<User> root = mock(Root.class);


                Predicate predicate = specification.toPredicate(root, query, cb);


                List<User> filteredUsers = new ArrayList<>();
                for (User user : allUsers) {
                    if (userMatchesPredicate(user, predicate)) {
                        filteredUsers.add(user);
                    }
                }
                return filteredUsers;

            }


            private boolean userMatchesPredicate(User user, Predicate predicate) {
                return true;
            }
        });


        List<User> result = userService.getAllUsers("username1", "John", "Doe");


        assertEquals(3, result.size());
        assertEquals("User1", result.get(0).getUsername());
        assertEquals("User2", result.get(1).getUsername());
        assertEquals("User3", result.get(2).getUsername());
    }

    @Test
    public void getAllUsers_Should_Filter_By_Title() {
        List<User> allUsers = TestHelper.createListOfUsers();

        when(userRepository.findAll(Mockito.any(Specification.class))).thenAnswer(new Answer<List<User>>() {
            @Override
            public List<User> answer(InvocationOnMock invocation) throws Throwable {

                Specification<User> specification = invocation.getArgument(0);


                CriteriaBuilder cb = mock(CriteriaBuilder.class);
                CriteriaQuery<User> query = mock(CriteriaQuery.class);
                Root<User> root = mock(Root.class);


                Predicate predicate = specification.toPredicate(root, query, cb);


                List<User> filteredUsers = new ArrayList<>();
                for (User user : allUsers) {
                    if (userMatchesPredicate(user, predicate)) {
                        filteredUsers.add(user);
                    }
                }
                return filteredUsers;

            }


            private boolean userMatchesPredicate(User user, Predicate predicate) {
                return user.getUsername().equals("User1");
            }
        });


        List<User> result = userService.getAllUsers("username1", "John", "Doe");


        assertEquals(1, result.size());
        assertEquals("User1", result.get(0).getUsername());

    }

    @Test
    void throwIfDuplicate_Should_Throw() {
        User user = TestHelper.createJunkieUser();

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        assertThrows(DuplicateEntityException.class, () -> {
            userService.throwIfUserIsDuplicate("username");
        });
    }
}
