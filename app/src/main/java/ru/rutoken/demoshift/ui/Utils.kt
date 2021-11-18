package ru.rutoken.demoshift.ui

import android.content.Context
import android.text.Layout.Alignment.ALIGN_NORMAL
import android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE
import android.text.style.AlignmentSpan
import android.text.style.RelativeSizeSpan
import androidx.core.text.buildSpannedString
import ru.rutoken.demoshift.R

fun Context.buildWaitingTokenString() = buildSpannedString {
        append(getText(R.string.connect_token), RelativeSizeSpan(1.3f), SPAN_INCLUSIVE_EXCLUSIVE)
        append(getText(R.string.waiting_token), AlignmentSpan.Standard(ALIGN_NORMAL), SPAN_INCLUSIVE_EXCLUSIVE)
}