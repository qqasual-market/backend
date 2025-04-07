package org.example.market.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.market.controller.user.UserController;
import org.example.market.dto.request.RequestSettingsUser;
import org.example.market.dto.response.ResponseSettingsUser;
import org.example.market.dto.response.ResponseUserInfo;
import org.example.market.service.ImageService;
import org.example.market.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProfileControllerTest {
    @Mock
    private ImageService imageService;

    @InjectMocks
    private UserController userController;

    @Mock
    private ProfileService profileService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();

    }

    @Test
    void testUploadAvatarUser_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", "image content".getBytes());
        final String username = "testUser ";

        doNothing().when(imageService).addAvatarUser (anyString(), any());

        mockMvc.perform(multipart("/user/upload")
                        .file(file)
                        .header("username", username))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Upload successful"));
    }

    @Test
    void testUploadAvatarUser_EmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", new byte[0]);
        final String username = "testUser ";

        mockMvc.perform(multipart("/user/upload")
                        .file(file)
                        .header("username", username))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("File is empty"));
    }

    @Test
    void testGetProfile_Success() throws Exception {
        final Long profileId = 1L;
        final String username = "testUser";
        ResponseUserInfo userInfo = ResponseUserInfo.builder().username(username).purchases(10).build();

        when(profileService.getUserInfo(anyLong(), anyString())).thenReturn(userInfo);

        mockMvc.perform(get("/user/profile/{profileId}", profileId)
                        .header("username", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.purchases").value(10));
    }

    @Test
    void testGetProfile_InvalidProfileId() throws Exception {
        final Long profileId = -1L;
        final String username = "testUser";

        mockMvc.perform(get("/user/profile/{profileId}", profileId)
                        .header("username", username))
                .andExpect(status().isBadRequest());
    }

}
