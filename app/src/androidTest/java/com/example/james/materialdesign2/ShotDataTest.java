package com.example.james.materialdesign2;

import android.app.Activity;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

/**
 * Created by james on 09/02/2018.
 * Testing the activity which displays the map and shot data for a single shot
 * TODO the xml for this is called tester, this name should be changed to something more relevant
 */

public class ShotDataTest {
    //mmap  object_tostring

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
        onView(withId(R.id.drawerList))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        testUtility.waiter(2000);
    }

    /**
     * open the shots stats from the nav drawer
     * check the map and the details of the shot are displayed
     */

    @Test
    public void openSingleShotDataTest(){
        onData(anything()).inAdapterView(withContentDescription("alllShotsList"))
                .atPosition(0).perform(click());
        testUtility.waiter(2000);
        onView(withId(R.id.mmap)).check(matches(isDisplayed()));
        onView(withId(R.id.object_tostring)).check(matches(isDisplayed()));
    }
}
