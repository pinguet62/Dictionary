package fr.pinguet62.jsfring.webapp.jsf.htmlunit;

import static java.io.File.createTempFile;
import static java.lang.Thread.sleep;
import static java.nio.charset.Charset.defaultCharset;
import static java.util.stream.Collectors.joining;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.springframework.ui.context.Theme;

import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlLink;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptJobManager;

import fr.pinguet62.jsfring.webapp.jsf.htmlunit.field.Field;
import fr.pinguet62.jsfring.webapp.jsf.htmlunit.filter.FilterPathPage;
import fr.pinguet62.jsfring.webapp.jsf.htmlunit.jasperreport.ParametersJasperReportPage;
import fr.pinguet62.jsfring.webapp.jsf.htmlunit.jasperreport.UsersRightsJasperReportPage;
import fr.pinguet62.jsfring.webapp.jsf.htmlunit.profile.ProfilesPage;
import fr.pinguet62.jsfring.webapp.jsf.htmlunit.right.RightsPage;
import fr.pinguet62.jsfring.webapp.jsf.htmlunit.user.UsersPage;
import lombok.extern.slf4j.Slf4j;

/**
 * Base class for all other pages.<br>
 * Provides shared functionalities (like menus) and utilities (like debug and waiting method).
 */
@Slf4j
public class AbstractPage {

    public static enum Delay {

        /** To use for long actions, when there are server treatments. */
        LONG(7_000),
        /** To use for short server treatment. Example: database reading. */
        MEDIUM(4_000),
        /** To use for simple actions, without server treatments. */
        SHORT(2_000);

        private final long ms;

        private Delay(long ms) {
            this.ms = ms;
        }

        public long getMs() {
            return ms;
        }
    }

    private static final File TMP_FILE;

    /** Initialize the {@link #TMP_FILE}. */
    static {
        try {
            TMP_FILE = createTempFile("jsfring-", null);
        } catch (IOException e) {
            throw new NavigatorException(e);
        }
        log.debug("Temporary file: {}", TMP_FILE);
    }

    /**
     * Static version of {@link #debug()} because popup and {@link Field components} behavior.
     *
     * @param page
     *            The {@link SgmlPage} to debug.
     * @see SgmlPage#asXml()
     */
    public static void debug(SgmlPage page) {
        debug(page.asXml());
    }

    /**
     * Executed in {@link Logger#isDebugEnabled() DEBUG} level or less.<br>
     * Write content to {@link #TMP_FILE temporary file}.
     *
     * @param content
     *            The content of file to write.
     */
    public static void debug(String content) {
        if (!log.isDebugEnabled())
            return;

        try {
            IOUtils.write(content, new FileOutputStream(TMP_FILE), defaultCharset());
        } catch (IOException e) {
            throw new NavigatorException(e);
        }
    }

    public static AbstractPage get() {
        return new AbstractPage(null);
    }

    private static String getUrl(String subUrl) {
        if (subUrl == null)
            subUrl = "";
        return "http://localhost:8080" + subUrl;
    }

    protected HtmlPage page;

    protected final WebClient webClient = new WebClient();

    /**
     * Constructor used by classes that inherit.
     *
     * @param page
     *            The {@link HtmlPage HTML page} once on the target page.
     */
    protected AbstractPage(HtmlPage page) {
        this.page = page;
        checkI18n();
    }

    /**
     * Check that there is no missing i18n messages into current page.<br>
     * Check only the current language.<br>
     * A missing i18n message is formatted as {@code "???key???"}.
     *
     * @throws NavigatorException
     *             Missing i18n message.
     */
    private void checkI18n() {
        if (page == null)
            return;
        Collection<String> libelles = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\?\\?\\?[^\\?]+\\?\\?\\?");
        Matcher matcher = pattern.matcher(page.asXml());
        while (matcher.find())
            libelles.add(matcher.group());
        if (!libelles.isEmpty())
            throw new NavigatorException("Missing i18n message: " + libelles.stream().collect(joining(", ")));
    }

    /**
     * Write content of current page.
     * <p>
     * To call after each page action.
     */
    protected void debug() {
        debug(page);
    }

    /**
     * Get the <code>&lt;p:messages/&gt;</code> content.
     *
     * @param xpath
     *            The XPath to find the tag.<br>
     *            Depends on message level.
     * @return The tag content.
     */
    private String getMessage(String xpath) {
        List<HtmlSpan> spans = page.getByXPath(xpath);
        if (spans.isEmpty())
            return null;
        if (spans.size() > 1)
            throw new NavigatorException("More than 1 <p:message> tag found into page.");
        return spans.get(0).getTextContent();
    }

    public String getMessageError() {
        return getMessage("//span[@class='ui-messages-error-summary']");
    }

    public String getMessageInfo() {
        return getMessage("//span[@class='ui-messages-info-summary']");
    }

    public HtmlPage getPage() {
        return page;
    }

    /**
     * Find PrimeFaces {@code <head><link>} tag and parse the {@code href} resource URL.
     *
     * @return The Theme key.
     * @see Theme
     */
    public String getTheme() {
        String resourceUrl = "/javax.faces.resource/theme.css.xhtml?ln=primefaces-";
        HtmlLink link = (HtmlLink) page.getByXPath("//head/link[contains(@href, '" + resourceUrl + "')]").get(0);
        return link.getAttribute("href").substring(resourceUrl.length());
    }

    public ChangePasswordPage gotoChangePasswordPage() {
        return gotoPage("/change-password.xhtml", ChangePasswordPage::new);
    }

    public IndexPage gotoIndex() {
        return gotoPage(null, IndexPage::new);
    }

    public LoginPage gotoLoginPage() {
        return gotoPage("/login.xhtml", LoginPage::new);
    }

    public MyAccountPage gotoMyAccountPage() {
        return gotoPage("/my-profile.xhtml", MyAccountPage::new);
    }

    private <T extends AbstractPage> T gotoPage(String subUrl, Function<HtmlPage, T> factory) {
        try {
            page = webClient.getPage(getUrl(subUrl));
            debug();
            return factory.apply(page);
        } catch (IOException e) {
            throw new NavigatorException("Error going to sub-url: " + subUrl, e);
        }
    }

    public ParametersJasperReportPage gotoParametersJasperReportPage() {
        return gotoPage("/report/parameters.xhtml", ParametersJasperReportPage::new);
    }

    public ProfilesPage gotoProfilesPage() {
        return gotoPage("/profile/list.xhtml", ProfilesPage::new);
    }

    public UsersRightsJasperReportPage gotoReportsUsersRightsPage() {
        return gotoPage("/report/usersRights.xhtml", UsersRightsJasperReportPage::new);
    }

    public RightsPage gotoRightsPage() {
        return gotoPage("/right/list.xhtml", RightsPage::new);
    }

    public FilterPathPage gotoSampleFilterSimple() {
        return gotoPage("/sample/filterPath.xhtml", FilterPathPage::new);
    }

    public AbstractPage gotoUrl(String subUrl) {
        return gotoPage(subUrl, p -> this);
    }

    public UsersPage gotoUsersPage() {
        return gotoPage("/user/list.xhtml?lang=en", UsersPage::new);
    }

    /**
     * Wait end of JavaScript (and Ajax) actions.<br>
     * Continue after the delay.
     *
     * @param delay
     *            The {@link Delay} to wait.
     */
    public void waitJS(Delay delay) {
        log.debug("Wait JavaScript");
        final int period = 200 /* ms */;
        JavaScriptJobManager manager = page.getEnclosingWindow().getJobManager();
        for (int t = 0; manager.getJobCount() > 0 && t < delay.getMs(); t += period)
            try {
                log.trace("Wait " + t + "ms");
                sleep(period);
            } catch (InterruptedException e) {
                throw new NavigatorException(e);
            }
    }

}