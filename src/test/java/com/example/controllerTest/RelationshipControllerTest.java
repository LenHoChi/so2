package com.example.controllerTest;

import com.example.TestRepositoryConfig;
import com.example.controller.RelationshipController;
import com.example.controller.UserController;
import com.example.dto.RelationshipDTO;
import com.example.dto.UserDTO;
import com.example.model.Relationship;
import com.example.model.RelationshipPK;
import com.example.model.User;
import com.example.repository.RelationshipRepository;
import com.example.service.RelationshipService;
import com.example.service.UserService;
import com.example.utils.request.RequestFriends;
import com.example.utils.request.RequestFriendsList;
import com.example.utils.request.RequestReciveUpdate;
import com.example.utils.request.RequestSubcriber;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(RelationshipController.class)
@AutoConfigureMockMvc(addFilters = false)

//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RelationshipControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RelationshipService relationshipService;

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetAllRelationships() throws Exception {
        RelationshipPK relationshipPK = new RelationshipPK("len1", "len10");
        RelationshipDTO relationshipDTO = new RelationshipDTO(relationshipPK, true, false, false);

        RelationshipPK relationshipPK1 = new RelationshipPK("len2", "len10");
        RelationshipDTO relationshipDTO1 = new RelationshipDTO(relationshipPK1, true, false, false);

        List<RelationshipDTO> relationshipDTOList = Arrays.asList(relationshipDTO, relationshipDTO1);
        //given(relationshipService.getAllRelationships()).willReturn(relationshipDTOList);
        when(relationshipService.getAllRelationships()).thenReturn(relationshipDTOList);
        mockMvc.perform(get("/api/relationship"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].relationshipPK.userEmail", is("len1")))
                .andExpect(jsonPath("$[1].relationshipPK.userEmail", is("len2")))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)));
        verify(relationshipService, times(1)).getAllRelationships();
        verifyNoMoreInteractions(relationshipService);
    }
//loi 404 xay ra khi co the url sai
    @Test
    public void testGetRelationship() throws Exception {
        RelationshipPK relationshipPK = new RelationshipPK("newmooncsu@gmail.com", "newmooncsu2@gmail.com");
        RelationshipDTO relationshipDTO = new RelationshipDTO(relationshipPK, true, false, false);
        when(relationshipService.getRelationshipById(relationshipPK)).thenReturn(Optional.of(relationshipDTO));
        mockMvc.perform(post("/api/relationship/getRelationshipbyId")
                .content(asJsonString(relationshipPK))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.relationshipPK.userEmail", is("newmooncsu@gmail.com")))
                .andExpect(content().contentType("application/json"));
        verify(relationshipService, times(1)).getRelationshipById(relationshipPK);
        verifyNoMoreInteractions(relationshipService);
    }

    @Test
    public void testBeFriends() throws Exception {
        List<String> listEmail = new ArrayList<>();
        listEmail.add("len1");
        listEmail.add("len10");
        RequestFriends requestFriends = new RequestFriends(listEmail);

        User user1 = new User("len1");
        User user2 = new User("len10");

        when(relationshipService.beFriends(user1.getEmail(), user2.getEmail())).thenReturn(true);
//        when(relationshipService.beFriends(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        mockMvc.perform(post("/api/relationship")
                .content(asJsonString(requestFriends))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
        verify(relationshipService, times(1)).beFriends(user1.getEmail(), user2.getEmail());
        verifyNoMoreInteractions(relationshipService);
    }

    @Test
    public void testGetFriendsList() throws Exception {
        User user1 = new User("len1");
        RequestFriendsList requestFriendsList = new RequestFriendsList(user1.getEmail());

        List<String> listEmail = new ArrayList<>();
        listEmail.add("len1");
        listEmail.add("len10");
/*Tư tưởng của Mock đơn giản là khi muốn test (A gọi B) thì thay vì
tạo ra một đối tượng B thật sự, bạn tạo ra
một thằng B' giả mạo, có đầy đủ chức năng như B thật (nhưng không phải thật)
Bạn sẽ giả lập cho B' biết là khi thằng A gọi tới nó,
nó cần làm gì, trả lại cái gì (hardcode).
Miễn làm sao cho nó trả ra đúng cái thằng A cần để
chúng ta có thể test A thuận lợi nhất.*/


        /*ở đây cần test là những phương thức trong servise--->cotroller
        nên ta tạo ra thằng servise giả có đầy
        * đủ chức năng như thật.... sau đó cho nó biết khi gọi hàm getfriendslist thì nó cần
        * trả về cái gì*/
        when(relationshipService.getFriendsList(requestFriendsList.getEmail())).thenReturn(listEmail);
        /*sau đó ta bắt đầu test A (phương thức post) khi đã có B(hàm gerfriendslist)*/

        /*CHỐT:----> Nghĩa là nhét kịch bản vào service sau đó dùng để test controller*/
        /*test đâu chạy đó. tét controller thì chạy ở controoler
        * nên ko cần quan tâm phương thức existsbyid như bên test service*/
        mockMvc.perform(post("/api/relationship/friendsList")
                .content(asJsonString(requestFriendsList))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
        verify(relationshipService, times(1)).getFriendsList(requestFriendsList.getEmail());
        verifyNoMoreInteractions(relationshipService);
    }
    @Test
    public void testGetCommonFriendsList() throws Exception{
        List<String> listEmail = new ArrayList<>();
        listEmail.add("len1");
        listEmail.add("len2");
        RequestFriends requestFriends = new RequestFriends(listEmail);

        List<String> lstEmail = new ArrayList<>();
        lstEmail.add("len1");
        lstEmail.add("len2");

        when(relationshipService.getCommonFriendsList(lstEmail)).thenReturn(lstEmail);
        mockMvc.perform(post("/api/relationship/commonFriendsList")
                .content(asJsonString(requestFriends))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
        verify(relationshipService, times(1)).getCommonFriendsList(lstEmail);
        verifyNoMoreInteractions(relationshipService);
    }

    @Test
    public void testBeSubciber() throws  Exception{
        User user1 = new User("len1");
        User user2 = new User("len10");

        RequestSubcriber requestSubcriber = new RequestSubcriber(user1.getEmail(), user2.getEmail());

        when(relationshipService.beSubciber(user1.getEmail(), user2.getEmail())).thenReturn(true);

        mockMvc.perform(post("/api/relationship/beSubcriber")
                .content(asJsonString(requestSubcriber))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
        verify(relationshipService, times(1)).beSubciber(user1.getEmail(), user2.getEmail());
        verifyNoMoreInteractions(relationshipService);
    }

    @Test
    public void testToBlock() throws Exception{
        User user1 = new User("len1");
        User user2 = new User("len10");
        RequestSubcriber requestSubcriber = new RequestSubcriber(user1.getEmail(), user2.getEmail());

        when(relationshipService.toBlock(user1.getEmail(), user2.getEmail())).thenReturn(true);
        mockMvc.perform(post("/api/relationship/toBlock")
                .content(asJsonString(requestSubcriber))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
        verify(relationshipService, times(1)).toBlock(user1.getEmail(), user2.getEmail());
        verifyNoMoreInteractions(relationshipService);
    }

    @Test
    public void testReceiveUpdate() throws Exception{
        User user = new User("len1");
        List<String> list = new ArrayList<>();
        list.add("len");
        RequestReciveUpdate requestReciveUpdate = new RequestReciveUpdate(user.getEmail());

        when(relationshipService.getReceiveUpdateList(user.getEmail())).thenReturn(list);
        mockMvc.perform(post("/api/relationship/receiveUpdate")
                .content(asJsonString(requestReciveUpdate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
        verify(relationshipService, times(1)).getReceiveUpdateList(user.getEmail());
        verifyNoMoreInteractions(relationshipService);
    }
}
