package br.com.alura.forumhub.backend.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidade que representa um usuário no sistema. Implementa UserDetails para ser utilizado pelo
 * Spring Security.
 */
@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@ToString(exclude = {"perfis"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(toBuilder = true)
public class Usuario implements UserDetails {

  /**
   * Constructor with defensive copying for mutable fields.
   *
   * @param id ID do usuário
   * @param nome nome do usuário
   * @param email email do usuário
   * @param senha senha do usuário
   * @param perfis perfis do usuário
   */
  public Usuario(Integer id, String nome, String email, String senha, Set<Perfil> perfis) {
    this.id = id;
    this.nome = nome;
    this.email = email;
    this.senha = senha;

    // Use setter method for defensive copying
    this.setPerfis(perfis);
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Integer id;

  @NotBlank(message = "O nome é obrigatório")
  @Column(nullable = false)
  private String nome;

  @NotBlank(message = "O email é obrigatório")
  @Email(message = "O email deve ser válido")
  @Column(unique = true, nullable = false)
  private String email;

  @NotBlank(message = "A senha é obrigatória")
  @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
  @Column(nullable = false)
  private String senha;

  @NotEmpty(message = "O usuário deve ter pelo menos um perfil")
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "usuario_perfil",
      joinColumns = @JoinColumn(name = "usuario_id"),
      inverseJoinColumns = @JoinColumn(name = "perfil_id"))
  private Set<Perfil> perfis = new HashSet<>();

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return getPerfis(); // Use the defensive copy method
  }

  @Override
  public String getPassword() {
    return this.senha;
  }

  @Override
  public String getUsername() {
    return this.email;
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
}
