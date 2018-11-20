package com.influx.influxtask.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.PathShape
import android.util.AttributeSet
import android.view.*
import android.widget.*
import com.influx.influxtask.R

class ExpandableGridView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    GridView(context, attrs, defStyle) {

    private var mLayoutParams: WindowManager.LayoutParams? = null
    private var mCoverView: LinearLayout? = null
    private var mParentViewGroup: ViewGroup? = null
    private var hasScrolled = false
    private var scrollYY = 0
    private var mListener: OnExpandItemClickListener? = null
    private lateinit var superChild: LinearLayout

    /**
     * Set listener for sub grid view item. When sub grid view item is clicked, it will invoke
     * the listener's onItemClick function.
     *
     * @param listener
     */
    fun setOnExpandItemClickListener(listener: OnExpandItemClickListener) {
        mListener = listener
    }

    /**
     * Expand the grid view under the clicked item.
     * @param clickedView The clicked item.
     * @param expandGridAdapter Adapter set to sub grid view.
     */
    fun expandGridViewAtView(clickedView: View) {

        mCoverView = LinearLayout(context)
        mCoverView!!.orientation = LinearLayout.VERTICAL

        val imageViewUp = ImageView(context)
        val imageViewDown = ImageView(context)
        val middleView = ScrollView(context)

        val touchBottom = clickedView.bottom
        if (touchBottom > measuredHeight - paddingBottom - verticalSpacing) {
            hasScrolled = true
            scrollYY = touchBottom - measuredHeight + paddingBottom + verticalSpacing / 2
            scrollBy(0, scrollYY)
        }
        this.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(this.drawingCache)
        this.destroyDrawingCache()

        var heightUp = 1
        var heightDown = 1
        val middleViewHeight = clickedView.height + 20
        val bottomPos = bitmap.height - touchBottom - verticalSpacing / 2 - middleViewHeight
        var upY = 0
        var downY = touchBottom


        if (bottomPos <= 0) {
            heightUp = touchBottom + verticalSpacing / 2 - middleViewHeight
            upY = middleViewHeight
            heightDown = bitmap.height - heightUp - middleViewHeight
            if (heightDown < 0) {
                heightUp += heightDown
                heightDown = paddingBottom
                heightUp -= heightDown
            }
            downY = upY + heightUp
        } else {
            heightUp = touchBottom + verticalSpacing / 2
            heightDown = bottomPos
        }
        val bitmapUp = Bitmap.createBitmap(bitmap, 0, upY, bitmap.width, heightUp)
        val bitmapDown = Bitmap.createBitmap(bitmap, 0, downY, bitmap.width, heightDown)

        imageViewUp.setImageBitmap(bitmapUp)
        imageViewUp.setOnClickListener { collapseGridView() }
        imageViewDown.setImageBitmap(bitmapDown)
        imageViewDown.setOnClickListener { collapseGridView() }

        val linearLayout = LinearLayout(context)

        // The below code is for adding the layout for expandable
        val inflater = LayoutInflater.from(context)
        val previewLayout = inflater.inflate(R.layout.preview_layout, null, false)
        val btn = previewLayout.findViewById<RelativeLayout>(R.id.btnOpenPanel)
        val btnBuy = previewLayout.findViewById<Button>(R.id.btnBuyTickets)
        superChild = previewLayout.findViewById(R.id.super_child)
        btn.setOnClickListener { mListener?.onPreviewClick() }
        btnBuy.setOnClickListener { mListener?.onBuyTicketClick() }

        linearLayout.addView(previewLayout)

        middleView.addView(linearLayout)
        val touchX = clickedView.left + columnWidth / 2
        val touchY = heightUp
        val canvas = Canvas(bitmapUp)
        val path = Path()
        path.moveTo((touchX - 15).toFloat(), touchY.toFloat())
        path.lineTo((touchX + 15).toFloat(), touchY.toFloat())
        path.lineTo(touchX.toFloat(), (touchY - 15).toFloat())
        path.lineTo((touchX - 15).toFloat(), touchY.toFloat())
        val circle = ShapeDrawable(PathShape(path, width.toFloat(), height.toFloat()))
        circle.paint.color = Color.WHITE
        circle.setBounds(0, 0, width, height)
        circle.draw(canvas)

//        val params = ViewGroup.LayoutParams(width, middleViewHeight)
        val params = ViewGroup.LayoutParams(width, LayoutParams.WRAP_CONTENT)

        middleView.layoutParams = params
        middleView.setBackgroundColor(Color.WHITE)

        mCoverView!!.addView(imageViewUp)
        mCoverView!!.addView(middleView)
        mCoverView!!.addView(imageViewDown)

        mParentViewGroup = parent as ViewGroup
        mLayoutParams = WindowManager.LayoutParams()
        mLayoutParams!!.format = PixelFormat.TRANSLUCENT
        mLayoutParams!!.gravity = Gravity.TOP or Gravity.LEFT
        mLayoutParams!!.x = left
        mLayoutParams!!.y = top
        mLayoutParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        mLayoutParams!!.height = WindowManager.LayoutParams.WRAP_CONTENT
        mLayoutParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        mParentViewGroup!!.addView(mCoverView, 0, mLayoutParams)
        mCoverView!!.bringToFront()

    }

    fun showPreviewView() {
        superChild.visibility = View.VISIBLE
    }


    /**
     * Collapse the grid view and remove the cover layer
     */
    fun collapseGridView() {
        if (mParentViewGroup != null && mCoverView != null) {
            mCoverView!!.removeAllViews()
            mParentViewGroup!!.removeView(mCoverView)
            mLayoutParams = null
            mCoverView = null
            mParentViewGroup = null
        }
        if (hasScrolled) {
            scrollBy(0, -scrollYY)
            hasScrolled = false
            scrollYY = 0
        }
    }

    /**
     * Sub item click listener interface
     */
    interface OnExpandItemClickListener {
        fun onItemClick(position: Int, clickPositionData: Any)
        fun onPreviewClick()
        fun onBuyTicketClick()
    }

}
