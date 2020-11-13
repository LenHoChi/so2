package com.example.serviceTest;

import com.example.TestRepositoryConfig;
import com.example.dto.UserDTO;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.impl.UserServiceImpl;
import com.example.utils.convert.UserConvert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

//@RunWith(MockitoJUnitRunner.class)
@RunWith(SpringRunner.class)
@SpringBootTest
//@ContextConfiguration(classes = TestRepositoryConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserServiceTest {
//    cach 1
//    @Mock
//    private UserRepository userRepository;
    //cach 2
    @MockBean
    private UserRepository userRepository;

    //@InjectMocks --->cach 1
    //cach 2 (use like below)
    @Autowired
    private UserServiceImpl userServiceImpl;

    private UserConvert userConvert;
    //cach 1 ---> test voi Mockito
    //dung @runwith(mockito..) thi ko dung cai nay nua
    //    @Before
    //    public void setUp() throws Exception {
    //        MockitoAnnotations.initMocks(this);
    //    }
    //https://loda.me/test-huong-dan-toan-tap-mockito-loda1576641016810/

    //cach 2 ---> test voi @MockBean
    //https://kipalog.com/posts/Spring-Boot--18--Hu-o--ng-da--n-chi-tie--t-Test-Spring-Boot


    @Test
    public void testGetAllUsers() throws Exception {
        User user = new User("len3");
        User user1 = new User("len11");

        List<User> userList = Arrays.asList(user, user1);

        when(userRepository.findAll()).thenReturn(userList);
        List<UserDTO> userList1 = userServiceImpl.getAllUsers();
        assertEquals(userList.size(), userList1.size());
    }

    @Test
    public void testGetUserById() throws Exception {
        User user = new User("len1");
        when(userRepository.findById(user.getEmail())).thenReturn(Optional.of(user));
        Optional<UserDTO> user1 = userServiceImpl.getUserById(user.getEmail());
        assertEquals(user.getEmail(), user1.get().getEmail());
    }

    @Test
    public void testCreateUser() throws Exception {
        User user = new User("len1");
        when(userRepository.save(user)).thenReturn(user);
        UserDTO userDTO = userServiceImpl.saveUser(userConvert.modelToDTO(user));
        //kiem tra xem ham save co duoc thuc thi hay ko. neu ham sava dc goi thi no se tra ve dto co ket
        // qua giong nhu user
        assertEquals(user.getEmail(), userDTO.getEmail());
    }

    @Test
    public void testDeleteUser() throws Exception {
        User user = new User("len1");
        when(userRepository.findById(user.getEmail())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);
        userServiceImpl.deleteUser(user.getEmail());
        //verify kiem tra xem ham findbyid va delete co duoc goi ko
        verify(userRepository, times(1)).findById(user.getEmail());
        verify(userRepository, times(1)).delete(user);
    }
}
