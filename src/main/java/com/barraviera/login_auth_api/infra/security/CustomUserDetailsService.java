package com.barraviera.login_auth_api.infra.security;

import com.barraviera.login_auth_api.domain.user.User;
import com.barraviera.login_auth_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

// Criamos a classe CustomUserDetailsService que implementa a interface UserDetailsService que vem do spring security
@Component // anotação pra que o spring consiga visualizar esta classe
public class CustomUserDetailsService implements UserDetailsService {

    // Vamos injetar a UserRepository para podermos usar abaixo
    @Autowired
    private UserRepository repository;

    // Metodo obrigatorio
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Criamos uma variavel user do tipo User e vamos atribuir a ela o resultado de findByEmail
        // veja que o username é o email do usuario
        // vamos lançar uma exceção orElseThrow caso nao encontre
        User user = this.repository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // assim que encontrar o usuario vamos retornar um objeto do tipo UserDetails
        // é um user do spring security -> org.springframework.security.core.userdetails.User
        // entao passamos pra este objeto o email, senha e um arraylist de roles que no caso ele nao tem nenhuma entao passamos um array vazio
        // entao ja montamos o UserDetails do spring
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }

}
