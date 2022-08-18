package com.chargemap.extensions

import java.lang.Math.abs

/**
 * Function designed to return the element nth places away from a given index. Ideal use for a number picker displaying
 * multiple values of a list in one go. If n is outside the bounds of the list, the function will wrap round to the correct
 * location in the list. Eg: currentIndex = 1, n = -4 and list.size = 10, return value will be elementAt(7)
 *
 * Params:
 * n - desired number of indexes away from currentIndex value
 * currentIndex - location to which n is calculated from
 *
 * Returns: <T> - nth number of places away from currentIndex.
 */
fun <T> List<T>.getElementAtOffsetIndexValue(
    n: Int,
    currentIndex: Int,
): T {
    val inBoundsN = getNWithinBounds(n, size)
    return elementAt(when {
        currentIndex + inBoundsN > size - 1 -> currentIndex + inBoundsN - size
        currentIndex + inBoundsN < 0 -> size + currentIndex + inBoundsN
        else -> currentIndex + inBoundsN
    })
}

/**
 * Recursive function to handle finding the Nth value within bounds of the list. If n is greater than the size of the
 * list then the method will be reducing the value of N by the size of the list each time until n is
 * within the bounds of the list. This function must only return a value that is less than the size of the list.
 *
 * Params:
 * n - desired number of indexes away
 * listSize - total number of elements within the list
 *
 *
 * Returns: Int - A value of N that is within the bounds of the list.
 */
fun getNWithinBounds(n: Int, listSize: Int): Int {
    val inRange = if (abs(n) > listSize) {
        n % listSize
    } else {
        n
    }
    return if (inRange >= 0) {
        inRange
    } else if (inRange < 0) {
        listSize + inRange
    } else {
        inRange
    }
}
