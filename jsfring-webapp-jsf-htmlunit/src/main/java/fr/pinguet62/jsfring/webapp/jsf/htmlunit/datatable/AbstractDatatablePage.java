package fr.pinguet62.jsfring.webapp.jsf.htmlunit.datatable;

import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableHeaderCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import fr.pinguet62.jsfring.webapp.jsf.htmlunit.AbstractPage;
import fr.pinguet62.jsfring.webapp.jsf.htmlunit.NavigatorException;
import fr.pinguet62.jsfring.webapp.jsf.htmlunit.datatable.popup.CreatePopup;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.pinguet62.jsfring.webapp.jsf.htmlunit.AbstractPage.Delay.MEDIUM;
import static fr.pinguet62.jsfring.webapp.jsf.htmlunit.AbstractPage.Delay.SHORT;
import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toList;

/**
 * @param <T>  The {@link AbstractRow} type.
 * @param <CP> The {@link CreatePopup} type.
 */
public abstract class AbstractDatatablePage<T extends AbstractRow<?, ?>, CP> extends AbstractPage implements Iterable<T> {

    protected AbstractDatatablePage(HtmlPage page) {
        super(page);
    }

    public CP actionCreate() {
        HtmlButton button = getDatatableHeader().getFirstByXPath("./button[contains(@onclick, 'createDialog')]");
        try {
            HtmlPage page = button.click();
            waitJS(MEDIUM);
            debug(page);

            return getPopupCreateFactory().apply(page);
        } catch (IOException e) {
            throw new NavigatorException(e);
        }
    }

    /**
     * @param iconPath The icon file path.<br>
     *                 Example: {@code "/img/foo.png"}.
     * @return The downloaded {@link InputStream}.
     */
    protected InputStream export(String iconPath) {
        HtmlImage icon = getDatatableFooter().getFirstByXPath("./a/img[contains(@src, '" + iconPath + "')]");
        HtmlAnchor link = (HtmlAnchor) icon.getParentNode();
        try {
            return link.click().getWebResponse().getContentAsStream();
        } catch (IOException e) {
            throw new NavigatorException(e);
        }
    }

    /**
     * @return The download {@link InputStream}.
     */
    public InputStream exportCSV() {
        return export("/img/csv.png");
    }

    /**
     * @return The download {@link InputStream}.
     */
    public InputStream exportPDF() {
        return export("/img/pdf.png");
    }

    /**
     * @return The download {@link InputStream}.
     */
    public InputStream exportXLS() {
        return export("/img/xls.png");
    }

    /**
     * @return The download {@link InputStream}.
     */
    public InputStream exportXLSX() {
        return export("/img/xlsx.png");
    }

    /**
     * @return The download {@link InputStream}.
     */
    public InputStream exportXML() {
        return export("/img/xml.png");
    }

    /**
     * Iterate on each {@link HtmlSpan} and stop when {@code class} attribute id {@code "ui-state-active"}.
     * <p>
     * Search paginator by <i>XPath</i>:
     * <ol>
     * <li>Paginator: {@code div} avec une {@code class} de {@code "ui-paginator"}</li>
     * <li>Footer: le 2nd</li>
     * <li>Liste des pages: {@code span} avec une {@code class} de {@code "ui-paginator-pages"}</li>
     * <li>Page active: {@code span} avec une {@code class} de {@code "ui-state-active"}</li>
     * </ol>
     *
     * @return The {@link Iterator} on <i>paginator</i> button of current page.
     */
    protected Iterator<HtmlSpan> getCurrentPage() {
        List<HtmlSpan> paginator = getDatatablePaginator().getByXPath("./span[@class='ui-paginator-pages']/span");
        Iterator<HtmlSpan> it;
        for (it = paginator.iterator(); it.hasNext(); it.next())
            if (it.next().getAttribute("class").contains("ui-state-active"))
                break;
        return it;
    }

    /**
     * <code>&lt;div class="...ui-datatable..."&gt;</code>
     *
     * @return The {@link HtmlDivision}.
     */
    protected HtmlDivision getDatatable() {
        return page.getFirstByXPath("//div[contains(@class, 'ui-datatable')]");
    }

    protected HtmlDivision getDatatableFooter() {
        return getDatatable().getFirstByXPath("./div[contains(@class, 'ui-datatable-footer')]");
    }

    protected HtmlDivision getDatatableHeader() {
        return getDatatable().getFirstByXPath("./div[contains(@class, 'ui-datatable-header')]");
    }

    /**
     * Get 2nd paginator, in footer.
     *
     * @return The {@link HtmlDivision} who contains the paginator buttons.
     */
    protected HtmlDivision getDatatablePaginator() {
        return getDatatable().getFirstByXPath("./div[contains(@class, 'ui-paginator')][2]");
    }

    /**
     * <code>&lt;div class="...ui-datatable-tablewrapper..."&gt;&lt;table&gt;</code>
     *
     * @return The {@link HtmlTable}.
     */
    protected HtmlTable getDatatableTable() {
        return getDatatable().getFirstByXPath("./div[@class='ui-datatable-tablewrapper']/table");
    }

    /**
     * @param title The column title.
     * @return The {@link HtmlTableHeaderCell}.
     */
    protected HtmlTableHeaderCell getDatatableTableHeader(String title) {
        Optional<HtmlTableHeaderCell> find = getDatatableTableHeaders().stream()
                .filter(th -> th.<HtmlSpan>getFirstByXPath("./span[contains(@class, 'ui-column-title')]").getTextContent().equals(title))
                .findAny();
        return find.orElse(null);
    }

    /**
     * <code>&lt;thead&gt;&lt;tr&gt;&lt;th&gt;</code>
     *
     * @return The {@link HtmlTableHeaderCell}s.
     */
    protected List<HtmlTableHeaderCell> getDatatableTableHeaders() {
        return getDatatableTable().getByXPath("./thead/tr/th");
    }

    /**
     * Converter from {@link HtmlPage} to {@link CreatePopup}.
     *
     * @return The {@link Function}.
     */
    protected abstract Function<HtmlPage, CP> getPopupCreateFactory();

    /**
     * Converter from {@link HtmlTableRow} to {@link AbstractRow}.
     *
     * @return The {@link Function}.
     */
    protected abstract Function<HtmlTableRow, T> getRowFactory();

    /**
     * <code>&lt;tbody&gt;&lt;tr&gt;</code>
     *
     * @return The {@link AbstractRow}s.<br>
     * An empty {@link List} if empty datatable.
     */
    public List<T> getRows() {
        List<HtmlTableRow> rows = getDatatableTable().getByXPath("./tbody/tr");
        if (rows.size() == 1 && rows.get(0).getAttribute("class").contains("ui-datatable-empty-message"))
            return new ArrayList<>();
        return rows.stream().map(getRowFactory()).collect(toList());
    }

    /**
     * @return The title on Datatable header.
     */
    public String getTitle() {
        DomText dom = getDatatableHeader().getFirstByXPath("./text()");
        return dom.getTextContent();
    }

    /**
     * Parse the {@code currentPageReportTemplate} attribute value
     *
     * @return The total count of lines.
     */
    public int getTotalCount() {
        HtmlSpan currentPageReportTemplate = getDatatablePaginator().getFirstByXPath("./span[contains(@class, 'ui-paginator-current')]");
        // Parse "x-y of z"
        Pattern pattern = compile(" [0-9]+$");
        Matcher matcher = pattern.matcher(currentPageReportTemplate.getTextContent());
        matcher.find();
        String total = matcher.group().substring(1);
        return parseInt(total);
    }

    /**
     * @throws NavigatorException No next page. See {@link #hasNextPage()}.
     */
    public void gotoNextPage() {
        Iterator<HtmlSpan> current = getCurrentPage();

        if (!current.hasNext())
            throw new NavigatorException("No next page. Test with hasNextPage() before each transition to next page.");

        HtmlSpan next = current.next();
        try {
            page = next.click();
            waitJS(SHORT);
            debug(page);
        } catch (IOException e) {
            throw new NavigatorException(e);
        }
    }

    /**
     * Check than a {@link HtmlSpan} exists after the current page.
     *
     * @return If the current page has next page.
     * @see Iterator#hasNext()
     */
    public boolean hasNextPage() {
        return getCurrentPage().hasNext();
    }

    public boolean isCreateButtonVisible() {
        String xpath = "./button[contains(@onclick, 'createDialog')]";
        List<HtmlButton> buttons = getDatatableHeader().getByXPath(xpath);
        if (buttons.size() >= 2)
            throw new IllegalArgumentException("More than 1 tag found with XPath: " + xpath);
        return !buttons.isEmpty();
    }

    @Override
    public RowIterator<T, CP> iterator() {
        return new RowIterator<>(this);
    }

    /**
     * Find the {@link HtmlTableHeaderCell} from column title, and click to sort results.
     * <p>
     * <code>&lt;span class="...ui-column-title..."&gt;</code>
     *
     * @param title The column title.
     */
    protected void sortBy(String title) {
        HtmlTableHeaderCell header = getDatatableTableHeader(title);
        try {
            page = header.click();
            waitJS(SHORT);
            debug(page);
        } catch (IOException e) {
            throw new NavigatorException(e);
        }
    }

}
