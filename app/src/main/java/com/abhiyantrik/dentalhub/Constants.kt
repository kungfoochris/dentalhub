package com.abhiyantrik.dentalhub

class Constants {

    companion object {
        @JvmStatic
        val CONTENT_TYPE_TEXT = "text/plain"
        @JvmStatic
        val CONTENT_TYPE_IMAGE = "image/*"
        @JvmStatic
        val CONTENT_TYPE_VIDEO = "video/*"

        @JvmStatic
        val PREF_AUTH_TOKEN = "AUTH-TOKEN"
        @JvmStatic
        val PREF_AUTH_EMAIL = "AUTH-EMAIL"
        @JvmStatic
        val PREF_AUTH_PASSWORD = "AUTH-PASSWORD"
        @JvmStatic
        val PREF_AUTH_SOCIAL = "AUTH-SOCIAL"

        @JvmStatic
        val PREF_SETUP_COMPLETE = "SETUP_COMPLETE"

        @JvmStatic
        val PREF_SELECTED_LOCATION_NAME = "SELECTED_LOCATION_NAME"
        @JvmStatic
        val PREF_SELECTED_LOCATION_ID = "SELECTED_LOCATION_ID"
        @JvmStatic
        val PREF_ACTIVITY_ID = "ACTIVITY_ID"
        @JvmStatic
        val PREF_ACTIVITY_NAME = "ACTIVITY_NAME"
        @JvmStatic
        val PREF_ACTIVITY_REMARKS = "ACTIVITY_REMARKS"

        @JvmStatic
        val LOCATION_REQUEST = 1011
        @JvmStatic
        val GPS_REQUEST = 1012
    }


}