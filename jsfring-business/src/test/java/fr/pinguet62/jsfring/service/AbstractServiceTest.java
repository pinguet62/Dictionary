package fr.pinguet62.jsfring.service;

import static fr.pinguet62.jsfring.test.DbUnitConfig.DATASET;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.Serializable;
import java.util.Random;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import fr.pinguet62.jsfring.SpringBootConfig;
import fr.pinguet62.jsfring.model.sql.Profile;
import fr.pinguet62.jsfring.model.sql.User;
import fr.pinguet62.jsfring.util.PasswordGenerator;

/** @see AbstractService */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = SpringBootConfig.class)
@DatabaseSetup(DATASET)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class AbstractServiceTest {

    @Inject
    private ProfileService profileService;

    @Inject
    private RightService rightService;

    @Inject
    private UserService userService;

    /** @see AbstractService#create(Serializable) */
    @Test
    public void test_create() {
        {
            int initialCount = profileService.getAll().size();
            profileService.create(new Profile("new profile"));
            assertThat(profileService.getAll(), hasSize(initialCount + 1));
        }
        {
            int initialCount = userService.getAll().size();
            userService.create(
                    new User("new login", new PasswordGenerator().get(), "foo@hostname.domain", new Random().nextBoolean()));
            assertThat(userService.getAll(), hasSize(initialCount + 1));
        }
    }

    /** @see AbstractService#delete(Serializable) */
    @Test
    public void test_delete() {
        profileService.delete(profileService.get(1));
        assertThat(profileService.getAll(), hasSize(1));

        profileService.delete(profileService.get(2));
        assertThat(profileService.getAll(), hasSize(0));
    }

    /** @see AbstractService#get(Serializable) */
    @Test
    public void test_get() {
        {
            assertThat(rightService.get("RIGHT_RO").getTitle(), is(equalTo("Affichage des droits")));
            assertThat(rightService.get("PROFILE_RO").getTitle(), is(equalTo("Affichage des profils")));
        }
        {
            assertThat(profileService.get(1).getTitle(), is(equalTo("Profile admin")));
            assertThat(profileService.get(2).getTitle(), is(equalTo("User admin")));
        }
        {
            assertThat(userService.get("super admin").getPassword(), is(equalTo("Azerty1!")));
            assertThat(userService.get("admin profile").getEmail(), is(equalTo("admin_profile@domain.fr")));
        }
    }

    /** @see AbstractDao#getAll() */
    @Test
    public void test_getAll() {
        assertThat(rightService.getAll(), hasSize(5));
        assertThat(profileService.getAll(), hasSize(2));
        assertThat(userService.getAll(), hasSize(3));
    }

    /** @see AbstractService#update(Object) */
    @Test
    public void test_update() {
        int id = 1;
        String newTitle = "new title";

        // Before
        Profile profile = profileService.get(id);
        assertThat(profile.getTitle(), is(not(equalTo(newTitle))));

        profile.setTitle(newTitle);
        profileService.update(profile);

        // Test
        assertThat(profileService.get(id).getTitle(), is(equalTo(newTitle)));
    }

}