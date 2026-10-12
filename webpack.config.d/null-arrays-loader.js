// Not part of the original client: a webpack loader for the browser bundle (milestone 5).
//
// Kotlin/JS compiles arrayOfNulls(n) (and the storage of Array(n) { ... }) to a bare JS `Array(n)`,
// whose slots are undefined until written. Kotlin's == treats undefined as null, but === does not,
// so game code that compares an unwritten slot by identity with a null reference (GlToolkit's
// setActiveTexture: openglTextures[unit] !== texture) sees two different values where the JVM sees
// null and null. This loader makes every bare `Array(n)` in the compiled Kotlin start out filled
// with null, as a JVM array does, so identity checks agree on both platforms.
module.exports = function (source) {
    const replaced = source.replace(/(?<![\w.$])Array\(/g, '$nullArray(')
    if (replaced === source) return source
    return 'var $nullArray = function (n) { var a = new Array(n); a.fill(null); return a; };\n' + replaced
}
