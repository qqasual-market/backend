package org.example.market.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.market.entity.Image;
import org.example.market.entity.Product;
import org.example.market.entity.User;
import org.example.market.exception.NotFoundProductException;
import org.example.market.exception.NotFoundUserException;
import org.example.market.repository.ImageRepository;
import org.example.market.repository.ProductsRepository;
import org.example.market.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final ProductsRepository productsRepository;
    private final UserRepository userRepository;
    public static final Logger logger = LogManager.getLogger(MarketService.class);

    public void addImageInProduct(MultipartFile file,final String username,final Long id) throws IOException {
            Optional<User> user = userRepository.findByUsername(username);
            Optional<Product> product = productsRepository.findProductByUsernameAndProductId(username,id);

            if (user.isEmpty()) throw new NotFoundUserException("User not found");
            if (product.isEmpty()) throw new NotFoundProductException("Product not found");
            Image image = Image.builder()
                         .imageName(file.getOriginalFilename())
                         .imageData(file.getBytes())
                         .build();
                 image.setProduct(product.get());
                 imageRepository.save(image);
        }


public void addAvatarUser(final String username,MultipartFile file) throws IOException {
    Optional<User> user = userRepository.findByUsername(username);
       if (user.isEmpty()) throw new NotFoundUserException("User not found");
       Image image = new Image();
            image.setImageData(file.getBytes());
            image.setImageName(file.getOriginalFilename());
            image.setUser(user.get());
        user.get().setImage(image);
    userRepository.save(user.get());
    }


}
