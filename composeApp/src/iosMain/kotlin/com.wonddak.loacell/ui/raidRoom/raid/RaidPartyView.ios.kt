package com.wonddak.loacell.ui.raidRoom.raid

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import platform.Foundation.NSData
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import kotlinx.cinterop.*
import platform.CoreGraphics.*
import platform.Foundation.*
import platform.LinkPresentation.LPLinkMetadata
import platform.UIKit.*
import platform.posix.memcpy

actual fun shareImage(bitmap: ImageBitmap?) {
    bitmap?.let {
        val uiImage = it.toUIImage() ?: return
        // Convert UIImage to NSData (e.g., PNG format)
        val pngData = UIImagePNGRepresentation(uiImage) ?: return

        // Create a temporary file URL
        val tempDir = platform.Foundation.NSTemporaryDirectory()
        val tempUrl = NSURL.fileURLWithPath("$tempDir/shared_image.png")

        // Write the PNG data to the temporary file
        pngData.writeToURL(tempUrl, true)


        val metadata = LPLinkMetadata()
        metadata.apply {
            title = "이미지 공유"
            originalURL = tempUrl
        }
        // Set up the share sheet using UIActivityViewController
        val shareVC = UIActivityViewController(
            activityItems = listOf(tempUrl), // Share the temporary image URL
            applicationActivities = null
        )

        shareVC.excludedActivityTypes = listOf(
            UIActivityTypeAirDrop,
            UIActivityTypeMail
        )

        UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
            shareVC,
            animated = true,
            null
        )
    }
}

private fun UIImage.toNSData(): NSData? {
    return UIImageJPEGRepresentation(this, 0.8)
}

@OptIn(ExperimentalForeignApi::class)
fun ImageBitmap.toUIImage(): UIImage? {
    val width = this.width
    val height = this.height
    val buffer = IntArray(width * height)

    this.readPixels(buffer)

    val colorSpace = CGColorSpaceCreateDeviceRGB()
    val context = CGBitmapContextCreate(
        data = buffer.refTo(0),
        width = width.toULong(),
        height = height.toULong(),
        bitsPerComponent = 8u,
        bytesPerRow = (4 * width).toULong(),
        space = colorSpace,
        bitmapInfo = CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value
    )

    val cgImage = CGBitmapContextCreateImage(context)
    return cgImage?.let { UIImage.imageWithCGImage(it) }
}