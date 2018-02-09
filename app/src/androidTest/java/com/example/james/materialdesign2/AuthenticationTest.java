package com.example.james.materialdesign2;

import android.app.Activity;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.Espresso.onView;
import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

/**
 * Created by james on 08/02/2018.
 */

@RunWith(AndroidJUnit4.class)

public class AuthenticationTest {
    String correctEmailAddress = "jamestate11@gmail.com";
    String correctPassword = "Keelagh46=";
    TestUtility testUtility = new TestUtility();




    @Rule //creates and launches the activity, once for each method
    public ActivityTestRule<Auth> authTestRule = new  ActivityTestRule<Auth>(Auth.class);

    @Test
    public void testUI() {
        Activity activity = authTestRule.getActivity();
        Assert.assertNotNull(activity.findViewById(R.id.field_email));
    }

    @Test
    public void loginTest() {
        Activity activity = authTestRule.getActivity();
        onView(withId(R.id.field_email)).perform(typeText(correctEmailAddress), closeSoftKeyboard());
        onView(withId(R.id.field_password)).perform(typeText(correctPassword), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        testUtility.waiter(5000);// wait for login
        onView(withId(R.id.newShot)).check(matches(isDisplayed()));
    }


}
