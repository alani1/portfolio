package name.abuchen.portfolio.ui.wizards.splits;

import name.abuchen.portfolio.model.Security;
import name.abuchen.portfolio.model.SecurityPrice;
import name.abuchen.portfolio.model.Values;
import name.abuchen.portfolio.ui.Messages;
import name.abuchen.portfolio.ui.util.SimpleListContentProvider;
import name.abuchen.portfolio.ui.wizards.AbstractWizardPage;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class PreviewQuotesPage extends AbstractWizardPage
{
    private class TransactionLabelProvider extends LabelProvider implements ITableLabelProvider
    {
        public Image getColumnImage(Object element, int columnIndex)
        {
            return null;
        }

        public String getColumnText(Object element, int columnIndex)
        {
            SecurityPrice p = (SecurityPrice) element;
            switch (columnIndex)
            {
                case 0:
                    return Values.Date.format(p.getTime());
                case 1:
                    return Values.Quote.format(p.getValue());
                case 2:
                    if (model.isChangeHistoricalQuotes() && p.getTime().before(model.getExDate()))
                    {
                        long shares = p.getValue() * model.getOldShares() / model.getNewShares();
                        return Values.Quote.format(shares);
                    }
                    return null;
                default:
                    return null;
            }
        }
    }

    private StockSplitModel model;

    private TableViewer tableViewer;

    public PreviewQuotesPage(StockSplitModel model)
    {
        super("preview-prices"); //$NON-NLS-1$

        setTitle(Messages.SplitWizardReviewQuotesTitle);
        setDescription(Messages.SplitWizardReviewQuotesDescription);

        this.model = model;
    }

    @Override
    public void beforePage()
    {
        Security security = model.getSecurity();
        tableViewer.setInput(security.getPrices());
    }

    @Override
    public void createControl(Composite parent)
    {
        Composite container = new Composite(parent, SWT.NONE);
        setControl(container);

        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(container);

        Button checkbox = new Button(container, SWT.CHECK);
        checkbox.setText(Messages.SplitWizardLabelUpdateQuotes);

        Composite tableContainer = new Composite(container, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tableContainer);

        TableColumnLayout layout = new TableColumnLayout();
        tableContainer.setLayout(layout);

        tableViewer = new TableViewer(tableContainer, SWT.BORDER);
        Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableColumn column = new TableColumn(tableViewer.getTable(), SWT.None);
        column.setText(Messages.ColumnDate);
        layout.setColumnData(column, new ColumnPixelData(80, true));

        column = new TableColumn(tableViewer.getTable(), SWT.RIGHT);
        column.setText(Messages.ColumnCurrentQuote);
        layout.setColumnData(column, new ColumnPixelData(80, true));

        column = new TableColumn(tableViewer.getTable(), SWT.RIGHT);
        column.setText(Messages.ColumnUpdatedQuote);
        layout.setColumnData(column, new ColumnPixelData(80, true));

        tableViewer.setLabelProvider(new TransactionLabelProvider());
        tableViewer.setContentProvider(new SimpleListContentProvider());

        // bindings

        DataBindingContext context = new DataBindingContext();

        context.bindValue(SWTObservables.observeSelection(checkbox), //
                        BeansObservables.observeValue(model, "changeHistoricalQuotes")); //$NON-NLS-1$

        checkbox.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                tableViewer.refresh();
            }
        });

    }
}
