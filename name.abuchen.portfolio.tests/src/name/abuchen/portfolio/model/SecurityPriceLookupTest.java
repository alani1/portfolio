package name.abuchen.portfolio.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import name.abuchen.portfolio.SecurityBuilder;
import name.abuchen.portfolio.util.Dates;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("nls")
public class SecurityPriceLookupTest
{
    private Security security;

    @Before
    public void setup()
    {
        security = new SecurityBuilder() //
                        .addPrice("2014-11-01", 1) //
                        .addPrice("2014-11-02", 2) //
                        .addPrice("2014-11-03", 3) //
                        .get();
    }

    @Test
    public void testLookupOfSecurityPrice()
    {
        security.setLatest(new LatestSecurityPrice(Dates.date("2014-11-05"), 5));

        assertThat(security.getSecurityPrice(Dates.date("2014-10-31")).getValue(), is(1L));
        assertThat(security.getSecurityPrice(Dates.date("2014-11-01")).getValue(), is(1L));
        assertThat(security.getSecurityPrice(Dates.date("2014-11-02")).getValue(), is(2L));
        assertThat(security.getSecurityPrice(Dates.date("2014-11-03")).getValue(), is(3L));
        assertThat(security.getSecurityPrice(Dates.date("2014-11-04")).getValue(), is(3L));
        assertThat(security.getSecurityPrice(Dates.date("2014-11-05")).getValue(), is(5L));
        assertThat(security.getSecurityPrice(Dates.date("2014-11-06")).getValue(), is(5L));
    }

    @Test
    public void testLookupIfHistoricQuotesContainGaps()
    {
        security.removePrice(security.getSecurityPrice(Dates.date("2014-11-02")));

        assertThat(security.getSecurityPrice(Dates.date("2014-10-31")).getValue(), is(1L));
        assertThat(security.getSecurityPrice(Dates.date("2014-11-01")).getValue(), is(1L));
        assertThat(security.getSecurityPrice(Dates.date("2014-11-02")).getValue(), is(1L));
        assertThat(security.getSecurityPrice(Dates.date("2014-11-03")).getValue(), is(3L));
        assertThat(security.getSecurityPrice(Dates.date("2014-11-04")).getValue(), is(3L));
    }

    @Test
    public void preferLatestOverHistoricPrice()
    {
        security.setLatest(new LatestSecurityPrice(Dates.date("2014-11-03"), 5));

        assertThat(security.getSecurityPrice(Dates.date("2014-11-02")).getValue(), is(2L));
        assertThat(security.getSecurityPrice(Dates.date("2014-11-03")).getValue(), is(5L));
        assertThat(security.getSecurityPrice(Dates.date("2014-11-04")).getValue(), is(5L));
    }

    @Test
    public void preferHistoricOverLatestIfLatestIsTooOld()
    {
        security.setLatest(new LatestSecurityPrice(Dates.date("2014-11-02"), 5));

        assertThat(security.getSecurityPrice(Dates.date("2014-11-01")).getValue(), is(1L));
        assertThat(security.getSecurityPrice(Dates.date("2014-11-02")).getValue(), is(2L));
        assertThat(security.getSecurityPrice(Dates.date("2014-11-03")).getValue(), is(3L));
        assertThat(security.getSecurityPrice(Dates.date("2014-11-04")).getValue(), is(3L));
    }

    @Test
    public void preferHistoricOverLatestIfLatestIsTooOldDespiteGaps()
    {
        security.addPrice(new SecurityPrice(Dates.date("2014-11-05"), 10));
        security.setLatest(new LatestSecurityPrice(Dates.date("2014-11-03"), 5));

        assertThat(security.getSecurityPrice(Dates.date("2014-11-02")).getValue(), is(2L));
        assertThat(security.getSecurityPrice(Dates.date("2014-11-03")).getValue(), is(3L));
        assertThat(security.getSecurityPrice(Dates.date("2014-11-04")).getValue(), is(3L));
        assertThat(security.getSecurityPrice(Dates.date("2014-11-05")).getValue(), is(10L));
    }

    @Test
    public void testZeroQuoteIfNoQuotesExist()
    {
        security.removeAllPrices();
        assertThat(security.getSecurityPrice(Dates.date("2014-11-01")).getValue(), is(0L));
    }

    @Test
    public void testIfNoHistoricQuotesExist()
    {
        security.removeAllPrices();

        security.setLatest(new LatestSecurityPrice(Dates.date("2014-11-02"), 5));

        assertThat(security.getSecurityPrice(Dates.date("2014-11-01")).getValue(), is(5L));
        assertThat(security.getSecurityPrice(Dates.date("2014-11-02")).getValue(), is(5L));
        assertThat(security.getSecurityPrice(Dates.date("2014-11-03")).getValue(), is(5L));
    }

}
