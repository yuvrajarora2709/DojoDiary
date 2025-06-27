package com.yuvraj.dojodiary

import android.adservices.adid.AdId
import android.provider.ContactsContract.CommonDataKinds.Organization
import java.sql.Date
import java.sql.Time
import java.time.MonthDay

data class PostDataModel(
    val organization: String?=null,
    val hours: String?=null,
    val day: String?=null,
    val date: String?=null,
    val time: String?=null,
    val postId: String?=null

)
