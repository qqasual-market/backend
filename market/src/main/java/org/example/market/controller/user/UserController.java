package org.example.market.controller.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.market.dto.response.ResponseSettingsUser;
import org.example.market.dto.response.ResponseUserInfo;
import org.example.market.dto.request.RequestSettingsUser;
import org.example.market.exception.NotFoundData;
import org.example.market.service.ImageService;
import org.example.market.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/user")
public class UserController {
private final ImageService imageService;
private final ProfileService profileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadAvatarUser(
            @NotBlank @RequestHeader(name = "username") String username,
            MultipartFile file) throws IOException {
            if (file.isEmpty()) {
                return ResponseEntity.ok()
                        .body("File is empty");
            }
            imageService.addAvatarUser(username,file);
            return ResponseEntity.ok()
                    .body("Upload successful");
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<ResponseUserInfo> getProfile(
            @Positive @PathVariable Long profileId,
            @NotBlank @RequestHeader(name = "username") String username) {
      ResponseUserInfo userInfo = profileService.getUserInfo(profileId,username);
      return ResponseEntity.ok().body(userInfo);
    }

    @GetMapping("/settings")
    public ResponseEntity<ResponseSettingsUser> setUserSettings(
            @NotBlank @RequestHeader(name = "AccessToken")String token,
            @Valid @RequestBody RequestSettingsUser settingsUser) {
        if (settingsUser == null || settingsUser.getEmail() == null
        && settingsUser.getUsername() == null) {
            throw new  NotFoundData("The data is empty");
        }
      ResponseSettingsUser settings =  profileService
              .getAndSetSettingsUser(token,settingsUser);
    return ResponseEntity.ok().body(settings);
    }



}
