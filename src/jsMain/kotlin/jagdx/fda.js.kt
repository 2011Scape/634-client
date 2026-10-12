/* Not part of the original client: the browser side of jagdx's exception (milestone 4). */
package jagdx

actual class fda : RuntimeException {
	actual constructor() : super()
	actual constructor(string: String?) : super(string)
}
