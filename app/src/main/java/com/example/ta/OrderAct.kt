package com.example.ta


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.ta.Adapter.CartAdapter
import com.example.ta.Adapter.CheckoutAdapter
import com.example.ta.Model.*
import com.example.ta.Model.Url_Volley.Companion.url_website
import com.example.ta.utilss.Tools
import com.example.ta.utilss.UserSessionManager
import kotlinx.android.synthetic.main.activity_order.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class OrderAct : AppCompatActivity() {

    lateinit var user:UserInfo
    lateinit var session: UserSessionManager
    var list = ArrayList<MKeranjang>()
    var st:Int = 0
    var banyak = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        get_total_cart()
        session = UserSessionManager(applicationContext)

        initToolbar()
        user = session.userDetails

        var url = url_website+"/udemy/get_cart.php?user_id="+user.id.toString()
        var rq: RequestQueue = Volley.newRequestQueue(this)


        var jar= JsonArrayRequest(Request.Method.GET,url,null, Listener { response ->

            //            UserInfo.jumlahCart = response.length()
            for (x in 0 .. response.length()-1)
            {
                list.add(
                    MKeranjang(
                        response.getJSONObject(x).getString("produk_id"),
                        response.getJSONObject(x).getString("judul_produk"),
                        response.getJSONObject(x).getInt("harga_diskon"),
                        response.getJSONObject(x).getInt("total_qty"),
                        response.getJSONObject(x).getString("foto"),
                        response.getJSONObject(x).getString("foto_type"),
                        "",
                        response.getJSONObject(x).getInt("stok")
                    )
                )
                if(response.getJSONObject(x).getInt("stok")==0)
                {
                    st=1
                }
////                MTotalCart.total_harga += (response.getJSONObject(x).getInt("harga_diskon") * response.getJSONObject(x).getInt("total_qty"))
//               var temp = (response.getJSONObject(x).getInt("harga_diskon") * response.getJSONObject(x).getInt("total_qty"))
////                Log.e("Hitung", (response.getJSONObject(x).getInt("harga_diskon") * response.getJSONObject(x).getInt("total_qty")).toString())
//                Log.e("Hitung", temp.toString())
//                kosong += temp
            }

            var adp= CartAdapter(this,list)
            cart_rv.layoutManager= LinearLayoutManager(this)
            cart_rv.adapter=adp

        }, Response.ErrorListener { error ->
            Log.e("ErrorCart", error.message.toString())
            Toast.makeText(this,error.message, Toast.LENGTH_LONG).show()
        })
        rq.add(jar)
        get_total_cart()
        if(st!=1 && list.size > 0 ||banyak>0||MTotalCart.total_cart>0||list.count()>0 )
        {
            btn_checkout.setOnClickListener{
                var i = Intent(this,CheckoutAct::class.java)
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }

        }
        else {

            if (list.size == 0 ||banyak == 0||MTotalCart.total_cart == 0)
            {
//                Toast.makeText(this, "Keranjang anda kosong", Toast.LENGTH_LONG).show()
            }
            if(st == 1)
            {
                Toast.makeText(this, "Hapus produk yang tidak tersedia", Toast.LENGTH_LONG).show()
            }
        }


        Log.e("Berat", MTotalCart.total_berat.toString())
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

    fun get_total_cart(){
        var locale = Locale("in", "ID")
        var formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(locale)
        var url1 = url_website +"/udemy/get_total_cart.php?user_id="+ MCart.user_id
        var rq1: RequestQueue = Volley.newRequestQueue(this)
        var jor = JsonObjectRequest(Request.Method.GET,url1,null, Listener { response ->
            //            cart_size.text = response.getInt("banyak").toString()
           banyak =  response.getInt("banyak")
            MTotalCart.total_cart = response.getInt("banyak")
            MTotalCart.total_harga = response.getInt("jumlah")
            MTotalCart.total_berat = response.getInt("berat")
        }, Response.ErrorListener { error ->
            Toast.makeText(this,error.message, Toast.LENGTH_LONG).show()
        })
        rq1.add(jor)
        txt_total.setText(formatRupiah.format(MTotalCart.total_harga))
    }

    private fun initToolbar() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Keranjang Belanja")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Tools.setSystemBarColor(this, R.color.grey_10)
        Tools.setSystemBarLight(this)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_setting, menu)
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

}
