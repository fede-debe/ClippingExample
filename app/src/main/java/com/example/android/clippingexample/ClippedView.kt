package com.example.android.clippingexample

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View

/** At the same level as MainActivity.kt, create a new Kotlin file and class for a custom view called
 * ClippedView that extends View. The rest of your work will all be inside this ClippedView.
 * The @JvmOverloads annotation instructs the Kotlin compiler to generate overloads for this function
 * that substitute default parameter values.
 *  */
class ClippedView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /** In ClippedView define a variable paint of a Paint. Enable anti-aliasing, and use the stroke
     *  width and text size defined in the dimensions.
     * */
    private val paint = Paint().apply {
        // smooth out edges of what is drawn without affecting shape
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        textSize = resources.getDimension(R.dimen.textSize)
    }

    /** create and initialize a path variable of a Path to store locally the path of what has been drawn */
    private val path = Path()

    /** add variables for dimensions for a clipping rectangle around the whole set of shapes. */
    private val clipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private val clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
    private val clipRectTop = resources.getDimension(R.dimen.clipRectTop)
    private val clipRectLeft = resources.getDimension(R.dimen.clipRectLeft)

    /** Add variables for the inset of a rectangle and the offset of a small rectangle */
    private val rectInset = resources.getDimension(R.dimen.rectInset)
    private val smallRectOffset = resources.getDimension(R.dimen.smallRectOffset)

    /** Add a variable for the radius of a circle. This is the circle that is drawn inside the rectangle. */
    private val circleRadius = resources.getDimension(R.dimen.circleRadius)

    /** Add an offset and a text size for text that is drawn inside the rectangle. */
    private val textOffset = resources.getDimension(R.dimen.textOffset)
    private val textSize = resources.getDimension(R.dimen.textSize)

    /** The shapes for this app are displayed in 2 columns and 4 rows determined by the  values of the dimensions you set up */
    /** Set up the coordinates for two columns. */
    private val columnOne = rectInset
    private val columnTwo = columnOne + rectInset + clipRectRight

    /** Add the coordinates for each row, including the final row for the transformed text */
    private val rowOne = rectInset
    private val rowTwo = rowOne + rectInset + clipRectBottom
    private val rowThree = rowTwo + rectInset + clipRectBottom
    private val rowFour = rowThree + rectInset + clipRectBottom
    private val textRow = rowFour + (1.5f * clipRectBottom)
    private val rejectRow = rowFour + rectInset + 2*clipRectBottom // create a variable for the y coordinates of an additional row.

    /** In order to use a rounded rectangle(commonly used shape), create and initialize a rectangle variable. RectF is a class
     * that holds rectangle coordinates in floating point.
     * */
    private var rectF = RectF(
        rectInset,
        rectInset,
        clipRectRight - rectInset,
        clipRectBottom - rectInset
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackAndUnclippedRectangle(canvas)
        drawDifferenceClippingExample(canvas)
        drawCircularClippingExample(canvas)
        drawIntersectionClippingExample(canvas)
        drawCombinedClippingExample(canvas)
        drawRoundedRectangleClippingExample(canvas)
        drawOutsideClippingExample(canvas)
        drawSkewedTextExample(canvas)
        drawTranslatedTextExample(canvas)
        drawQuickRejectExample(canvas)
    }

    /** inside this method set the boundaries of the clipping rectangle for the whole shape.
     * Apply a clipping rectangle that constraints to drawing only a square. This method reduces
     * the region of the screen that future draw operations can write to. It sets the clipping
     * boundaries to be the spatial intersection of the current clipping rectangle and a rectangle
     * passed into clipRect. (there are a lot of variants of this method and they accept different
     * form of regions and allow different operations under clipping rectangle) */
    private fun drawClippedRectangle(canvas: Canvas) {
        canvas.clipRect(
            clipRectLeft,
            clipRectTop,
            clipRectRight,
            clipRectBottom
        )

        /** add code to fill the canvas with white colors */
        canvas.drawColor(Color.WHITE)

        /** change color to red and draw a diagonal line inside the clipping rectangle */
        paint.color = Color.RED
        canvas.drawLine(
            clipRectLeft,
            clipRectTop,
            clipRectRight,
            clipRectBottom,
            paint
        )

        /** draw a circle inside the clipping rectangle */
        paint.color = Color.GREEN
        canvas.drawCircle(
            circleRadius,
            clipRectBottom - circleRadius,
            circleRadius,
            paint
        )

        /** set the color to blue and draw text aligned with the right edge of the clipping rectangle.
         * use canvas draw text to draw the text */
        paint.color = Color.BLUE
        //align the RIGHT side of the text with the origin.
        paint.textSize = textSize
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(
            context.getString(R.string.clipping),
            clipRectRight,
            textOffset,
            paint
        )
    } // this is the whole canvas, because we are not drawing rectangles, we are clipping. Because of the clipping rectangle,
    // only the region defined by the clipping rectangle is felt, creating a white rectangle and the rest of the surface will
    // remain gray.

    /** To see the drawClippedRectangle() method in action, draw the first unclipped rectangle by implementing the drawUnclippedRectangle()
     *  method as shown below. Save the canvas. Translate to the first row and column position. Draw by calling drawClippedRectangle().
     *  Then restore the canvas to its previous state.
     *  */
    private fun drawBackAndUnclippedRectangle(canvas: Canvas) {
        canvas.drawColor(Color.GRAY)
        canvas.save()
        canvas.translate(columnOne,rowOne)
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    /** Save the canvas. Translate the origin of the canvas into open space to the first row, second column, to the right of the first
     *  rectangle. Apply two clipping rectangles. The DIFFERENCE operator subtracts the second rectangle from the first one. Note: The
     *  method clipRect(float, float, float, float, Region.Op.DIFFERENCE) was deprecated in API level 26. The recommended alternative
     *  method is clipOutRect(float, float, float, float), which is currently available in API level 26 and higher. So be sure to check
     *  the SDK version and use the appropriate API. Call the drawClippedRectangle() method to draw the modified canvas. Restore the
     *  canvas state.
     *  */
    private fun drawDifferenceClippingExample(canvas: Canvas) {
        canvas.save()
        // Move the origin to the right for the next rectangle.
        canvas.translate(columnTwo,rowOne)
        // Use the subtraction of two clipping rectangles to create a frame.
        canvas.clipRect(
            2 * rectInset, 2 * rectInset,
            clipRectRight - 2 * rectInset,
            clipRectBottom - 2 * rectInset
        )
        // The method clipRect(float, float, float, float, Region.Op
        // .DIFFERENCE) was deprecated in API level 26. The recommended
        // alternative method is clipOutRect(float, float, float, float),
        // which is currently available in API level 26 and higher. -> same fot clipOutPath
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            canvas.clipRect(
                4 * rectInset,4 * rectInset,
                clipRectRight - 4 * rectInset,
                clipRectBottom - 4 * rectInset,
                Region.Op.DIFFERENCE
            )
        else {
            canvas.clipOutRect(
                4 * rectInset,4 * rectInset,
                clipRectRight - 4 * rectInset,
                clipRectBottom - 4 * rectInset
            )
        }
        drawClippedRectangle(canvas)
        canvas.restore()
    }
    /** It follows the same pattern as the previous method. Draw a rectangle that uses a circular clipping path. It means removing
     * the circle and showing the gray background instead. */
    private fun drawCircularClippingExample(canvas: Canvas) {

        canvas.save()
        canvas.translate(columnOne, rowTwo)
        // Clears any lines and curves from the path but unlike reset(),
        // keeps the internal data structure for faster reuse.
        path.rewind()
        // add a circle for clipping
        path.addCircle(
            circleRadius,clipRectBottom - circleRadius,
            circleRadius,Path.Direction.CCW
        )
        // IF/ELSE for Deprecated method(CHECKING FOR THE VERSIONS).The method clipPath(path, Region.Op.DIFFERENCE)
        // was deprecated in API level 26. The recommended alternative method is
        // clipOutPath(Path), which is currently available in
        // API level 26 and higher.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipPath(path, Region.Op.DIFFERENCE)
        } else {
            canvas.clipOutPath(path)
        }
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    /** Draw the intersection between 2 clipping rectangles in the second row and column. Depending on the screen resolution, the looks of this region will vary.
     *  You can experiment a small rect offset dimension to change the size of the visible region. */
    private fun drawIntersectionClippingExample(canvas: Canvas) {

        canvas.save()
        canvas.translate(columnTwo,rowTwo)
        canvas.clipRect(
            clipRectLeft,clipRectTop,
            clipRectRight - smallRectOffset,
            clipRectBottom - smallRectOffset
        )
        // The method clipRect(float, float, float, float, Region.Op
        // .INTERSECT) was deprecated in API level 26. The recommended
        // alternative method is clipRect(float, float, float, float), which
        // is currently available in API level 26 and higher.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipRect(
                clipRectLeft + smallRectOffset,
                clipRectTop + smallRectOffset,
                clipRectRight,clipRectBottom,
                Region.Op.INTERSECT // INTERSECTION
            )
        } else {
            canvas.clipRect(
                clipRectLeft + smallRectOffset,
                clipRectTop + smallRectOffset,
                clipRectRight,clipRectBottom
            ) // intersecting rectangles
        }
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    /** Combine shapes, a circle and a rectangle, and draw any path to define a clipping region. */
    private fun drawCombinedClippingExample(canvas: Canvas) {

        canvas.save()
        canvas.translate(columnOne, rowThree)
        path.rewind()
        path.addCircle(
            clipRectLeft + rectInset + circleRadius,
            clipRectTop + circleRadius + rectInset,
            circleRadius,Path.Direction.CCW
        )
        path.addRect(
            clipRectRight / 2 - circleRadius,
            clipRectTop + circleRadius + rectInset,
            clipRectRight / 2 + circleRadius,
            clipRectBottom - rectInset,Path.Direction.CCW
        )
        canvas.clipPath(path) // we are just clipping the path, we don't need to check different versions of the clipping method.
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    /** Add a Rounded rectangle which is a commonly used clipping shape. addRoundRect method takes a rectangle(rectF), X and Y values for
     * the corner radius and the  directions to wind the round rectangles contour. Path.Direction specifies how close shapes are oriented
     * when they are added to a path, and CCW stands for CounterClockWise.
     * */
    private fun drawRoundedRectangleClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnTwo,rowThree)
        path.rewind()
        path.addRoundRect(
            rectF,clipRectRight / 4,
            clipRectRight / 4, Path.Direction.CCW
        )
        canvas.clipPath(path)
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    /** Here we clip outside of the rectangle by doubling the insets of the clipping rectangle*/
    private fun drawOutsideClippingExample(canvas: Canvas) {

        canvas.save()
        canvas.translate(columnOne,rowFour)
        canvas.clipRect(2 * rectInset,2 * rectInset,
            clipRectRight - 2 * rectInset,
            clipRectBottom - 2 * rectInset)
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    /** Drawing text is not really different from any other shapes and you can apply transformations to text. You can translate texts by translating
     *  the  canvas and drawing the text.
     *  */
    private fun drawTranslatedTextExample(canvas: Canvas) {

        canvas.save()
        paint.color = Color.GREEN
        // Align the RIGHT side of the text with the origin.
        paint.textAlign = Paint.Align.LEFT
        // Apply transformation to canvas.
        canvas.translate(columnTwo,textRow)
        // Draw text.
        canvas.drawText(context.getString(R.string.translated),
            clipRectLeft,clipRectTop,paint)
        canvas.restore()
    }

    /** You can also skew text, distort it in various ways. When you use View classes provided by the Android system,
     * the system clips views for you to minimize overdraw. When you use custom View classes and override the onDraw()
     * method, clipping what you draw becomes your responsibility.
     * */
    private fun drawSkewedTextExample(canvas: Canvas) {

        canvas.save()
        paint.color = Color.YELLOW
        paint.textAlign = Paint.Align.RIGHT
        // Position text.
        canvas.translate(columnTwo, textRow)
        // Apply skew transformation.
        canvas.skew(0.2f, 0.3f)
        canvas.drawText(context.getString(R.string.skewed),
            clipRectLeft, clipRectTop, paint)
        canvas.restore()
    }
    private fun drawQuickRejectExample(canvas: Canvas) {

        val inClipRectangle = RectF(clipRectRight / 2,
            clipRectBottom / 2,
            clipRectRight * 2,
            clipRectBottom * 2)

        val notInClipRectangle = RectF(RectF(clipRectRight+1,
            clipRectBottom+1,
            clipRectRight * 2,
            clipRectBottom * 2))

        canvas.save()
        canvas.translate(columnOne, rejectRow)
        canvas.clipRect(
            clipRectLeft,clipRectTop,
            clipRectRight,clipRectBottom
        )
        if (canvas.quickReject(
                inClipRectangle, Canvas.EdgeType.AA)) { // you can also use notInClipRectangle to change the output
            canvas.drawColor(Color.WHITE)
        }
        else {
            canvas.drawColor(Color.BLACK)
            canvas.drawRect(inClipRectangle, paint
            )
        }
        canvas.restore()
    }
}