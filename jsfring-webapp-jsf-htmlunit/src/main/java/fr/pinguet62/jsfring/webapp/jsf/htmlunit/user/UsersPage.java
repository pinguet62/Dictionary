package fr.pinguet62.jsfring.webapp.jsf.htmlunit.user;

import com.gargoylesoftware.htmlunit.html.*;
import fr.pinguet62.jsfring.webapp.jsf.htmlunit.NavigatorException;
import fr.pinguet62.jsfring.webapp.jsf.htmlunit.datatable.AbstractDatatablePage;
import fr.pinguet62.jsfring.webapp.jsf.htmlunit.field.SelectOneButton;
import fr.pinguet62.jsfring.webapp.jsf.htmlunit.user.popup.UserCreatePopup;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import static fr.pinguet62.jsfring.webapp.jsf.htmlunit.AbstractPage.Delay.MEDIUM;
import static fr.pinguet62.jsfring.webapp.jsf.htmlunit.AbstractPage.Delay.SHORT;
import static fr.pinguet62.jsfring.webapp.jsf.htmlunit.user.UsersPage.Column.ACTIVE;
import static fr.pinguet62.jsfring.webapp.jsf.htmlunit.user.UsersPage.Column.EMAIL;

public final class UsersPage extends AbstractDatatablePage<UserRow, UserCreatePopup> {

    public static enum ActiveFilter {
        All, No, Yes;
    }

    public static enum Column {

        ACTIONS(4, "Actions"), ACTIVE(2, "Active"), EMAIL(1, "Email"), LAST_CONNECTION(3, "Last connection");

        public static Column fromTitle(String title) {
            for (Column column : values())
                if (title.equals(column.title))
                    return column;
            throw new IllegalArgumentException("Unknown title: " + title);
        }

        private final int index;

        private final String title;

        private Column(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public int getIndex() {
            return index;
        }

        public String getTitle() {
            return title;
        }

    }

    public UsersPage(HtmlPage page) {
        super(page);
    }

    /**
     * Test if the {@link Column} is visible.<br>
     * Check that {@link HtmlTableHeaderCell table header} {@code class} contains {@code "ui-helper-hidden"} value.
     *
     * @param column The {@link Column}.
     * @return The result.
     */
    public boolean columnVisibile(Column column) {
        HtmlTableHeaderCell th = getDatatableTableHeader(column.getTitle());
        return !th.getAttribute("class").contains("ui-helper-hidden");
    }

    public void filterByActive(ActiveFilter value) {
        // TODO common abstract parent method
        HtmlDivision div = (HtmlDivision) getDatatableTableHeader(ACTIVE.getTitle())
                .getByXPath("./div[contains(@class, 'ui-column-customfilter')]/div[contains(@class, 'ui-selectonebutton')]")
                .get(0);
        SelectOneButton<ActiveFilter> selectOneButton = new SelectOneButton<ActiveFilter>(div, ActiveFilter::valueOf);
        selectOneButton.setValue(value);
    }

    /**
     * @param value The value to set.<br>
     *              {@code null} to reset filter.
     */
    public void filterEmail(String value) {
        HtmlInput input = (HtmlInput) getDatatableTableHeader(EMAIL.getTitle())
                .getByXPath("./input[contains(@class, 'ui-column-filter')]").get(0);
        try {
            input.type(value);
            waitJS(MEDIUM);
            debug();
        } catch (IOException e) {
            throw new NavigatorException(e);
        }
    }

    @Override
    protected Function<HtmlPage, UserCreatePopup> getPopupCreateFactory() {
        return UserCreatePopup::new;
    }

    @Override
    protected Function<HtmlTableRow, UserRow> getRowFactory() {
        return UserRow::new;
    }

    // TODO test
    public void hideOrShowColumn(Column column) {
        try {
            HtmlButton toggler = (HtmlButton) getDatatableHeader().getByXPath("./button[contains(@id, 'toggler')]").get(0);

            // Show Toogler
            page = toggler.click();
            waitJS(SHORT);
            debug();

            List<HtmlListItem> choices = page.getByXPath(
                    "//div[contains(@class, 'ui-columntoggler')]/ul[contains(@class, 'ui-columntoggler-items')]/li[contains(@class, 'ui-columntoggler-item')]");
            HtmlListItem choice = choices.stream()
                    .filter(li -> Column.fromTitle(((HtmlLabel) li.getByXPath("./label").get(0)).asText()).equals(column))
                    .findAny().get();
            choice.click();
            page = ((HtmlDivision) choice.getByXPath("./div[contains(@class, 'ui-chkbox')]").get(0)).click();
            waitJS(SHORT);
            debug();

            // Hide Toogler
            page = toggler.click();
            waitJS(SHORT);
            debug();
        } catch (IOException e) {
            throw new NavigatorException(e);
        }
    }

    /**
     * Find the {@link HtmlTableHeaderCell} from column title, and click to sort results.
     * <p>
     * <code>&lt;span class="...ui-column-title..."&gt;</code>
     *
     * @param column The {@link Column}.
     */
    public void sortBy(Column column) {
        sortBy(column.getTitle());
    }

}