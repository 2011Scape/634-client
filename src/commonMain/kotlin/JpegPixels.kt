/*
 * Not part of the original client: the cache's JPEGs carry four channels, Y, Cb, Cr and alpha, with
 * no marker naming the colour space, which current JDKs cannot decode through the Toolkit path of
 * JpegSpriteDecoder (milestone 3 moved the decoder that reads them raw behind this function).
 */
class JpegPixels(val width: Int, val pixels: IntArray, val height: Int)

/**
 * The ARGB pixels of the JPEG `imageData`, or null when it decodes to an empty image; throws
 * IOException or RuntimeException when it cannot decode it, and the caller falls back to the
 * Toolkit path.
 */
expect fun decodeJpegPixels(imageData: ByteArray): JpegPixels?
