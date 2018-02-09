package com.example.james.materialdesign2;

import android.app.Activity;
import android.content.ComponentName;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
//import static android.support.test.espresso.intent.Intents.intended;
//import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.core.StringStartsWith.startsWith;


/**
 * Created by james on 09/02/2018.
 * ViewAction.swipeRight();
 */

public class NavDrawerTest {

    String correctEmailAddress = "jamestate11@gmail.com";
    String correctPassword = "Keelagh46=";
    TestUtility testUtility = new TestUtility();




    @Rule //login
    public ActivityTestRule<Auth> authTestRule = new  ActivityTestRule<Auth>(Auth.class);
    public AuthenticationTest login = new AuthenticationTest();

    @Before
    public void beforeTest() {
        Activity activity = authTestRule.getActivity();
        onView(withId(R.id.field_email)).perform(typeText(correctEmailAddress), closeSoftKeyboard());
        onView(withId(R.id.field_password)).perform(typeText(correctPassword), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        testUtility.waiter(5000);// wait for login
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        testUtility.waiter(2000);
    }

    @Test
    public void shotStatsOpenTest(){
        onView(withId(R.id.drawerList))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        testUtility.waiter(2000);
        onView(withId(R.id.shots_heading)).check(matches(isDisplayed()));

    }
    @Test
    public void shotsAverageOpenTest(){
        onView(withId(R.id.drawerList))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        testUtility.waiter(2000);
        onView(withId(R.id.averages_heading)).check(matches(isDisplayed()));
    }


}
