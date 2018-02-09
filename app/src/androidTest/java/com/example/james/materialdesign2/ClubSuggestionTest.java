package com.example.james.materialdesign2;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.collection.IsMapContaining.hasEntry;

/**
 * Created by james on 08/02/2018.
 */

public class ClubSuggestionTest {

    String correctEmailAddress = "jamestate11@gmail.com";
    String correctPassword = "Keelagh46=";
    TestUtility testUtility = new TestUtility();




    @Rule //login
    public ActivityTestRule<Auth> authTestRule = new  ActivityTestRule<Auth>(Auth.class);
    public AuthenticationTest login = new AuthenticationTest();
//    @Rule //club sugestion
//    public ActivityTestRule<ClubSuggestion> clubSuggestionActivityTestRule =
//            new  ActivityTestRule<ClubSuggestion>(ClubSuggestion.class);
//    @Rule
//    public ActivityTestRule<MainActivity>mainActivityActivityTestRule =
//            new  ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void beforeTest() {
        Activity activity = authTestRule.getActivity();
        onView(withId(R.id.field_email)).perform(typeText(correctEmailAddress), closeSoftKeyboard());
        onView(withId(R.id.field_password)).perform(typeText(correctPassword), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        testUtility.waiter(5000);// wait for login
        onView(withId(R.id.stats)).perform(click());
        testUtility.waiter(2000);


    }

    @Test
    public void openClubSuggestionsTest() {
        // "6I|Full::17.85m"
        onView(withId(R.id.inputSuggestion)).perform(typeText("18"), closeSoftKeyboard());
        onView(withId(R.id.getSuggestion)).perform(click());
        testUtility.waiter(5000);

        onData(anything()).inAdapterView(withContentDescription("Possible Shots")).atPosition(0);


    }
}
