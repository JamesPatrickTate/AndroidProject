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
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;

/**
 * Created by james on 09/02/2018.
 */

public class AveragesDisplayTest {

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
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        testUtility.waiter(2000);
    }

    /**
     * open the averages and confirm to value
     */

    @Test
    public void openAverageTest(){
        onData(anything()).inAdapterView(withContentDescription("AVGlist"))
                .atPosition(0).check(matches(withText("SW|Full :: 5.2 m")));

    }
}

