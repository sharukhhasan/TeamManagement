package com.sharukhhasan.teammanagement.util;

import com.sharukhhasan.teammanagement.activity.AdminListActivity;
import com.sharukhhasan.teammanagement.activity.ManagerListActivity;
import com.sharukhhasan.teammanagement.activity.UserListActivity;

/**
 * Created by sharukhhasan on 6/7/16.
 */
public class Constants {
    final public static String SHARED_PREFERENCES_NAME = "TimeManagmentPrefs";

    // TODO: YOUR_FIREBASE_URL_GOES_HERE , for example: "https://YOURPROJECTNAME.firebaseIO.com";
    final public static String FIREBASE_URL = "https://timemanagement-59954.firebaseio.com/";

    final public static String FIREBASE_USERS = "users";
    final public static String FIREBASE_USERS_TIME = "usersTime";
    final public static String FIREBASE_MANAGERS = "managers";
    final public static String FIREBASE_ADMIN = "admin";
    final public static String FIREBASE_REPORT_DATES = "reportDates";

    final public static String DATE_FORMAT = "yyyy-MM-dd";

    final public static String HOURS_MIN = "1";
    final public static String HOURS_MAX = "24";

    final public static int LEVEL_USER = 0;
    final public static int LEVEL_MANAGER = 1;
    final public static int LEVEL_ADMIN = 2;

    final public static int SELECT_FOR_PROMOTE_OR_DEMOTE = 0;
    final public static int SELECT_TO_ADD_USERS = 1;
    final public static int SELECT_FROM_ADMIN = 2;

    final public static String[] LEVEL_TO_STRING = {"User", "Manager", "Admin"};
    final public static Class[] LEVEL_TO_ACTIVITY = { UserListActivity.class, ManagerListActivity.class, AdminListActivity.class};
}
