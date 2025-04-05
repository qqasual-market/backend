package org.example.market.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.market.dto.ProductDTO;
import org.example.market.dto.request.ProductRequest;
import org.example.market.service.ImageService;
import org.example.market.service.MarketService;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock
    private MarketService marketService;

    @InjectMocks
    private ProductsController productController;

    @Mock
    private ImageService imageService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();

    }

    @Test
    void testCreateProduct_Success() throws Exception {
        final String username = "testUser";
        final ProductRequest productRequest = ProductRequest.builder().categoryProduct("HOME").productName("test").productPrice(BigDecimal.valueOf(100)).quantity(10).build();
        doNothing().when(marketService).createProduct(any(String.class), any(ProductRequest.class));
        mockMvc.perform(post("/products/create/")
                        .header("username", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateProduct_MissingUsernameHeader() throws Exception {
        final ProductRequest productRequest = ProductRequest.builder().categoryProduct("HOME").productName("test").productPrice(BigDecimal.valueOf(100)).quantity(10).build();
        mockMvc.perform(post("/products/create/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteProduct_Success() throws Exception {
        String username = "testUser";
        Long id = 1L;
        String address = "123 Main St";

        doNothing().when(marketService).deleteProductByUser(anyLong(), any(String.class));

        mockMvc.perform(delete("/products/delete/")
                        .header("username", username)
                        .header("Id", id)
                        .param("address", address))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteProduct_MissingAddressParam() throws Exception {
        String username = "testUser";
        Long id = 1L;

            mockMvc.perform(delete("/products/delete/")
                        .header("username", username)
                        .header("Id", id))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllProducts_Success() throws Exception {
        List<ProductDTO> dto = new ArrayList<>();
        dto.add(ProductDTO.builder().productName("test").username("test-username").build());
        dto.add(ProductDTO.builder().productName("test1").username("test-username1").build());

        when(marketService.getAllProducts()).thenReturn(dto);

        mockMvc.perform(get("/products/get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].productName").value("test"))
                .andExpect(jsonPath("$[0].username").value("test-username"))
                .andExpect(jsonPath("$[1].productName").value("test1"))
                .andExpect(jsonPath("$[1].username").value("test-username1"));
    }


    @Test
    void testUploadImageInProduct_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test image content".getBytes());
        String username = "testUser";
        Long id = 1L;

        doNothing().when(imageService).addImageInProduct(any(), anyString(), anyLong());

        mockMvc.perform(multipart("/products/upload")
                        .file(file)
                        .header("username", username)
                        .param("id", id.toString()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Success Upload!"));
    }

    @Test
    void testUploadImageInProduct_EmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", new byte[0]);
        String username = "testUser";
        Long id = 1L;

        mockMvc.perform(multipart("/products/upload")
                        .file(file)
                        .header("username", username)
                        .param("id", id.toString()))
                .andExpect(status().isBadRequest());
    }
}
