package org.example.market.service;

import org.example.market.dto.request.RequestSettingsUser;
import org.example.market.dto.response.ResponseImageData;
import org.example.market.dto.response.ResponseSettingsUser;
import org.example.market.dto.response.ResponseSoldProduct;
import org.example.market.dto.response.ResponseUserInfo;
import org.example.market.entity.User;
import org.example.market.entity.enums.Role;
import org.example.market.exception.NotFoundUserException;
import org.example.market.repository.ImageRepository;
import org.example.market.repository.SoldProductRepository;
import org.example.market.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {
    @InjectMocks
    private ProfileService profileService;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private SoldProductRepository soldProductRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    public void testGetAndSetSettingsUser_UserNotFound() {
        final String username = "nonExistingUser";
        RequestSettingsUser request = new RequestSettingsUser();
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(NotFoundUserException.class, () -> {
            profileService.getAndSetSettingsUser(username, request);
        });

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testGetAndSetSettingsUser_UserFoundAndUpdated() {
        final String username = "existingUser";
        RequestSettingsUser request = new RequestSettingsUser();
        request.setUsername("newUsername");
        request.setEmail("newEmail@example.com");

        User user = new User();
        user.setUsername(username);
        user.setEmail("oldEmail@example.com");
        user.setRoles(Collections.singleton(Role.ROLE_USER));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        ResponseSettingsUser response = profileService.getAndSetSettingsUser(username, request);

        assertNotNull(response);
        assertEquals("newUsername", response.getUsername());
        assertEquals("newEmail@example.com", response.getEmail());
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetUserInfo() {
        Long userId = 1L;
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        ResponseImageData imageData = new ResponseImageData();
        imageData.setImageData("base64ImageData".getBytes());
        List<ResponseSoldProduct> stateSeller = List.of();
        List<ResponseSoldProduct> stateBuyer = List.of(new ResponseSoldProduct(),new ResponseSoldProduct());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(imageRepository.findImageByUserId(userId)).thenReturn(Optional.of(imageData));
        when(soldProductRepository.findAllSalesProductByIdUser(userId)).thenReturn(stateSeller);
        when(soldProductRepository.findAllBuyProductByIdUser(userId)).thenReturn(stateBuyer);

        ResponseUserInfo result = profileService.getUserInfo(userId, username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(imageData.getImageData(), result.getImage());

        verify(userRepository, times(1)).findById(userId);
        verify(imageRepository, times(1)).findImageByUserId(userId);
        verify(soldProductRepository, times(1)).findAllSalesProductByIdUser(userId);
        verify(soldProductRepository, times(1)).findAllBuyProductByIdUser(userId);
    }


    @Test
    void testSummingSalesAndPurchases_EmptyList() {
        List<ResponseSoldProduct> products = Collections.emptyList();

        int result = profileService.summingSalesAndPurchases(products);

        assertEquals(0, result, "Сумма должна быть 0 для пустого списка");
    }

    @Test
    void testSummingSalesAndPurchases_AllQuantitiesPositive() {
        ResponseSoldProduct product1 = new ResponseSoldProduct();
        product1.setQuantity(10);
        ResponseSoldProduct product2 = new ResponseSoldProduct();
        product2.setQuantity(20);
        List<ResponseSoldProduct> products = List.of(product1, product2);

        int result = profileService.summingSalesAndPurchases(products);

        assertEquals(30, result, "Сумма должна быть 30 (10 + 20)");
    }



}
