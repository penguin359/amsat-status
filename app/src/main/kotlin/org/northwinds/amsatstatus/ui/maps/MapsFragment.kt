/**********************************************************************************
 * Copyright (c) 2021 Loren M. Lang                                               *
 *                                                                                *
 * Permission is hereby granted, free of charge, to any person obtaining a copy   *
 * of this software and associated documentation files (the "Software"), to deal  *
 * in the Software without restriction, including without limitation the rights   *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell      *
 * copies of the Software, and to permit persons to whom the Software is          *
 * furnished to do so, subject to the following conditions:                       *
 *                                                                                *
 * The above copyright notice and this permission notice shall be included in all *
 * copies or substantial portions of the Software.                                *
 *                                                                                *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR     *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,       *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE    *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER         *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  *
 * SOFTWARE.                                                                      *
 **********************************************************************************/

package org.northwinds.amsatstatus.ui.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory.*
import org.northwinds.amsatstatus.R
import org.northwinds.amsatstatus.Report
import org.northwinds.amsatstatus.databinding.FragmentMapsBinding
import org.northwinds.amsatstatus.ui.dashboard.DashboardViewModel
import org.northwinds.amsatstatus.util.Locator


class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentMapsBinding
    private val ALICE_SPRINGS = LatLng(-24.6980, 133.8807)
    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        dashboardViewModel =
            ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        binding = FragmentMapsBinding.inflate(layoutInflater)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            ?.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val nameView: Spinner = binding.root.findViewById(R.id.name)
        nameView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                dashboardViewModel.empty()
            }

            override fun onItemSelected(
                p0: AdapterView<*>?,
                view: View?,
                position: Int,
                _id: Long,
            ) {
                //val spinner = view as Spinner
                //val id = spinner.selectedItemPosition
                val id = position
                val satellite_ids =
                    resources.getStringArray(R.array.satellite_ids)
                /*
                Toast.makeText(
                    activity!!.applicationContext,
                    "Sat Item " + position.toString() + " (" + satellite_ids[id] + ")",
                    Toast.LENGTH_LONG
                ).show()
                */
                dashboardViewModel.update(satellite_ids[id])
            }
        }
//        val reportView: RecyclerView = root.findViewById(R.id.reports)
//        dashboardViewModel.reports.observe(viewLifecycleOwner, Observer {
//            with(reportView) {
//                adapter = MyReportRecyclerViewAdapter(it)
//            }
//        })

        val prefs = PreferenceManager(context).sharedPreferences
        val satHeard =
            prefs.getString(requireContext().getString(R.string.preference_satellite), "")
        val idx = requireContext().resources.getStringArray(R.array.satellite_ids).indexOf(satHeard)
        if (idx >= 0)
            nameView.setSelection(idx)

        return binding.root
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
////        mMap.addMarker(MarkerOptions().position(sydney).icon(vectorToBitmap(android.R.drawable.ic_menu_myplaces, Color.parseColor("#A4C639"))).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        // HUE_CYAN
        // HUE_VIOLET
        // HUE_YELLOW
        // HUE_RED
        // HUE_ORANGE
//        mMap.addMarker(
//            MarkerOptions()
//                .position(ALICE_SPRINGS)
//                .icon(BitmapDescriptorFactory.defaultMarker((5.0 * 360 / 8.0).toFloat()))
//                .title("Alice Springs")
//        )
        dashboardViewModel.reports.observe(viewLifecycleOwner, Observer {
            val bounds = LatLngBounds.builder()
            var count = 0
            mMap.clear()
            for (item in it) {
                try {
                    val pos = Locator.grid_to_coord(item.gridSquare)
                    val hue = when (item.report) {
                        Report.HEARD -> HUE_CYAN
                        Report.TELEMETRY_ONLY -> HUE_YELLOW
                        Report.CREW_ACTIVE -> HUE_VIOLET
                        Report.NOT_HEARD -> HUE_RED
                        Report.CONFLICTED -> 0.0f
                    }
                    mMap.addMarker(
                        MarkerOptions().position(LatLng(pos.latitude, pos.longitude))
                            .icon(BitmapDescriptorFactory.defaultMarker(hue)).title(item.callsign)
                    )
                    bounds.include(LatLng(pos.latitude, pos.longitude))
                    count++
                } catch (ex: RuntimeException) {
                }
            }
            //center the map to a specific spot (city)
//            mMap.
//            mMap.setCenter(LatLng(0.0, 0.0))
            if (count < 1)
                return@Observer
            val bounded = bounds.build()
            //center the map to the geometric center of all markers
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounded, 25))
//            mMap.setCenter(bounds.build().getCenter());
//            mMap.fitBounds(bounds);
//            mMap.cameraPosition = bounds.build().getCenter()
//
//            //remove one zoom level to ensure no marker is on the edge.
//            mMap.setZoom(mMap.getZoom()-1);
//
//            // set a minimum zoom
//            // if you got only 1 marker or all markers are on the same address map will be zoomed too much.
//            if(mMap.getZoom()> 15){
//                mMap.setZoom(15);
//            }
        })
    }
}
