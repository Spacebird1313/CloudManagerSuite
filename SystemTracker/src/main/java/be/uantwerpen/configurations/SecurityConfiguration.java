package be.uantwerpen.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by Thomas on 13/02/2016.
 */
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.authorizeRequests().antMatchers("/systemLoad")
                                    .permitAll();

        http.authorizeRequests().antMatchers("/")
                                    .permitAll()
                                    .anyRequest()
                                    .fullyAuthenticated()
                                    .and()
                                .exceptionHandling()
                                    .accessDeniedPage("/access?accessdenied");

        http.csrf().disable();
        http.headers().frameOptions().disable();
    }
}
