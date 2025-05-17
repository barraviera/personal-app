package com.barraviera.login_auth_api.controllers;

import com.barraviera.login_auth_api.domain.user.User;
import com.barraviera.login_auth_api.dto.LoginRequestDTO;
import com.barraviera.login_auth_api.dto.RegisterRequestDTO;
import com.barraviera.login_auth_api.dto.ResponseDTO;
import com.barraviera.login_auth_api.infra.security.TokenService;
import com.barraviera.login_auth_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth") // pra informar o endpoint que este controller ficará ouvindo
@RequiredArgsConstructor // pra que o lombok gere automaticamente o construtor desta classe contendo como parametro o repository, passwordEncoder e tokenService
public class AuthController {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO body) {

        // Vamos localizar o usuario
        User user = this.repository.findByEmail(body.email())
                // caso nao encontre o usuario iremos retornar uma excessao
                .orElseThrow(() -> new RuntimeException("User not found"));

        // vamos comparar a senha que veio por parametro no body é a mesma que está no banco de dados
        if(passwordEncoder.matches(body.password(), user.getPassword())) {
            // se as senhas forem iguais iremos criar um token usando o metodo generateToken da classe TokenService
            String token = this.tokenService.generateToken(user);
            // vamos retornar os dados que o frontend precisa, que no caso é um token e um nome
            // pra isso criamos uma classe ResponseDTO que recebe um nome e um token
            return ResponseEntity.ok(new ResponseDTO(user.getName(), token));
        }

        // caso as senhas nao sejam iguais...
        return ResponseEntity.badRequest().build();
    }

    // metodo pra criar usuario
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDTO body) {

        // Verificar se no banco de dados tem o usuario
        Optional<User> user = this.repository.findByEmail(body.email());

        // se o usuario ainda nao existir vamos criar um novo
        if(user.isEmpty()) {

            // Vamos criar um novo objeto User
            User newUser = new User();
            // setar a senha, mas antes vamos usar o metodo passwordEncoder para criptografar
            newUser.setPassword(passwordEncoder.encode(body.password())); // password que vem do body por parametro
            newUser.setEmail(body.email());
            newUser.setName(body.name());
            // criamos um objeto User com os dados necessarios
            // agora vamos salvar no banco
            this.repository.save(newUser);

            // depois de salvar no banco, vamos fazer a geração do token
            String token = this.tokenService.generateToken(newUser);
            // vamos retornar os dados que o frontend precisa, que no caso é um token e um nome
            // pra isso criamos uma classe ResponseDTO que recebe um nome e um token
            return ResponseEntity.ok(new ResponseDTO(newUser.getName(), token));
        }

        // caso ja tenha esse usuario criado, vamos retornar um badRequest
        return ResponseEntity.badRequest().build();
    }

}
