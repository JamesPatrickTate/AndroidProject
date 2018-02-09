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
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.collection.IsMapContaining.hasEntry;

/**
 * Created by james on 08/02/2018.
 */

public class NewShotTest{

    String correctEmailAddress = "jamestate11@gmail.com";
    String correctPassword = "Keelagh46=";
    TestUtility testUtility = new TestUtility();




    @Rule //login
    public ActivityTestRule<Auth> authTestRule = new  ActivityTestRule<Auth>(Auth.class);


    @Before
    public void beforeTest() {
         authTestRule.getActivity();
        onView(withId(R.id.field_email)).perform(typeText(correctEmailAddress), closeSoftKeyboard());
        onView(withId(R.id.field_password)).perform(typeText(correctPassword), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        testUtility.waiter(5000);// wait for login
        onView(withId(R.id.newShot)).perform(click());
        testUtility.waiter(2000);
    }

    @Test
    public void clubSpinnerTest() {
        onView(withId(R.id.clubSelector)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("D"))).perform(click());
        onView(withId(R.id.clubSelector)).check(matches(withSpinnerText(containsString("D"))));
    }

    @Test
    public void swingLengthSpinnerTest() {
        onView(withId(R.id.shotSelector)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("1/2"))).perform(click());
        onView(withId(R.id.shotSelector)).check(matches(withSpinnerText(containsString("1/2"))));
    }
}
