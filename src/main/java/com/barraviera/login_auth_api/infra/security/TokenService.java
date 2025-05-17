package com.barraviera.login_auth_api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.barraviera.login_auth_api.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

// anotação para indicar que é um componente do spring e conseguir fazer a injeção de dependencia correta
@Service
public class TokenService {

    // a chave privada ficará no arquivo application.properties
    @Value("${api.security.token.secret}")
    private String secret;

    // metodo de geração do token para quando o usuario estiver fazendo login ou registro
    // vamos receber por parametro um usuario
    public String generateToken(User user) {

        // usaremos um try catch pois pode haver erro ao criar um token
        try {

            // definir o algoritmo de criptografia do token
            // o algoritmo pega uma informação e gerar um hash dela usando uma chave privada
            // e só quem tem essa chave privada consegue descriptografa depois e obter a informação original
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // Vamos gerar o token
            String token = JWT.create()
                    .withIssuer("login-auth-api") // quem está emitindo o token, no caso, a nossa propria api, ou se for um microserviço
                    .withSubject(user.getEmail()) // quem está recebendo este token. Vamos salvar o email do usuario no token
                    .withExpiresAt(this.generateExpirationDate()) // tempo de expiração do token
                    .sign(algorithm); // passamos o algoritmo para de fato gerar o token

            return token;

        }catch (JWTCreationException exception) {
            throw new RuntimeException("Error while authenticating");
        }

    }

    // função de expiração de token
    // tem como retorno um Instant do java.time
    private Instant generateExpirationDate() {
        // pegamos o tempo exato de agora LocalDateTime.now()
        // e vamos adicionar mais 2 horas, entao o token terá validade de 2 horas a partir do now
        // .toInstant para transformar em um instante e vamos passar o nosso fuso de -3
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    // função para validar o token
    // vamos receber por parametro o token do tipo String
    public String validateToken(String token) {

        // vamos fazer um try catch pois pode ser que de erro na validação do token
        // entao o JWT lança uma excessão
        try {
            // se conseguirmos validar o token, retornaremos o email do usuario que está nesse token
            // que salvamos no momento de gerar o token

            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("login-auth-api")
                    .build()
                    .verify(token)
                    .getSubject(); // pegamos email que está no criptografado no token que geramos na função generateToken()

        } catch (JWTVerificationException exception) {
            // JWTVerificationException -> se der erro na verificação do token
            // vamos retornar um nulo
            // e depois nos nossos filter chains (cadeia de filtros) em SecurityFilter.java, vamos verificar, se o token for nulo o usuario nao vai ser autenticado
            return null;
        }

    }


}
