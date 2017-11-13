package fr.pinguet62.jsfring.webservice;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fr.pinguet62.jsfring.common.security.userdetails.UserDetailsUtils.getCurrent;
import static fr.pinguet62.jsfring.webservice.OAuthWebservice.PATH;
import static java.util.stream.Collectors.toList;

/**
 * Webservice for OAuth additional utilities.
 */
@RestController
@RequestMapping(PATH)
public class OAuthWebservice {

    /**
     * @see OAuthWebservice#getAutorities()
     */
    public static final String AUTORITIES_PATH = "/autorities";

    public static final String PATH = "/oauth";

    /**
     * Get the {@link GrantedAuthority#getAuthority() authoritie}s of current connected user.
     *
     * @return The {@link GrantedAuthority#getAuthority() authoritie}s.
     */
    @GetMapping(AUTORITIES_PATH)
    public List<String> getAutorities() {
        return getCurrent().getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList());
    }

}