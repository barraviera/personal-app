package com.barraviera.login_auth_api.infra.security;

import com.barraviera.login_auth_api.domain.user.User;
import com.barraviera.login_auth_api.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

// O SecurityFilter é meio que padrao para os projetos usando spring security

// Colocamos a anotação @Component para que o spring consiga enxergar esta classe
// e esta classe vai estender o OncePerRequestFilter, que é um filtro que vai executar uma vez pra cada request que chegar na nossa api
// ou seja, antes da requisição chegar nos controllers, ele bate no filtro do spring security pra ver se este usuario que fez a requisição
// pode fazer esse request ou nao

// Esta nossa classe em especifico fará a verificação se o token que o usuario mandou junto da requisição é válido, ou seja, se é um token que a nossa
// aplicação emitiu, se for válido ele vai salvar no contexto da autenticação quem é esse usuario que está fazendo a requisição e, entao
// podemos usar em outros componentes da nossa aplicação para recuperar essas informações
@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;
    @Autowired
    UserRepository userRepository;

    // Metodo que será o filtro
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Chamamos a funcao recoverToken pra obter somente o token
        var token = this.recoverToken(request);
        // E chamamos a funcao tokenService do TokenService.java para validar este token
        var login = tokenService.validateToken(token);

        // Se for diferente de nulo... Obs. lembra que no metodo validateToken do TokenService.java se o token nao for válido iremos retornar nulo
        if(login != null){
            // Busca o usuario no banco de dados
            // .orElseThrow(() caso nao encontre o usuario lançamos uma excessao RuntimeException("User Not Found")
            User user = userRepository.findByEmail(login).orElseThrow(() -> new RuntimeException("User Not Found"));
            // Assim que encontrar o usuario, criamos uma coleção de roles, que no nosso caso será somente role de usuario normal ROLE_USER
            // pois nessa aplicação nao vamos ter ROLE_ADMIN nem outro tipo de permissao
            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            // Criamos o objeto de autenticacao contendo o usuario e sua role (user, null, authorities);
            var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
            // e criando este objeto do tipo UsernamePasswordAuthenticationToken nós vamos setar no SecurityContextHolder que é o contexto de segurança do spring security
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    // Metodo auxiliar que vai receber o request que veio do usuario, e vai pegar o Header Authorization deste request
    // que onde estará o token
    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization"); // pega o Header Authorization
        if(authHeader == null) return null; // se nao tiver nada no Header Authorization vamos retornar um nulo

        // senao vamos retornar o Header Authorization mas substituindo o Bearer por vazio
        // pois ele vem da seguinte forma   Authorization: Bearer tokennnnnnnnnnnnnnnnnnnnnnn
        // entao removemos o Bearer para ficar somente com o valor do token
        return authHeader.replace("Bearer ", "");
    }

}
