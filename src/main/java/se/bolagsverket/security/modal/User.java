package se.bolagsverket.security.modal;


import jakarta.persistence.*;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import se.bolagsverket.core.modal.Pokemon;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "app_user")
@SuppressWarnings("unused")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "hash_password", nullable = false)
    private String hashPassword;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "user_favorite",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "pokemon_id")
    )
    private Set<Pokemon> favorites = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // UserDetails implementation

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return hashPassword;
    }

    @Override
    @NonNull
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }


    //getters / setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public void setUsername(String username) { this.username = username; }

    public void setHashPassword(String hashPassword) { this.hashPassword = hashPassword; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public Set<Pokemon> getFavorites() { return favorites; }
    public void setFavorites(Set<Pokemon> favorites) { this.favorites = favorites; }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void addFavorite(Pokemon pokemon) { this.favorites.add(pokemon); }
    public void removeFavorite(Pokemon pokemon) { this.favorites.remove(pokemon); }
}