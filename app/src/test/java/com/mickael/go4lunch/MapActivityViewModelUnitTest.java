package com.mickael.go4lunch;

import com.mickael.go4lunch.data.repository.AppRepository;
import com.mickael.go4lunch.ui.map.activity.MapActivityViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(JUnit4.class)
public class MapActivityViewModelUnitTest {

    private MapActivityViewModel mapActivityViewModel;

    @Mock
    AppRepository appRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mapActivityViewModel = new MapActivityViewModel(this.appRepository);
    }

    @Test
    public void getTimeForNotificationsTest() {
        Calendar calendar = this.mapActivityViewModel.getTimeForNotifications();
        Assert.assertEquals(12, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(0, calendar.get(Calendar.SECOND));
        Assert.assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }
}