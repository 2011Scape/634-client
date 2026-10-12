import io.IOException

/*
 * Not part of the original client: the browser side of decodeJpegPixels (milestone 4). There is no
 * synchronous JPEG decoder here; the IOException sends JpegSpriteDecoder down its Toolkit path.
 */
actual fun decodeJpegPixels(imageData: ByteArray): JpegPixels? {
	throw IOException("no raw JPEG decoder in a browser")
}
