package fr.pinguet62.jsfring.webapp.jsf.i18n;

import fr.pinguet62.jsfring.SpringBootConfig;
import fr.pinguet62.jsfring.webapp.jsf.htmlunit.AbstractPage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static fr.pinguet62.jsfring.webapp.jsf.htmlunit.AbstractPage.get;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootConfig.class, webEnvironment = DEFINED_PORT)
public class LangFilterITTest {

    @Autowired
    private LangBean langBean;

    private AbstractPage navigator;

    @Before
    public void before() {
        navigator = get();
    }

    @Test
    public void test_getParameter() {
        String initialLangKey = navigator.gotoMyAccountPage().getLangSwitcher().getValue();
        String initialTitle = navigator.gotoRightsPage().getTitle();

        String newLangKey = langBean.getSupportedLocales().stream().filter(loc -> !loc.getLanguage().equals(initialLangKey))
                .findAny().get().getLanguage();
        String subUrl = String.format("/index.xhtml?%s=%s", LangFilter.PARAMETER, newLangKey);
        navigator.gotoUrl(subUrl);

        String newTitle = navigator.gotoRightsPage().getTitle();
        assertThat(newTitle, is(not(equalTo(initialTitle))));
    }

}