package com.ecommerce.product.security;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends GenericFilter {

    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(
            ServletRequest req,
            ServletResponse res,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            String userId = jwtUtil.getUserId(token);
            String role = jwtUtil.getRole(token);

            Authentication auth =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of(new SimpleGrantedAuthority(role))
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(auth);
        }

        chain.doFilter(req, res);
    }
}
//@Component
//@RequiredArgsConstructor
//public class JwtAuthFilter extends GenericFilter {
//
//    private final JwtUtil jwtUtil;
//
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest request = (HttpServletRequest) req;
//        String header = request.getHeader("Authorization");
//
//        if (header != null && header.startsWith("Bearer ")) {
//            String token = header.substring(7);
//            String role = jwtUtil.getRole(token);
//
//            Authentication auth = new UsernamePasswordAuthenticationToken(
//                    jwtUtil.getUserId(token),
//                    null,
//                    List.of(new SimpleGrantedAuthority(role))
//            );
//
//            SecurityContextHolder.getContext().setAuthentication(auth);
//        }
//
//        chain.doFilter(req, res);
//    }
//}
