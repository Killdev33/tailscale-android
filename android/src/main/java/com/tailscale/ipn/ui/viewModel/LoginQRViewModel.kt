// Copyright (c) Tailscale Inc & AUTHORS
// SPDX-License-Identifier: BSD-3-Clause

package com.tailscale.ipn.ui.viewModel

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.tailscale.ipn.ui.notifier.Notifier
import com.tailscale.ipn.ui.util.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginQRViewModel : IpnViewModel() {

  val qrCode: StateFlow<ImageBitmap?> = MutableStateFlow(null)

  init {
    viewModelScope.launch {
      Notifier.browseToURL.collect { url ->
        url?.let { qrCode.set(generateQRCode(url, 200, 0)) } ?: run { qrCode.set(null) }
      }
    }
  }

  fun generateQRCode(content: String, size: Int, padding: Int): ImageBitmap? {
    val qrCodeWriter = QRCodeWriter()

    val encodeHints = mapOf<EncodeHintType, Any?>(EncodeHintType.MARGIN to padding)

    val bitmapMatrix =
        try {
          qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size, encodeHints)
        } catch (ex: WriterException) {
          return null
        }

    val qrCode =
        Bitmap.createBitmap(
            size,
            size,
            Bitmap.Config.ARGB_8888,
        )

    for (x in 0 until size) {
      for (y in 0 until size) {
        val shouldColorPixel = bitmapMatrix?.get(x, y) ?: false
        val pixelColor = if (shouldColorPixel) Color.BLACK else Color.WHITE
        qrCode.setPixel(x, y, pixelColor)
      }
    }

    return qrCode.asImageBitmap()
  }
}
