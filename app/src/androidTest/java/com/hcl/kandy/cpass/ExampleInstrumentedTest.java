package com.hcl.kandy.cpass;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.hcl.kandy.cpass.activities.LoginActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {


    // @Rule
    //public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    private String url = "oauth-cpaas.att.com";
    private String uName = "karang@hcl.com";
    private String pwd = "Test@12345";
    private String client = "PUB-karan.prod.lwjn";
    private String destination = "ashish@gmail.com";
    private String destinationSMS = "+911234567890";
    private String destinationAddress = "abc@gmail.com";
    private String participant_address = "abc@gmail.com";

    @Test
    public void verifyLoginModule() {

        ActivityScenario.launch(LoginActivity.class);
        // Types a message into a EditText element.
        onView(withId(R.id.et_url))
                .perform(typeText(url), closeSoftKeyboard());
        onView(withId(R.id.et_user_name))
                .perform(typeText(uName), closeSoftKeyboard());
        onView(withId(R.id.et_user_password))
                .perform(typeText(pwd), closeSoftKeyboard());
        onView(withId(R.id.et_user_client))
                .perform(typeText(client), closeSoftKeyboard());

        onView(withId(R.id.button_login)).perform(click());

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void verifyChatMessage() {

        ActivityScenario.launch(LoginActivity.class);
        // Types a message into a EditText element.
        onView(withId(R.id.et_url))
                .perform(typeText(url), closeSoftKeyboard());
        onView(withId(R.id.et_user_name))
                .perform(typeText(uName), closeSoftKeyboard());
        onView(withId(R.id.et_user_password))
                .perform(typeText(pwd), closeSoftKeyboard());
        onView(withId(R.id.et_user_client))
                .perform(typeText(client), closeSoftKeyboard());

        onView(withId(R.id.button_login)).perform(click());

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.container)).check(matches(isDisplayed()));

        onView(withId(R.id.etDestainationAddress))
                .perform(typeText(destination), closeSoftKeyboard());
        onView(withId(R.id.btnFetchChat)).perform(click());

        onView(withId(R.id.etMessage))
                .perform(typeText("hi"), closeSoftKeyboard());
        onView(withId(R.id.btnStartChat)).perform(click());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void verifySMS() {

        ActivityScenario.launch(LoginActivity.class);
        // Types a message into a EditText element.
        onView(withId(R.id.et_url))
                .perform(typeText(url), closeSoftKeyboard());
        onView(withId(R.id.et_user_name))
                .perform(typeText(uName), closeSoftKeyboard());
        onView(withId(R.id.et_user_password))
                .perform(typeText(pwd), closeSoftKeyboard());
        onView(withId(R.id.et_user_client))
                .perform(typeText(client), closeSoftKeyboard());

        // Clicks a button to send the message to another
        // activity through an explicit intent.
        onView(withId(R.id.button_login)).perform(click());

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.container)).check(matches(isDisplayed()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open()); // Open Drawer

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_sms));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("+12102424496")).perform(click());
        onView(withId(R.id.etDestainationAddress))
                .perform(typeText(destinationSMS), closeSoftKeyboard());
        onView(withId(R.id.etMessage))
                .perform(typeText("hello"), closeSoftKeyboard());
        onView(withId(R.id.btnStartSMS)).perform(click());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void verifyMultimedia() {

        ActivityScenario.launch(LoginActivity.class);
        // Types a message into a EditText element.
        onView(withId(R.id.et_url))
                .perform(typeText(url), closeSoftKeyboard());
        onView(withId(R.id.et_user_name))
                .perform(typeText(uName), closeSoftKeyboard());
        onView(withId(R.id.et_user_password))
                .perform(typeText(pwd), closeSoftKeyboard());
        onView(withId(R.id.et_user_client))
                .perform(typeText(client), closeSoftKeyboard());

        // Clicks a button to send the message to another
        // activity through an explicit intent.
        onView(withId(R.id.button_login)).perform(click());

        // Verifies that the DisplayMessageActivity received an intent
        // with the correct package name and message.

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.container)).check(matches(isDisplayed()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open()); // Open Drawer

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_multimedia));

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.etDestainationAddress))
                .perform(typeText(destinationAddress), closeSoftKeyboard());

        onView(withId(R.id.btnFetchChat)).perform(click());
        onView(withId(R.id.etMessage))
                .perform(typeText("hello"), closeSoftKeyboard());

        onView(withId(R.id.btnSendChat)).perform(click());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void verifyCall() {

        ActivityScenario.launch(LoginActivity.class);
        // Types a message into a EditText element.
        onView(withId(R.id.et_url))
                .perform(typeText(url), closeSoftKeyboard());
        onView(withId(R.id.et_user_name))
                .perform(typeText(uName), closeSoftKeyboard());
        onView(withId(R.id.et_user_password))
                .perform(typeText(pwd), closeSoftKeyboard());
        onView(withId(R.id.et_user_client))
                .perform(typeText(client), closeSoftKeyboard());

        // Clicks a button to send the message to another
        // activity through an explicit intent.
        onView(withId(R.id.button_login)).perform(click());

        // Verifies that the DisplayMessageActivity received an intent
        // with the correct package name and message.

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.container)).check(matches(isDisplayed()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open()); // Open Drawer

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_call));

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.participant_address))
                .perform(clearText(), typeText(participant_address), closeSoftKeyboard());

        onView(withId(R.id.start_call_button)).perform(click());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.activeCallHangupButton)).perform(click());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void verifyAddressbook() {

        ActivityScenario.launch(LoginActivity.class);
        // Types a message into a EditText element.
        onView(withId(R.id.et_url))
                .perform(typeText(url), closeSoftKeyboard());
        onView(withId(R.id.et_user_name))
                .perform(typeText(uName), closeSoftKeyboard());
        onView(withId(R.id.et_user_password))
                .perform(typeText(pwd), closeSoftKeyboard());
        onView(withId(R.id.et_user_client))
                .perform(typeText(client), closeSoftKeyboard());

        // Clicks a button to send the message to another
        // activity through an explicit intent.
        onView(withId(R.id.button_login)).perform(click());

        // Verifies that the DisplayMessageActivity received an intent
        // with the correct package name and message.

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.container)).check(matches(isDisplayed()));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open()); // Open Drawer

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_addressbook));
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.recycleView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.et_last_name))
                .perform(typeText("abcd"), closeSoftKeyboard());
        onView(withId(R.id.button_create_contact)).perform(click());
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(isRoot()).perform(ViewActions.pressBack());
    }

    @Test
    public void verifyGroupChat() {

        ActivityScenario.launch(LoginActivity.class);
        // Types a message into a EditText element.
        onView(withId(R.id.et_url))
                .perform(typeText(url), closeSoftKeyboard());
        onView(withId(R.id.et_user_name))
                .perform(typeText(uName), closeSoftKeyboard());
        onView(withId(R.id.et_user_password))
                .perform(typeText(pwd), closeSoftKeyboard());
        onView(withId(R.id.et_user_client))
                .perform(typeText(client), closeSoftKeyboard());

        // Clicks a button to send the message to another
        // activity through an explicit intent.
        onView(withId(R.id.button_login)).perform(click());

        // Verifies that the DisplayMessageActivity received an intent
        // with the correct package name and message.

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.container)).check(matches(isDisplayed()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open()); // Open Drawer
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_group_chat));

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // onView(withId(R.id.groupList))
        //       .perform(ListVie.actionOnItemAtPosition(0, click()));
        onData(anything()).inAdapterView(withId(R.id.groupList)).atPosition(0).perform(click());
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.messageEditText))
                .perform(typeText("abcd"), closeSoftKeyboard());
        onView(withId(R.id.sendMessage)).perform(click());
    }
/*
    @Test
    public void verifyMessageSentToMessageActivity() {

        ActivityScenario.launch(LoginActivity.class);
        // Types a message into a EditText element.
        onView(withId(R.id.et_url))
                .perform(typeText(url), closeSoftKeyboard());
        onView(withId(R.id.et_user_name))
                .perform(typeText(uName), closeSoftKeyboard());
        onView(withId(R.id.et_user_password))
                .perform(typeText(pwd), closeSoftKeyboard());
        onView(withId(R.id.et_user_client))
                .perform(typeText(client), closeSoftKeyboard());

        // Clicks a button to send the message to another
        // activity through an explicit intent.
        onView(withId(R.id.button_login)).perform(click());

        // Verifies that the DisplayMessageActivity received an intent
        // with the correct package name and message.

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.container)).check(matches(isDisplayed()));

        onView(withId(R.id.etDestainationAddress))
                .perform(typeText(destination), closeSoftKeyboard());
        onView(withId(R.id.btnFetchChat)).perform(click());

        onView(withId(R.id.etMessage))
                .perform(typeText("hi"), closeSoftKeyboard());
        onView(withId(R.id.btnStartChat)).perform(click());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open()); // Open Drawer

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_sms));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("+12102424496")).perform(click());
        onView(withId(R.id.etDestainationAddress))
                .perform(typeText(destinationSMS), closeSoftKeyboard());
        onView(withId(R.id.etMessage))
                .perform(typeText("hello"), closeSoftKeyboard());
        onView(withId(R.id.btnStartSMS)).perform(click());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open()); // Open Drawer

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_multimedia));

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.etDestainationAddress))
                .perform(typeText(destinationAddress), closeSoftKeyboard());

        onView(withId(R.id.btnFetchChat)).perform(click());
        onView(withId(R.id.etMessage))
                .perform(typeText("hello"), closeSoftKeyboard());

        onView(withId(R.id.btnSendChat)).perform(click());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open()); // Open Drawer

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_call));

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.participant_address))
                .perform(clearText(), typeText(participant_address), closeSoftKeyboard());

        onView(withId(R.id.start_call_button)).perform(click());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.activeCallHangupButton)).perform(click());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open()); // Open Drawer

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_addressbook));
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.recycleView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.et_last_name))
                .perform(typeText("abcd"), closeSoftKeyboard());
        onView(withId(R.id.button_create_contact)).perform(click());
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(isRoot()).perform(ViewActions.pressBack());

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open()); // Open Drawer
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_group_chat));

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // onView(withId(R.id.groupList))
        //       .perform(ListVie.actionOnItemAtPosition(0, click()));
        onData(anything()).inAdapterView(withId(R.id.groupList)).atPosition(0).perform(click());
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.messageEditText))
                .perform(typeText("abcd"), closeSoftKeyboard());
        onView(withId(R.id.sendMessage)).perform(click());
    }
*/
}
