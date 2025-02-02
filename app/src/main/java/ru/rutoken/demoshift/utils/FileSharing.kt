/*
 * Copyright (c) 2020, Aktiv-Soft JSC.
 * See the LICENSE file at the top-level directory of this distribution.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.bouncycastle.signature.CmsSigner
import java.io.File

fun copyAssetToCache(filename: String, context: Context) {
    File(context.cacheDir, "/$filename").outputStream()
        .use { output -> context.assets.open(filename).use { it.copyTo(output) } }
}

fun shareFileAndSignature(fileUri: Uri, signature: String, context: Context): Intent {
    val signatureFile = File(context.cacheDir, "/signature.sig")
    signatureFile.outputStream().use { it.write(signature.toByteArray()) }

    val signatureUri =
        FileProvider.getUriForFile(context, "ru.rutoken.demoshift.fileprovider", signatureFile)
    val uris = arrayListOf(fileUri, signatureUri)

    val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = "*/*"
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
    }

    return Intent.createChooser(intent, context.getString(R.string.share_result))
}