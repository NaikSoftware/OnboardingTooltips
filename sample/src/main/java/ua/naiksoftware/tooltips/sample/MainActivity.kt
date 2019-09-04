package ua.naiksoftware.tooltips.sample

import android.graphics.Color
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import ua.naiksoftware.tooltips.TooltipOverlayPopup
import ua.naiksoftware.tooltips.TooltipOverlayParams
import ua.naiksoftware.tooltips.TooltipPosition
import ua.naiksoftware.tooltips.TooltipView
import ua.naiksoftware.tooltips.sample.ui.main.SectionsPagerAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var fab: FloatingActionButton
    private var popup : TooltipOverlayPopup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        fab = findViewById(R.id.fab)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val tooltipView = TooltipView(this, "Test tooltip")
        val density = resources.displayMetrics.density
        tooltipView.setPadding(
            (density * 160).toInt(),
            (density * 4).toInt(),
            (density * 16).toInt(),
            (density * 4).toInt()
        )
        tooltipView.setBubbleColor(Color.BLUE)
        tooltipView.setTextColor(Color.WHITE)

        fab.doOnPreDraw {
            popup = TooltipOverlayPopup()
            popup?.show(
                TooltipOverlayParams(tooltipView, fab)
                    .setDismissOnTouchAnchor(true)
                    .setAnchorClickable(true)
                    .setDismissOnTouchOverlay(false)
//                    .setDismissOnTouchOutside(true)
                    .withTransparentOverlay(true)
                    .withTooltipPosition(TooltipPosition.TOP),
                this
            )

//            popup?.dismissAsync(5000)
        }
    }

    override fun onDestroy() {
        popup?.dismiss()
        super.onDestroy()
    }
}