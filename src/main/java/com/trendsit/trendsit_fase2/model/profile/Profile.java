package com.trendsit.trendsit_fase2.model.profile;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.trendsit.trendsit_fase2.model.comentario.Comentario;
import com.trendsit.trendsit_fase2.model.diretorio.Diretorio;
import com.trendsit.trendsit_fase2.model.friendship.Friendship;
import com.trendsit.trendsit_fase2.model.group.Group;
import com.trendsit.trendsit_fase2.model.postagem.Postagem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({
        "friends",
        "following",
        "followers",
        "sentFriendRequests",
        "receivedFriendRequests",
        "postagens",
        "comentarios",
        "diretorio",
        "group",
        "autor"
})@Table(name = "profiles", uniqueConstraints = {
        @UniqueConstraint(columnNames = "friend_number")
})
public class Profile implements UserDetails {

    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(columnDefinition = "UUID", updatable = false)
    private UUID id;

    @Column(name = "friend_number", unique = true, nullable = false)
    private Long friendNumber;

    @Enumerated(EnumType.STRING)
    private ProfileRole role = ProfileRole.USER;

    private Integer idade;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "username", nullable = false)
    private String username;

    @JsonIgnore
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL)
    private List<Postagem> postagens = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL)
    @JsonManagedReference("comment-author")
    private List<Comentario> comentarios = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diretorio_id")
    private Diretorio diretorio;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(name = "curso")
    private String curso;

    @Column(name = "profile_image")
    private String profileImage = "/default-avatar.png";

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id")
    private Profile autor;

    @ManyToMany
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<Profile> friends = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "follows",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    private Set<Profile> following = new HashSet<>();

    @ManyToMany(mappedBy = "following")
    private Set<Profile> followers = new HashSet<>();

    // Friendship relationships
    @OneToMany(mappedBy = "userFrom", cascade = CascadeType.ALL)
    private Set<Friendship> sentFriendRequests = new HashSet<>();

    @OneToMany(mappedBy = "userTo", cascade = CascadeType.ALL)
    private Set<Friendship> receivedFriendRequests = new HashSet<>();

    public Profile() {}

    public Profile(UUID id, String username) {
        this.id = id;
        this.username = username;
    }

    public Profile(Integer idade, String curso) {
        this.idade = idade;
        this.curso = curso;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return null; // Not used for JWT authentication
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return Objects.equals(id, profile.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}