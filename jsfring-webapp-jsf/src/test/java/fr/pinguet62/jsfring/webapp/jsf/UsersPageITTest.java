package fr.pinguet62.jsfring.webapp.jsf;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fr.pinguet62.jsfring.SpringBootConfig;
import fr.pinguet62.jsfring.dao.sql.ProfileDao;
import fr.pinguet62.jsfring.dao.sql.UserDao;
import fr.pinguet62.jsfring.model.sql.Profile;
import fr.pinguet62.jsfring.model.sql.User;
import fr.pinguet62.jsfring.webapp.jsf.htmlunit.user.UserRow;
import fr.pinguet62.jsfring.webapp.jsf.htmlunit.user.UsersPage;
import fr.pinguet62.jsfring.webapp.jsf.htmlunit.user.popup.UserCreatePopup;
import fr.pinguet62.jsfring.webapp.jsf.htmlunit.user.popup.UserShowPopup;
import fr.pinguet62.jsfring.webapp.jsf.htmlunit.user.popup.UserUpdatePopup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.List;
import java.util.Random;

import static fr.pinguet62.jsfring.test.DbUnitConfig.DATASET;
import static fr.pinguet62.jsfring.util.MatcherUtils.equalWithoutOrderTo;
import static fr.pinguet62.jsfring.webapp.jsf.htmlunit.AbstractPage.get;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

/**
 * @see UsersPage
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootConfig.class, webEnvironment = DEFINED_PORT)
// DbUnit
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
@DatabaseSetup(DATASET)
public class UsersPageITTest {

    private UsersPage page;

    @Autowired
    private ProfileDao profileDao;

    @Autowired
    private UserDao userDao;

    @Before
    public void before() {
        page = get().gotoUsersPage();
    }

    @Test
    public void test_action_create() {
        // Data
        String email = randomUUID().toString().substring(0, 10) + "@email.fr";
        boolean active = new Random().nextBoolean();
        List<Profile> profiles = profileDao.findAll().stream().limit(2).collect(toList());

        // Fill fields
        UserCreatePopup popup = page.actionCreate();
        popup.getEmail().setValue(email);
        popup.getActive().setValue(active);
        popup.getProfiles().setValue(profiles.stream().map(Profile::getTitle).collect(toList()));
        popup.submit();

        // Check
        User user = userDao.findById(email).get();
        assertThat(user, is(not(nullValue())));
        assertThat(user.getEmail(), is(equalTo(email)));
        assertThat(user.getActive(), is(equalTo(active)));
        assertThat(user.getProfiles(), is(equalWithoutOrderTo(profiles)));
    }

    @Test
    public void test_action_create_field_readonly() {
        UserCreatePopup popup = page.actionCreate();

        assertThat(popup.getEmail().isReadonly(), is(false));
        assertThat(popup.getActive().isReadonly(), is(false));
        assertThat(popup.getProfiles().isReadonly(), is(false));
    }

    @Test
    public void test_action_rendered() {
        assertThat(page.isCreateButtonVisible(), is(true));
        for (UserRow row : page.getRows()) {
            assertThat(row.isActionButtonShowVisible(), is(true));
            assertThat(row.isActionButtonUpdateVisible(), is(true));
            assertThat(row.isActionButtonDeleteVisible(), is(true));
        }
    }

    @Test
    public void test_action_show_field_readonly() {
        UserShowPopup popup = page.iterator().next().actionShow();
        assertThat(popup.getEmail().isReadonly(), is(true));
        assertThat(popup.isActive().isReadonly(), is(true));
        assertThat(popup.getLastConnection().isReadonly(), is(true));
        assertThat(popup.getProfiles().isReadonly(), is(true));
    }

    @Test
    public void test_action_show_field_value() {
        UserRow row = page.iterator().next();
        UserShowPopup popup = row.actionShow();

        User user = userDao.findById(row.getEmail()).get();
        assertThat(popup.getEmail().getValue(), is(equalTo(user.getEmail())));
        assertThat(popup.isActive().getValue(), is(equalTo(user.getActive())));
        if (user.getLastConnection() == null)
            assertThat(popup.getLastConnection().getValue(), is(nullValue()));
        else
            assertThat(popup.getLastConnection().getValue(), is(equalTo(user.getLastConnection())));
        assertThat(popup.getProfiles().getValue(),
                is(equalWithoutOrderTo(user.getProfiles().stream().map(Profile::getTitle).collect(toList()))));
    }

    @Test
    public void test_action_update() {
        UserRow row = page.iterator().next();
        UserUpdatePopup popup = row.actionUpdate();

        // Data
        String email = row.getEmail();
        boolean active = !row.isActive();
        List<Profile> profiles = profileDao.findAll().stream().limit(2).collect(toList());

        // Fill fields
        popup.getActive().setValue(active);
        popup.getProfiles().setValue(profiles.stream().map(Profile::getTitle).collect(toList()));
        popup.submit();

        // Check
        User user = userDao.findById(email).get();
        assertThat(user.getEmail(), is(equalTo(email)));
        assertThat(user.getActive(), is(equalTo(active)));
        assertThat(user.getProfiles(), is(equalWithoutOrderTo(profiles)));
    }

    @Test
    public void test_action_update_field_readonly() {
        UserUpdatePopup popup = page.iterator().next().actionUpdate();

        assertThat(popup.getEmail().isReadonly(), is(true));
        assertThat(popup.getActive().isReadonly(), is(false));
        assertThat(popup.getLastConnection().isReadonly(), is(true));
        assertThat(popup.getProfiles().isReadonly(), is(false));
    }

    @Test
    public void test_action_update_field_value() {
        UserRow row = page.iterator().next();
        UserUpdatePopup popup = row.actionUpdate();

        User user = userDao.findById(row.getEmail()).get();
        assertThat(popup.getEmail().getValue(), is(equalTo(user.getEmail())));
        assertThat(popup.getActive().getValue(), is(equalTo(user.getActive())));
        if (user.getLastConnection() == null)
            assertThat(popup.getLastConnection().getValue(), is(nullValue()));
        else
            assertThat(popup.getLastConnection().getValue(), is(equalTo(user.getLastConnection())));
        assertThat(popup.getProfiles().getValue(),
                is(equalWithoutOrderTo(user.getProfiles().stream().map(Profile::getTitle).collect(toList()))));
    }

}