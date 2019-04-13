package pavanjdot.com.pokemongo

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    val USER_LOCATION_REQUEST_CODE = 1000
    private var playerLoaction: Location? = null
    private var oldLocationOfPlayer: Location? = null
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null

    private var pokemonCharacter: ArrayList<PokemonCharacter> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = PlayerLocationListner()

        requestLocationPermission()
        initializePokemonCharacter()
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
        //17.338993, 76.7698991



    }

    //ask permission
    private fun requestLocationPermission() {

        if(Build.VERSION.SDK_INT>=23) {
            if(ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(android.Manifest.permission.
                    ACCESS_FINE_LOCATION), USER_LOCATION_REQUEST_CODE)

                return
            }

        }

        accessUserLocation()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {


        if(requestCode == USER_LOCATION_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

                accessUserLocation()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    inner class PlayerLocationListner: LocationListener {

        constructor(){
            playerLoaction = Location("MyProvider")
            playerLoaction?.latitude = 0.0
            playerLoaction?.longitude = 0.0
        }

        override fun onLocationChanged(updatedLocation: Location?) {


            playerLoaction = updatedLocation

        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) { TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }


    }

    private fun initializePokemonCharacter() {

        pokemonCharacter.add(PokemonCharacter("Hello this is c1",
            "I'am powerfull", R.drawable.c1, 1.6517729, 31.996134 )
        )

        pokemonCharacter.add(PokemonCharacter("Hello this is C2",
            "Im Pwoerfull", R.drawable.c2, 27.404523, 29.647654))

        pokemonCharacter.add(PokemonCharacter("Hello this is C3",
            "Im Pwoerfull", R.drawable.c3, 10.492703, 10.709112))

        pokemonCharacter.add(PokemonCharacter("Hello this is C4",
            "Im Pwoerfull", R.drawable.c4, 28.220750, 1.898764))
    }

    private fun accessUserLocation() {
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            1, 2f, locationListener!!)

        var newThread = NewThread()
        newThread.start()
    }

    inner class NewThread: Thread {

        constructor(): super(){

            oldLocationOfPlayer = Location("My Provider")
            oldLocationOfPlayer?.latitude = 0.0
            oldLocationOfPlayer?.longitude = 0.0

        }

        override fun run() {
            super.run()


            while(true) {
                if(oldLocationOfPlayer?.distanceTo(playerLoaction) == 0f){

                    continue
                }

                oldLocationOfPlayer = playerLoaction

                try {


                    runOnUiThread {

                        mMap.clear()

                        val pLocation = LatLng(playerLoaction!!.latitude, playerLoaction!!.longitude)
                        mMap.addMarker(MarkerOptions().position(pLocation).title("Hi, I am the Player")
                                .snippet("Let's go")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.player)))
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(pLocation))

                        for (pokemonCharacterIndex in 0.until(pokemonCharacter.size)){

                            // 0, 1, 2, 3
                            var pc = pokemonCharacter[pokemonCharacterIndex]
                            if(pc.isKilled == false){

                                var pcLocation = LatLng(pc.location!!.latitude, pc.location!!.longitude)
                                mMap.addMarker(MarkerOptions()
                                    .position(pcLocation)
                                    .title(pc.titleOfPokemon)
                                    .snippet(pc.message)
                                    .icon(BitmapDescriptorFactory.fromResource(pc.iconOfPokemon!!)))


                                if (playerLoaction!!.distanceTo(pc.location) < 1) {

                                    Toast.makeText(this@MapsActivity,
                                        "${pc.titleOfPokemon} is eliminated",
                                        Toast.LENGTH_SHORT).show()
                                    pc.isKilled = true
                                    pokemonCharacter[pokemonCharacterIndex] = pc
                                }
                            }



                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }




        }

    }
}


