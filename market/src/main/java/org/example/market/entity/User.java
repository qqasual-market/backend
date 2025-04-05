package org.example.market.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.market.entity.enums.Role;
import org.springframework.lang.Nullable;


import java.math.BigDecimal;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Getter
    @Nullable
    @Column(name = "email")
    private String email;

    private BigDecimal balance;

    @OneToOne(cascade = CascadeType.ALL,targetEntity = Image.class)

    @JoinColumns({
    @JoinColumn(name = "image_id", referencedColumnName = "id"),
    @JoinColumn(name = "user_image",referencedColumnName = "image_data")})
    private Image image;

    @Enumerated(EnumType.STRING)
    @CollectionTable(name ="roles",joinColumns = @JoinColumn(name = "user_id"))
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;

    @OneToMany(mappedBy = "user",targetEntity = Product.class,fetch = FetchType.LAZY)
    private Set<Product> product = new HashSet<Product>();


    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public String getPassword() {
        return password;
    }
    public String getUsername() {
        return username;
    }


    public String getEmail() {
        return email;
    }
}



