package com.mickael.go4lunch;

import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.data.model.placesapi.common.Photo;
import com.mickael.go4lunch.data.repository.AppRepository;
import com.mickael.go4lunch.ui.restaurantdetails.RestaurantDetailsViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(JUnit4.class)
public class RestaurantDetailsViewModelUnitTest {

    private RestaurantDetailsViewModel restaurantDetailsViewModel;

    @Mock
    AppRepository appRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.restaurantDetailsViewModel = new RestaurantDetailsViewModel(this.appRepository);
    }

    @Test
    public void updateRestaurantRatingTest() {
        float expectedResult = 3f;
        float result = this.restaurantDetailsViewModel.updateRestaurantRating("5");
        assertEquals(expectedResult, result, 0.001);
    }

    @Test
    public void getRestaurantPhotoUrlTest() {
        Restaurant restaurant = new Restaurant();
        List<Photo> photos = new ArrayList<>();
        photos.add(new Photo("reference"));
        restaurant.setPhotos(photos);

        String result = this.restaurantDetailsViewModel.getRestaurantPhotoUrl(restaurant);

        String expectedResult = "https://maps.googleapis.com/maps/api/place/photo?maxheight=1600&photoreference=reference&key=" + BuildConfig.GOOGLE_WEB_API_KEY;

        assertEquals(expectedResult, result);
    }
}