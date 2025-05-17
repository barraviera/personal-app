package com.barraviera.login_auth_api.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// pra dizer que a classe é uma entidade
@Entity
// anotação para dizer que está classe representa a tabela users da banco
@Table(name = "users")
// acessores do lombok
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    // Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // anotação para dizer que esse valor é gerado automaticamente pelo JPA e a estrategia de geração é UUID
    private String id;

    private String name;
    private String email;
    private String password;

}
