package org.example.market.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.market.dto.request.ProductRequest;
import org.example.market.dto.request.ProductUpdateRequest;
import org.example.market.service.ImageService;
import org.example.market.service.MarketService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("products")
public class ProductsController {

    private final MarketService marketService;
    private final ImageService imageService;

    @PostMapping("/create/")
    public ResponseEntity<Void> createProduct (
      @NotBlank @RequestHeader(name = "username") String username,
      @Valid @RequestBody ProductRequest request) {
         marketService.createProduct(username,request);
         return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/")
    public ResponseEntity<Void> deleteProduct (
    @NotBlank @RequestHeader(name = "username") String username,
    @Positive @RequestHeader("Id") Long id,
    @NotBlank @RequestParam("address") String address) {
        marketService.deleteProductByUser(id,username);
        return ResponseEntity.ok().build();
    }

  @GetMapping("/get")
  public ResponseEntity<List> getAllProduct(){
  return ResponseEntity.ok()
         .body(marketService.getAllProducts());
  }


  @PostMapping("/buy/")
  public ResponseEntity<String> buyProduct(
         @NotBlank @RequestHeader(name = "username") String username,
         @NotNull @Positive @RequestParam Long id,
         @NotNull @Positive @RequestParam Integer quantity,
         @NotBlank @RequestParam("address") String address  ) {
  marketService.buyProduct(username,id,quantity,address);
  return ResponseEntity.ok().body("Success Buy!");
  }


@PutMapping("/update/")
public ResponseEntity<String> updateProduct (
        @NotBlank @RequestHeader("username") String username,
        @Valid @RequestBody ProductUpdateRequest request,
        @Positive @RequestParam Long id) {
    marketService.updateProduct(id,username,request);
    return ResponseEntity.ok("Товар успешно обновлён!");
    }

@PostMapping("/upload")
public ResponseEntity<String> uploadImageInProduct(
      MultipartFile file,
      @Positive Long id,
      @NotBlank @RequestHeader(name = "username") String username) throws IOException {
    if (file.isEmpty()) {
        return ResponseEntity.badRequest().body("Файл не найден");
    }
    imageService.addImageInProduct(file,username,id);
    return ResponseEntity.ok().body("Success Upload!");
}



}