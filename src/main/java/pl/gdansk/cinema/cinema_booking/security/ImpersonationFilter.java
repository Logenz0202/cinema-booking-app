package pl.gdansk.cinema.cinema_booking.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImpersonationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
                authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            
            String impersonateUser = request.getHeader("X-Impersonate-User");
            if (impersonateUser != null && !impersonateUser.isEmpty()) {
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(impersonateUser);
                    UsernamePasswordAuthenticationToken impersonatedAuth = 
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(impersonatedAuth);
                    log.info("Admin {} is impersonating user {}", authentication.getName(), impersonateUser);
                } catch (Exception e) {
                    log.warn("Failed to impersonate user {}: {}", impersonateUser, e.getMessage());
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
