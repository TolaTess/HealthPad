package com.tolaotesanya.healthpad.activities.auth;

import android.content.Context;

import com.tolaotesanya.healthpad.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AuthActivityTest {

       /*@Test
    public void checkEmailField(){
        Espresso.onView(ViewMatchers.withId(R.id.login_email)).perform(ViewActions.typeText("cagie"));
    }

    @Test
    public void checkPasswordField(){
Espresso.onView(ViewMatchers.withId(R.id.login_password)).perform(ViewActions.typeText("password"));
    }*/

    @Rule
    public ActivityTestRule<AuthActivity> main = new ActivityTestRule<>(AuthActivity.class);


    @Test
    public void checkLoginBtn(){
        Espresso.onView(ViewMatchers.withId(R.id.login_btn)).perform(ViewActions.click());
    }

    @Test
    public void checkRegBtn(){
        Espresso.onView(ViewMatchers.withId(R.id.reg_btn)).perform(ViewActions.click());
    }

}