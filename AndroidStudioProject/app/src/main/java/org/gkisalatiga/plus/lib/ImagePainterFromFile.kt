/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * This class allows one to display an external image in Kotlin Jetpack Composable
 * without external dependencies such as Coil and Glide.
 * ---
 * Load an ImageBitmap from a specific path, then convert it into a Painter
 * that can be loaded in a composable Image element.
 * SOURCE: https://developer.android.com/develop/ui/compose/graphics/images/custompainter
 */

package org.gkisalatiga.plus.lib

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import java.io.File
import kotlin.math.roundToInt

/**
 * @param imagePath the absolute path to the image that will be displayed.
 */
class ImagePainterFromFile constructor(private val imagePath: String) : Painter() {
    // Load the image path as a bitmap, then convert it into an ImageBitmap.
    // SOURCE: https://slack-chats.kotlinlang.org/t/510573/hi-there-is-there-a-way-to-convert-bitmap-into-imagebitmap-i#dd5fc826-2d22-4a3b-94f7-d07784ad09a5
    private val imgFile = File(imagePath)
    private val imgBitmap = (BitmapFactory.decodeFile(imgFile.absolutePath) as Bitmap).asImageBitmap()

    // Get the parsed image bitmap's offset and size for displaying in the screen.
    val srcOffset: IntOffset = IntOffset.Zero
    val srcSize: IntSize = IntSize(imgBitmap.width, imgBitmap.height)

    override fun DrawScope.onDraw() {
        drawImage(
            imgBitmap,
            srcOffset,
            srcSize,
            dstSize = IntSize(
                this@onDraw.size.width.roundToInt(),
                this@onDraw.size.height.roundToInt()
            )
        )
    }

    // Back-end functions and declarations that we shouldn't be concerned about.
    // SOURCE: https://developer.android.com/develop/ui/compose/graphics/images/custompainter
    override val intrinsicSize: Size get() = size.toSize()
    private val size: IntSize = validateSize(srcOffset, srcSize)
    private fun validateSize(srcOffset: IntOffset, srcSize: IntSize): IntSize {
        require(
            srcOffset.x >= 0 &&
                    srcOffset.y >= 0 &&
                    srcSize.width >= 0 &&
                    srcSize.height >= 0 &&
                    srcSize.width <= imgBitmap.width &&
                    srcSize.height <= imgBitmap.height
        )
        return srcSize
    }

}