package com.tolaotesanya.healthpad.activities.auth;

import com.tolaotesanya.healthpad.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> main = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void checkloginBtn(){
        Espresso.onView(ViewMatchers.withId(R.id.login_button)).perform(ViewActions.click());
    }

}