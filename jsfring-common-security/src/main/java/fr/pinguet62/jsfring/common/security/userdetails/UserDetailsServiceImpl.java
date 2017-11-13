package fr.pinguet62.jsfring.common.security.userdetails;

import fr.pinguet62.jsfring.dao.sql.UserDao;
import fr.pinguet62.jsfring.model.sql.User;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of {@link UserDetailsService}.<br>
 * <ol>
 * <li>User fill <i>username</i> and <i>password</i></li>
 * <li>{@link DaoAuthenticationProvider} request {@link UserDetails} to {@link UserDetailsService}</li>
 * <li>{@link UserDetailsService} retrieve user's informations in database, from its <i>username</i></li>
 * <li>{@link DaoAuthenticationProvider} check user's <i>password</i> from the {@link UserDetails#getPassword() database
 * password} and other informations</li>
 * </ol>
 *
 * @see AuthenticationProvider
 * @see UserDetailsService
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDao userDao;

    public UserDetailsServiceImpl(UserDao userDao) {
        this.userDao = requireNonNull(userDao);
    }

    /**
     * The login process.<br>
     * Reset {@link User#lastConnection} after connection.
     *
     * @param id The {@link User#email user's login}.
     * @return The {@link UserDetails}.
     * @throws UsernameNotFoundException If {@link User} is not found.
     * @see UserDetailsImpl
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Optional<User> userOp = userDao.findById(id);
        User user = userOp.orElseThrow(() -> new UsernameNotFoundException("No user with username: " + id));

        userDao.resetLastConnectionDate(user);

        return new UserDetailsImpl(user);
    }

}