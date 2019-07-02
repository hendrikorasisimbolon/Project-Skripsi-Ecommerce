package com.example.ta

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.ta.Adapter.CheckoutAdapter
import com.example.ta.Adapter.CityAdapter
import com.example.ta.Adapter.ExpedisiAdapter
import com.example.ta.Api.ApiServiceRO
import com.example.ta.Api.ApiUrl
import com.example.ta.Fragment.DeliveryFragment
import com.example.ta.Fragment.UpdateAddressFragment
import com.example.ta.Model.*
import com.example.ta.Model.cost.ItemCost
import com.example.ta.Model.expedisi.ItemExpedisi
import com.example.ta.utils.Tools
import com.example.ta.utils.UserSessionManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_checkout.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class CheckoutAct : AppCompatActivity() {


    private var alert: AlertDialog.Builder? = null
    private var ad: AlertDialog? = null

    lateinit var user: UserInfo
    lateinit var session: UserSessionManager
    var list = ArrayList<MKeranjang>()

    private val listItemExpedisi = java.util.ArrayList<ItemExpedisi>()
    private var adapter_expedisi: ExpedisiAdapter? = null
    private var progressDialog: ProgressDialog? = null
    private var mListView: ListView? = null
    private var adapter_city: CityAdapter? = null
    private var searchList: EditText? = null


    companion object{
        var listEkspedisi : ArrayList<MService> = ArrayList()
        var eksp:String = ""
        var service:Int = -1
        var harga_servicesblm : Int = 0
        var hasil_service:String =""
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        session = UserSessionManager(applicationContext)

        initToolbar()

        user = session.userDetails


        var url = Url_Volley.url_website +"/udemy/get_cart.php?user_id="+user.id.toString()
        var rq: RequestQueue = Volley.newRequestQueue(this)


        var jar= JsonArrayRequest(Request.Method.GET,url,null, Response.Listener { response ->

            //            UserInfo.jumlahCart = response.length()
            for (x in 0..response.length() - 1) {
                list.add(
                    MKeranjang(
                        response.getJSONObject(x).getString("produk_id"),
                        response.getJSONObject(x).getString("judul_produk"),
                        response.getJSONObject(x).getInt("harga_diskon"),
                        response.getJSONObject(x).getInt("total_qty"),
                        response.getJSONObject(x).getString("foto"),
                        response.getJSONObject(x).getString("foto_type")
                    )
                )
            }

            var adp = CheckoutAdapter(this, list)
            rv_cart.layoutManager = LinearLayoutManager(this)
            rv_cart.adapter = adp

        }, Response.ErrorListener { error ->
            Toast.makeText(this,error.message, Toast.LENGTH_LONG).show()
        })
        rq.add(jar)
        get_total_cart()
        txtalamat_cart.text =
            user.address + ", " + user.nama_kota + System.getProperty("line.separator") + user.nama_provinsi + System.getProperty("line.separator") + "(+62) "+ user.phone

        getCoast("278",user.id_kota,MTotalCart.total_berat.toString(),"pos")
        getCoast("278",user.id_kota,MTotalCart.total_berat.toString(),"tiki")
        getCoast("278",user.id_kota,MTotalCart.total_berat.toString(),"jne")

        btn_edit_prof.setOnClickListener {
            var obj = UpdateAddressFragment()
            var mann = this.fragmentManager
            obj.show(mann, "Srv")
        }

        btn_service.setOnClickListener {
            var obj = DeliveryFragment()
            var mann = this.fragmentManager
            obj.show(mann, "Srv")
        }

        if (service > 0)
        {
            hasil_service.text = listEkspedisi[service].kode+ " "+ listEkspedisi[service].service + System.getProperty("line.separator")+
                                "Harga Rp."+ listEkspedisi[service].tarif.toString()+ System.getProperty("line.separator")+ "Estimasi : "+
                                listEkspedisi[service].estimasi+ " hari"
        }
        listEkspedisi.clear()
    }

    override fun onStart() {
        get_total_cart()
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                try {
                    recreate()
                } catch (e: Exception) {}
                handler.postDelayed(this, 300000)
            }
        }
        handler.postDelayed(runnable, 300000)
        super.onStart()
    }

    override fun onResume() {
        get_total_cart()
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                try {
                    recreate()
                } catch (e: Exception) {}
                handler.postDelayed(this, 300000)
            }
        }
        handler.postDelayed(runnable, 300000)
        super.onResume()
    }

    @SuppressLint("SetTextI18n")
    fun get_total_cart(){
        var locale = Locale("in", "ID")
        var formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(locale)
        var url1 = Url_Volley.url_website +"/udemy/get_total_cart.php?user_id="+ MCart.user_id
        var rq1: RequestQueue = Volley.newRequestQueue(this)
        var jor = JsonObjectRequest(Request.Method.GET,url1,null, Response.Listener { response ->
            //            cart_size.text = response.getInt("banyak").toString()
            Log.e("Banyak Cart", response.getString("banyak"))
            MTotalCart.total_cart = response.getInt("banyak")
            MTotalCart.total_harga = response.getInt("jumlah")
            MTotalCart.total_berat = response.getInt("berat")
        }, Response.ErrorListener { error ->
            Toast.makeText(this,error.message, Toast.LENGTH_LONG).show()
        })
        rq1.add(jor)
        total_harga_checkout.text = formatRupiah.format(MTotalCart.total_harga)
        total_berat.text = MTotalCart.total_berat.toString() + " gram"
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Keranjang Belanja"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Tools.setSystemBarColor(this, R.color.grey_10)
        Tools.setSystemBarLight(this)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_done, menu)
        Tools.changeMenuIconColor(menu, resources.getColor(R.color.grey_60))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else {
            Toast.makeText(applicationContext, item.title, Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getExpedisi() {

        var itemItemExpedisi = ItemExpedisi()

        itemItemExpedisi = ItemExpedisi("1", "pos")
        listItemExpedisi.add(itemItemExpedisi)
        itemItemExpedisi = ItemExpedisi("2", "tiki")
        listItemExpedisi.add(itemItemExpedisi)
        itemItemExpedisi = ItemExpedisi("3", "jne")
        listItemExpedisi.add(itemItemExpedisi)

        mListView?.adapter = adapter_expedisi

        adapter_expedisi?.setList(listItemExpedisi)
        adapter_expedisi?.filter("")

    }

    private inner class MyTextWatcherCity(private val view: View) : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(s: CharSequence, i: Int, before: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            when (view.id) {
                R.id.searchItem -> adapter_city!!.filter(editable.toString())
            }
        }
    }


    fun getCoast(origin: String, destination: String, weight: String, courier: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiUrl.URL_ROOT_HTTPS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiServiceRO::class.java)
        val call = service.getCost(
            "4abd53cc25919616cfc9a21ae55168f1",
            origin,
            destination,
            weight,
            courier
        )
        call.enqueue(object : Callback<ItemCost> {
            override fun onResponse(call: Call<ItemCost>, response: retrofit2.Response<ItemCost>) {
                Log.v("wow", "json : " + Gson().toJson(response))
                progressDialog?.dismiss()
                if (response.isSuccessful) {
                    val statusCode = response.body()!!.rajaongkir!!.status!!.code
                    if (statusCode == 200) {
                        val a = response.body()!!.rajaongkir!!.results
                        if (a != null) {
                            for (i in 0..a.size-1){
                                var b = response.body()!!.rajaongkir!!.results!![i].costs
                                if (b != null) {
                                    for (j in 0..b.size-1){
                                        var c =  response.body()!!.rajaongkir!!.results!![i].code!!.toUpperCase()+" "+
                                                response.body()!!.rajaongkir!!.results!![i].costs!![j].service!!.toUpperCase() + System.getProperty("line.separator")+
                                                "Tarif : "+ response.body()!!.rajaongkir!!.results!![i].costs!![j].cost!![0].value!!.toString() + System.getProperty("line.separator")+
                                                "Estimasi waktu sampai : "+ response.body()!!.rajaongkir!!.results!![i].costs!![j].cost!![0].etd+ " hari"
                                        listEkspedisi.add(
                                            MService(
                                                response.body()!!.rajaongkir!!.results!![i].code!!.toUpperCase(),
                                                response.body()!!.rajaongkir!!.results!![i].costs!![j].service!!.toUpperCase(),
                                                response.body()!!.rajaongkir!!.results!![i].costs!![j].cost!![0].value!!.toInt(),
                                                response.body()!!.rajaongkir!!.results!![i].costs!![j].cost!![0].etd!!.toString()
                                            )
                                        )
                                        Log.e("IsiEks", c)

                                    }
                                }

                            }
                        }



                    } else {

                        val message = response.body()!!.rajaongkir!!.status!!.description
                        Toast.makeText(this@CheckoutAct, message, Toast.LENGTH_SHORT).show()
                    }

                } else {
                    val error = "Error Retrive Data from Server !!!"
                    Toast.makeText(this@CheckoutAct, error, Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<ItemCost>, t: Throwable) {

                progressDialog?.dismiss()
                Toast.makeText(this@CheckoutAct, "Message : Error " + t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }

}